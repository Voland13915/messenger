package messenger.singleton;

import messenger.observer.MessengerObserver;
import messenger.observer.ObserverEvent;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebSocketManager {

    private static volatile WebSocketManager instance;

    public static WebSocketManager getInstance() {
        if (instance == null) {
            synchronized (WebSocketManager.class) {
                if (instance == null) instance = new WebSocketManager();
            }
        }
        return instance;
    }

    private WebSocketManager() {}

    private final List<String> onlineUsers = new ArrayList<>();
    private boolean connected  = false;
    private String  myUsername = null;

    private final List<MessengerObserver> observers = new CopyOnWriteArrayList<>();

    private WebSocketClient client;

    public void attach(MessengerObserver observer) {
        if (!observers.contains(observer)) observers.add(observer);
    }

    public void detach(MessengerObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(ObserverEvent event, Object data) {
        for (MessengerObserver o : observers) {
            o.update(event, data);
        }
    }

    private String lastServerUrl = null;

    public void connect(String serverUrl, String username) {
        this.lastServerUrl = serverUrl;
        this.myUsername = username;
        try {
            client = new WebSocketClient(new URI(serverUrl)) {

                @Override
                public void onOpen(ServerHandshake handshake) {
                    try {
                        JSONObject reg = new JSONObject();
                        reg.put("type",     "register");
                        reg.put("username", myUsername);
                        send(reg.toString());
                    } catch (Exception e) { e.printStackTrace(); }

                    connected = true;
                    javafx.application.Platform.runLater(() ->
                            notifyObservers(ObserverEvent.CONNECTION_CHANGED, true));
                }

                @Override
                public void onMessage(String rawJson) {
                    try {
                        JSONObject json = new JSONObject(rawJson);
                        String type = json.getString("type");
                        javafx.application.Platform.runLater(() -> {
                            try { processMessage(type, json); }
                            catch (Exception e) { e.printStackTrace(); }
                        });
                    } catch (Exception e) { e.printStackTrace(); }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    connected = false;
                    javafx.application.Platform.runLater(() ->
                            notifyObservers(ObserverEvent.CONNECTION_CHANGED, false));
                }

                @Override
                public void onError(Exception ex) {
                    System.err.println("[WS] Ошибка: " + ex.getMessage());
                }

                public void simulateDisconnect() {
                    connected = false;
                    javafx.application.Platform.runLater(() ->
                            notifyObservers(ObserverEvent.CONNECTION_CHANGED, false));
                }

                public void simulateReconnect() {
                    connected = true;
                    javafx.application.Platform.runLater(() ->
                            notifyObservers(ObserverEvent.CONNECTION_CHANGED, true));
                }
            };
            client.connect();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public String getLastServerUrl() { return lastServerUrl; }

    private void processMessage(String type, JSONObject json) throws Exception {
        switch (type) {

            case "registered":
                System.out.println("[WS] Зарегистрирован: " + json.getString("username"));
                notifyObservers(ObserverEvent.CONNECTION_CHANGED, true);
                break;

            case "user_joined": {
                String name = json.getString("username");
                if (!name.equals(myUsername) && !onlineUsers.contains(name))
                    onlineUsers.add(name);
                notifyObservers(ObserverEvent.USER_JOINED, name);
                break;
            }

            case "user_left": {
                String name = json.getString("username");
                onlineUsers.remove(name);
                notifyObservers(ObserverEvent.USER_LEFT, name);
                break;
            }

            case "online_list": {
                onlineUsers.clear();
                json.getJSONArray("users").forEach(u -> {
                    String name = u.toString();
                    if (!name.equals(myUsername)) onlineUsers.add(name);
                });
                notifyObservers(ObserverEvent.USER_JOINED,
                        new ArrayList<>(onlineUsers));
                break;
            }

            case "message":
                notifyObservers(ObserverEvent.MESSAGE_RECEIVED, json);
                break;

            case "read":
                notifyObservers(ObserverEvent.MESSAGE_READ, json.optString("from", ""));
                break;

            case "error":
                System.err.println("[WS] Сервер: " + json.optString("message"));
                break;
        }
    }

    public void sendRead(String originalSender) {
        if (!connected) return;
        try {
            JSONObject json = new JSONObject();
            json.put("type", "read");
            json.put("to",   originalSender);
            client.send(json.toString());
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void send(String to, String text) {
        send(to, text, "TEXT", null, null, null);
    }

    public void send(String to, String text, String msgType,
                     String filePath, String location) {
        send(to, text, msgType, filePath, location, null);
    }

    public void send(String to, String text, String msgType,
                     String filePath, String location, String quote) {
        if (!connected) { System.err.println("[WS] Нет соединения"); return; }
        try {
            JSONObject json = new JSONObject();
            json.put("type",    "message");
            json.put("to",      to);
            json.put("text",    text);
            json.put("msgType", msgType);
            if (filePath != null) json.put("filePath", filePath);
            if (location != null) json.put("location", location);
            if (quote    != null) json.put("quote",    quote);
            client.send(json.toString());
        } catch (Exception e) { e.printStackTrace(); }
    }

    public boolean      isConnected()        { return connected; }
    public String       getMyUsername()      { return myUsername; }
    public int          getConnectionCount() { return onlineUsers.size(); }
    public List<String> getOnlineUsers()     { return new ArrayList<>(onlineUsers); }

    public void disconnect() {
        if (client != null) client.close();
    }
    public void simulateDisconnect() {
        connected = false;
        javafx.application.Platform.runLater(() ->
                notifyObservers(ObserverEvent.CONNECTION_CHANGED, false));
    }

    public void simulateReconnect() {
        connected = true;
        javafx.application.Platform.runLater(() ->
                notifyObservers(ObserverEvent.CONNECTION_CHANGED, true));
    }
}

