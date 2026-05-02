package messenger.state;

public class DeliveredState implements MessageState {

    @Override
    public void handle(MessageContext context) {
        System.out.println("[DeliveredState] Сообщение " + context.getMessageId()
                + " доставлено получателю: " + context.getRecipient());
    }

    @Override public String getDisplayName() { return "Доставлено"; }
    @Override public String getStatusIcon()  { return "✓"; }
}