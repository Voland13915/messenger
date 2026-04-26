package messenger.composite;

public interface Recipient {
    void send(String message);
    String getName();
}