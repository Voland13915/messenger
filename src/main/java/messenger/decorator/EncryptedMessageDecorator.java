package messenger.decorator;

public class EncryptedMessageDecorator extends MessageDecorator {

    public EncryptedMessageDecorator(Message component) {
        super(component);
    }

    @Override
    public String getContent() {
        return "🔒 " + component.getContent();
    }
}