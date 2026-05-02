package messenger.state;

public class SendingState implements MessageState {

    @Override
    public void handle(MessageContext context) {
        System.out.println("[SendingState] Сообщение " + context.getMessageId()
                + " отправляется на сервер...");
        context.setState(new DeliveredState());
    }

    @Override public String getDisplayName() { return "Отправляется"; }
    @Override public String getStatusIcon()  { return "🕐"; }
}