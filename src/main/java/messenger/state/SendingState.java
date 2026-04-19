package messenger.state;

/**
 * ConcreteStateA — состояние «Отправляется».
 *
 * Поведение: сообщение передано на сервер, ждём подтверждения.
 * Переход: сервер принял → DeliveredState
 *          сервер недоступен → FailedState
 *
 * Аналог TCPListen из GoF (начальное состояние соединения).
 */
public class SendingState implements MessageState {

    @Override
    public void handle(MessageContext context) {
        System.out.println("[SendingState] Сообщение " + context.getMessageId()
                + " отправляется на сервер...");
        // Имитация: после отправки переходим в Delivered
        // В реальном приложении переход происходит по подтверждению от сервера
        context.setState(new DeliveredState());
    }

    @Override public String getDisplayName() { return "Отправляется"; }
    @Override public String getStatusIcon()  { return "🕐"; }
}