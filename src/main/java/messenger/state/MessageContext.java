package messenger.state;

public class MessageContext {

    private MessageState state;

    private final String messageId;
    private final String text;
    private final String recipient;

    public MessageContext(String messageId, String text, String recipient) {
        this.messageId = messageId;
        this.text      = text;
        this.recipient = recipient;
        this.state = new SendingState();
        System.out.println("[MessageContext] Создано сообщение " + messageId
                + " → начальное состояние: " + state.getDisplayName());
    }

    public void request() {
        state.handle(this);
    }

    public void setState(MessageState newState) {
        System.out.println("[MessageContext] " + messageId + ": "
                + state.getDisplayName() + " → " + newState.getDisplayName());
        this.state = newState;
    }

    public String getStatusIcon() {
        return state.getStatusIcon();
    }

    public String getDisplayName() { return state.getDisplayName(); }
    public MessageState getState() { return state; }
    public String getMessageId()   { return messageId; }
    public String getText()        { return text; }
    public String getRecipient()   { return recipient; }
}