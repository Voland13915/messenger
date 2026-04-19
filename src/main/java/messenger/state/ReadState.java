package messenger.state;

/**
 * ConcreteStateC — состояние «Прочитано».
 *
 * Поведение: получатель прочитал сообщение (две галочки).
 * Конечное состояние — переходов дальше нет.
 *
 * Аналог TCPEstablished с завершённым обменом.
 */
public class ReadState implements MessageState {

    @Override
    public void handle(MessageContext context) {
        System.out.println("[ReadState] Сообщение " + context.getMessageId()
                + " прочитано получателем: " + context.getRecipient());
        // Конечное состояние — ничего не делаем
    }

    @Override public String getDisplayName() { return "Прочитано"; }
    @Override public String getStatusIcon()  { return "✓✓"; }
}