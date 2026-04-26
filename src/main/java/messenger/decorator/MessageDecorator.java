package messenger.decorator;

public abstract class MessageDecorator implements Message {
    protected final Message component;

    public MessageDecorator(Message component) {
        this.component = component;
    }

    @Override public String getContent() { return component.getContent(); }
    @Override public String getSender()  { return component.getSender(); }
}