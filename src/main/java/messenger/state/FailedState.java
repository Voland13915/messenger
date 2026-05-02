package messenger.state;

public class FailedState implements MessageState {

    private final String reason;

    public FailedState(String reason) {
        this.reason = reason;
    }

    @Override
    public void handle(MessageContext context) {
        System.out.println("[FailedState] Сообщение " + context.getMessageId()
                + " не доставлено. Причина: " + reason);
        System.out.println("[FailedState] Повтор отправки...");
        context.setState(new SendingState());
        context.request();
    }

    @Override public String getDisplayName() { return "Ошибка: " + reason; }
    @Override public String getStatusIcon()  { return "✕"; }
}