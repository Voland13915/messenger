package messenger.state;

public class ReadState implements MessageState {

    @Override
    public void handle(MessageContext context) {
        System.out.println("[ReadState] Сообщение " + context.getMessageId()
                + " прочитано получателем: " + context.getRecipient());
    }

    @Override public String getDisplayName() { return "Прочитано"; }
    @Override public String getStatusIcon()  { return "✓✓"; }
}