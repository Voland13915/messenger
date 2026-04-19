package messenger.state;

/**
 * Context — контекст (аналог TCPConnection из GoF).
 *
 * - Определяет интерфейс Request() для клиентов
 * - Хранит экземпляр ConcreteState (текущее состояние)
 * - Делегирует зависящие от состояния запросы: state->Handle()
 *
 * Клиенты конфигурируют контекст объектами State.
 * После конфигурации клиенты не должны напрямую
 * связываться с объектами состояния.
 */
public class MessageContext {

    // Context хранит ссылку на текущее состояние
    private MessageState state;

    // Данные сообщения
    private final String messageId;
    private final String text;
    private final String recipient;

    public MessageContext(String messageId, String text, String recipient) {
        this.messageId = messageId;
        this.text      = text;
        this.recipient = recipient;
        // Начальное состояние — отправка
        this.state = new SendingState();
        System.out.println("[MessageContext] Создано сообщение " + messageId
                + " → начальное состояние: " + state.getDisplayName());
    }

    /**
     * Request() — основной интерфейс для клиентов.
     *
     * Context делегирует вызов текущему состоянию:
     *   state->Handle(this)
     *
     * Передаёт себя как аргумент — состояние может
     * при необходимости сменить текущее состояние контекста.
     */
    public void request() {
        state.handle(this);
    }

    /**
     * Установить новое состояние.
     * Вызывается из ConcreteState.handle() при смене состояния.
     */
    public void setState(MessageState newState) {
        System.out.println("[MessageContext] " + messageId + ": "
                + state.getDisplayName() + " → " + newState.getDisplayName());
        this.state = newState;
    }

    /** Получить иконку текущего состояния для UI */
    public String getStatusIcon() {
        return state.getStatusIcon();
    }

    public String getDisplayName() { return state.getDisplayName(); }
    public MessageState getState() { return state; }
    public String getMessageId()   { return messageId; }
    public String getText()        { return text; }
    public String getRecipient()   { return recipient; }
}