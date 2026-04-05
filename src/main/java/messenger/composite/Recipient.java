package messenger.composite;

// Component — общий интерфейс для Leaf и Composite
public interface Recipient {
    // Operation()
    void send(String message);
    String getName();
}