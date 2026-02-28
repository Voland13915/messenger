package messenger.factorymethod;

/**
 * AbstractProduct — абстрактное сообщение.
 * Объявляет интерфейс для всех типов сообщений.
 */
public abstract class Message {
    protected String sender;
    protected String content;

    public Message(String sender, String content) {
        this.sender = sender;
        this.content = content;
    }

    // Каждый тип сообщения отображается по-своему
    public abstract void display();

    public String getContent() { return content; }
    public String getSender() { return sender; }
}
