package messenger.state;

/**
 * ConcreteStateD — состояние «Ошибка отправки».
 *
 * Поведение: доставка не удалась (нет соединения и т.д.).
 * Переход: пользователь повторяет → SendingState
 *
 * Аналог TCPClosed из GoF.
 */
public class FailedState implements MessageState {

    private final String reason;

    public FailedState(String reason) {
        this.reason = reason;
    }

    @Override
    public void handle(MessageContext context) {
        System.out.println("[FailedState] Сообщение " + context.getMessageId()
                + " не доставлено. Причина: " + reason);
        // Повтор отправки — переход обратно в SendingState
        System.out.println("[FailedState] Повтор отправки...");
        context.setState(new SendingState());
        context.request();
    }

    @Override public String getDisplayName() { return "Ошибка: " + reason; }
    @Override public String getStatusIcon()  { return "✕"; }
}