package messenger.adapter;

// Target — интерфейс которым пользуется Client
public interface Notifier {
    // Request()
    void notify(String message);
}