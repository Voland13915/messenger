package messenger.singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton — глобальный менеджер соединений WebSocket.
 *
 * Гарантирует, что существует ровно один экземпляр менеджера
 * и предоставляет к нему глобальную точку доступа через Instance().
 *
 * Потокобезопасная реализация через double-checked locking.
 */
public class WebSocketManager {

    // static uniqueInstance — единственный экземпляр класса
    private static volatile WebSocketManager instance;

    private final List<String> activeConnections = new ArrayList<>();
    private boolean isRunning = false;

    // Приватный конструктор — запрещает создание объекта извне
    private WebSocketManager() {
        System.out.println("  [WebSocketManager] Инициализация менеджера соединений...");
    }

    /**
     * static Instance() — точка доступа к единственному экземпляру.
     * Double-checked locking для потокобезопасности.
     */
    public static WebSocketManager getInstance() {
        if (instance == null) {
            synchronized (WebSocketManager.class) {
                if (instance == null) {
                    instance = new WebSocketManager();
                }
            }
        }
        return instance;
    }

    public void start() {
        isRunning = true;
        System.out.println("  [WebSocketManager] Сервер запущен.");
    }

    public void connect(String userId) {
        activeConnections.add(userId);
        System.out.println("  [WebSocketManager] Пользователь подключён: " + userId +
                " (всего соединений: " + activeConnections.size() + ")");
    }

    public void disconnect(String userId) {
        activeConnections.remove(userId);
        System.out.println("  [WebSocketManager] Пользователь отключён: " + userId);
    }

    public int getConnectionCount() {
        return activeConnections.size();
    }

    public boolean isRunning() {
        return isRunning;
    }
}
