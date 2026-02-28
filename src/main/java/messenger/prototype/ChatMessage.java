package messenger.prototype;

/**
 * ConcretePrototype — конкретное сообщение чата.
 * Реализует операцию клонирования себя.
 * При пересылке создаётся копия с новым forwardedBy.
 */
public class ChatMessage implements ForwardableMessage {

    private String originalSender;
    private String content;
    private String timestamp;
    private String forwardedBy; // null если не пересылалось

    public ChatMessage(String originalSender, String content, String timestamp) {
        this.originalSender = originalSender;
        this.content = content;
        this.timestamp = timestamp;
    }

    /**
     * Clone() — возвращает копию самого себя.
     * Клиент получает новый объект, не зная деталей реализации.
     */
    @Override
    public ChatMessage clone() {
        try {
            return (ChatMessage) super.clone(); // shallow copy достаточен для строк
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException("Клонирование не поддерживается", e);
        }
    }

    @Override
    public void setForwardedBy(String user) {
        this.forwardedBy = user;
    }

    @Override
    public void display() {
        System.out.println("  [ChatMessage] " + originalSender + " (" + timestamp + "): \"" + content + "\"");
        if (forwardedBy != null) {
            System.out.println("    ↩ Переслал: " + forwardedBy);
        }
    }

    public String getContent() { return content; }
}
