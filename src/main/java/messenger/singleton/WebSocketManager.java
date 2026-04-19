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

/**
 * ConcreteSubject (паттерн Observer) + Singleton.
 *
 * subjectState:  onlineUsers, connected
 * GetState():    getOnlineUsers(), isConnected()
 * SetState():    изменяется внутри при событиях WebSocket
 * Notify():      вызывается автоматически после SetState()
 */
public class WebSocketManager {

    // ── Singleton (double-checked locking) ───────────────────────────
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

    // ── subjectState ──────────────────────────────────────────────────
    private final List<String> onlineUsers = new ArrayList<>();
    private boolean connected  = false;
    private String  myUsername = null;

    // ── observers — список наблюдателей ───────────────────────────────
    private final List<MessengerObserver> observers = new CopyOnWriteArrayList<>();

    // ── WebSocket ─────────────────────────────────────────────────────
    private WebSocketClient client;

    // ═══════════════════════════════════════════════════════
    // Subject: Attach / Detach / Notify
    // ═══════════════════════════════════════════════════════

    /** Attach(Observer) */
    public void attach(MessengerObserver observer) {
        if (!observers.contains(observer)) observers.add(observer);
    }

    /** Detach(Observer) */
    public void detach(MessengerObserver observer) {
        observers.remove(observer);
    }

    /** Notify() — для каждого o: o->Update() */
    private void notifyObservers(ObserverEvent event, Object data) {
        for (MessengerObserver o : observers) {
            o.update(event, data);
        }
    }

    // ═══════════════════════════════════════════════════════
    // Подключение к серверу
    // ═══════════════════════════════════════════════════════

    public void connect(String serverUrl, String username) {
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

                    // SetState + Notify
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
            };
            client.connect();
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ── Обработка входящих событий: SetState() → Notify() ────────────
    private void processMessage(String type, JSONObject json) throws Exception {
        switch (type) {

            case "registered":
                System.out.println("[WS] Зарегистрирован: " + json.getString("username"));
                // Уведомляем наблюдателей что соединение готово к работе
                notifyObservers(ObserverEvent.CONNECTION_CHANGED, true);
                break;

            case "user_joined": {
                String name = json.getString("username");
                if (!name.equals(myUsername) && !onlineUsers.contains(name))
                    onlineUsers.add(name);              // SetState
                notifyObservers(ObserverEvent.USER_JOINED, name); // Notify
                break;
            }

            case "user_left": {
                String name = json.getString("username");
                onlineUsers.remove(name);               // SetState
                notifyObservers(ObserverEvent.USER_LEFT, name);   // Notify
                break;
            }

            case "online_list": {
                onlineUsers.clear();
                json.getJSONArray("users").forEach(u -> {
                    String name = u.toString();
                    if (!name.equals(myUsername)) onlineUsers.add(name);
                });
                notifyObservers(ObserverEvent.USER_JOINED,
                        new ArrayList<>(onlineUsers));  // передаём копию
                break;
            }

            case "message":
                notifyObservers(ObserverEvent.MESSAGE_RECEIVED, json);
                break;

            case "error":
                System.err.println("[WS] Сервер: " + json.optString("message"));
                break;
        }
    }

    // ═══════════════════════════════════════════════════════
    // Отправка
    // ═══════════════════════════════════════════════════════

    public void send(String to, String text) {
        send(to, text, "TEXT", null, null);
    }

    public void send(String to, String text, String msgType,
                     String filePath, String location) {
        if (!connected) { System.err.println("[WS] Нет соединения"); return; }
        try {
            JSONObject json = new JSONObject();
            json.put("type",    "message");
            json.put("to",      to);
            json.put("text",    text);
            json.put("msgType", msgType);
            if (filePath != null) json.put("filePath", filePath);
            if (location != null) json.put("location", location);
            client.send(json.toString());
        } catch (Exception e) { e.printStackTrace(); }
    }

    // ═══════════════════════════════════════════════════════
    // GetState()
    // ═══════════════════════════════════════════════════════

    public boolean      isConnected()        { return connected; }
    public String       getMyUsername()      { return myUsername; }
    public int          getConnectionCount() { return onlineUsers.size(); }
    public List<String> getOnlineUsers()     { return new ArrayList<>(onlineUsers); }

    public void disconnect() {
        if (client != null) client.close();
    }
}