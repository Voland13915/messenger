package messenger.decorator;

// ConcreteComponent
public class SimpleMessage implements Message {
    private final String content;
    private final String sender;

    public SimpleMessage(String content, String sender) {
        this.content = content;
        this.sender  = sender;
    }

    @Override public String getContent() { return content; }
    @Override public String getSender()  { return sender; }
}