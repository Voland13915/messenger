package messenger.singleton;

import java.util.ArrayList;
import java.util.List;

public class WebSocketManager {

    private static volatile WebSocketManager instance;
    private final List<String> activeConnections = new ArrayList<>();
    private boolean isRunning = false;

    private WebSocketManager() {
        System.out.println("  [WebSocketManager] Инициализация менеджера соединений...");
    }

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

    /*SingletonOperation()*/
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

    /*GetSingletonData()*/
    public int getConnectionCount() {
        return activeConnections.size();
    }

    public boolean isRunning() {
        return isRunning;
    }

    // Демонстрация
    public static void main(String[] args) {
        System.out.println("=== Singleton: WebSocketManager ===");

        WebSocketManager manager1 = WebSocketManager.getInstance();
        WebSocketManager manager2 = WebSocketManager.getInstance();
        manager1.start();
        manager1.connect("alice");
        manager1.connect("bob");
        System.out.println("  Один и тот же экземпляр: " + (manager1 == manager2));
        System.out.println("  Активных соединений: " + manager1.getConnectionCount());
        manager1.disconnect("bob");
        System.out.println("  Активных соединений после отключения: " + manager1.getConnectionCount());
    }
}