package server;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Запуск: java -jar messenger-server.jar
 * По умолчанию слушает порт 8887.
 * Все устройства в одной Wi-Fi сети подключаются по адресу:
 *   ws://<IP_компьютера>:8887
 */
public class MessengerServer extends WebSocketServer {

    // socket → username
    private final Map<WebSocket, String> clients = new ConcurrentHashMap<>();

    public MessengerServer(int port) {
        super(new InetSocketAddress(port));
    }

    // ── Клиент подключился ────────────────────────────────────────────
    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        System.out.println("[Server] Новое подключение: " + conn.getRemoteSocketAddress());
    }

    // ── Клиент отключился ─────────────────────────────────────────────
    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        String username = clients.remove(conn);
        System.out.println("[Server] Отключился: " + username);

        // Уведомить всех об уходе
        if (username != null) {
            broadcast(new JSONObject()
                    .put("type", "user_left")
                    .put("username", username)
                    .toString());
        }
    }

    // ── Входящее сообщение ────────────────────────────────────────────
    @Override
    public void onMessage(WebSocket conn, String rawJson) {
        try {
            JSONObject json = new JSONObject(rawJson);
            String type = json.getString("type");

            switch (type) {

                // Клиент представляется
                case "register": {
                    String username = json.getString("username");
                    clients.put(conn, username);
                    System.out.println("[Server] Зарегистрирован: " + username);

                    // Подтверждение отправителю
                    conn.send(new JSONObject()
                            .put("type", "registered")
                            .put("username", username)
                            .toString());

                    // Уведомить всех о новом участнике
                    broadcast(new JSONObject()
                            .put("type", "user_joined")
                            .put("username", username)
                            .toString());

                    // Отправить новому клиенту список онлайн-пользователей
                    conn.send(new JSONObject()
                            .put("type", "online_list")
                            .put("users", clients.values())
                            .toString());
                    break;
                }

                // Обычное сообщение — переслать получателю (или всем)
                case "message": {
                    String from = clients.get(conn);
                    if (from == null) { conn.send(error("Сначала зарегистрируйтесь")); break; }

                    json.put("from", from);
                    System.out.println("[Server] " + from + " → " + json.optString("to", "all")
                            + ": " + json.optString("text", ""));

                    String to = json.optString("to", "");
                    if (to.isEmpty() || to.equals("all")) {
                        // Широковещательная рассылка
                        broadcast(json.toString());
                    } else {
                        // Личное сообщение — найти получателя
                        WebSocket target = findByUsername(to);
                        if (target != null) {
                            target.send(json.toString());
                            conn.send(json.toString()); // эхо отправителю
                        } else {
                            conn.send(error("Пользователь " + to + " не в сети"));
                        }
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

    // ── Найти сокет по имени пользователя ────────────────────────────
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

    // ── Точка входа ───────────────────────────────────────────────────
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8887;
        MessengerServer server = new MessengerServer(port);
        server.start();
        System.out.println("Нажмите Enter для остановки...");
        System.in.read();
        server.stop();
    }
}