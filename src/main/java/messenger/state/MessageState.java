package messenger.state;

public interface MessageState {
    void handle(MessageContext context);
    String getDisplayName();
    String getStatusIcon();
}