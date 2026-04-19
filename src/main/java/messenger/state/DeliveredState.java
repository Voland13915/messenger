package messenger.state;

/**
 * ConcreteStateB — состояние «Доставлено».
 *
 * Поведение: сообщение дошло до получателя (одна галочка).
 * Переход: получатель открыл чат → ReadState
 *
 * Аналог TCPEstablished из GoF.
 */
public class DeliveredState implements MessageState {

    @Override
    public void handle(MessageContext context) {
        System.out.println("[DeliveredState] Сообщение " + context.getMessageId()
                + " доставлено получателю: " + context.getRecipient());
        // Переход в Read происходит когда получатель открывает чат
        // context.setState(new ReadState()) вызывается извне при этом событии
    }

    @Override public String getDisplayName() { return "Доставлено"; }
    @Override public String getStatusIcon()  { return "✓"; }
}