package server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class MessengerServer extends WebSocketServer {

    private final Map<WebSocket, String> clients = new ConcurrentHashMap<>();

    public MessengerServer(int port) {
        super(new InetSocketAddress(port));
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("[Server] Новое подключение: " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String username = clients.remove(conn);
        System.out.println("[Server] Отключился: " + username);

        if (username != null) {
            broadcast(new JSONObject()
                    .put("type", "user_left")
                    .put("username", username)
                    .toString());
        }
    }

    @Override
    public void onMessage(WebSocket conn, String rawJson) {
        try {
            JSONObject json = new JSONObject(rawJson);
            String type = json.getString("type");

            switch (type) {
                case "register": {
                    String username = json.getString("username");
                    clients.put(conn, username);
                    System.out.println("[Server] Зарегистрирован: " + username);

                    conn.send(new JSONObject()
                            .put("type", "registered")
                            .put("username", username)
                            .toString());

                    broadcast(new JSONObject()
                            .put("type", "user_joined")
                            .put("username", username)
                            .toString());

                    conn.send(new JSONObject()
                            .put("type", "online_list")
                            .put("users", clients.values())
                            .toString());
                    break;
                }

                case "message": {
                    String from = clients.get(conn);
                    if (from == null) { conn.send(error("Сначала зарегистрируйтесь")); break; }

                    json.put("from", from);
                    System.out.println("[Server] " + from + " → " + json.optString("to", "all")
                            + ": " + json.optString("text", ""));

                    String to = json.optString("to", "");
                    if (to.isEmpty() || to.equals("all")) {
                        broadcast(json.toString());
                    } else {
                        WebSocket target = findByUsername(to);
                        if (target != null) {
                            target.send(json.toString());
                            conn.send(json.toString());
                        } else {
                            conn.send(error("Пользователь " + to + " не в сети"));
                        }
                    }
                    break;
                }

                case "read": {
                    String reader = clients.get(conn);
                    if (reader == null) break;
                    String originalSender = json.optString("to", "");
                    WebSocket senderSocket = findByUsername(originalSender);
                    if (senderSocket != null) {
                        json.put("from", reader);
                        senderSocket.send(json.toString());
                        System.out.println("[Server] read: " + reader + " прочитал сообщения от " + originalSender);
                    }
                    break;
                }

                default:
                    System.out.println("[Server] Неизвестный тип: " + type);
            }

        } catch (Exception e) {
            System.err.println("[Server] Ошибка разбора: " + e.getMessage());
        }
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        System.err.println("[Server] Ошибка: " + ex.getMessage());
    }

    @Override
    public void onStart() {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  Messenger Server запущен            ║");
        System.out.println("║  Порт: " + getPort() + "                         ║");
        System.out.println("║  Адрес для клиентов:                 ║");
        System.out.println("║  ws://<ВАШ_IP>:" + getPort() + "              ║");
        System.out.println("╚══════════════════════════════════════╝");
    }

    private WebSocket findByUsername(String username) {
        return clients.entrySet().stream()
                .filter(e -> e.getValue().equals(username))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(null);
    }

    private String error(String msg) {
        return new JSONObject().put("type", "error").put("message", msg).toString();
    }

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8887;
        MessengerServer server = new MessengerServer(port);
        server.start();
        System.out.println("Нажмите Enter для остановки...");
        System.in.read();
        server.stop();
    }
}