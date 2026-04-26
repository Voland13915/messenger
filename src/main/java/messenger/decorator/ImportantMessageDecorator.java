package messenger.decorator;

public class ImportantMessageDecorator extends MessageDecorator {

    public ImportantMessageDecorator(Message component) {
        super(component);
    }

    @Override
    public String getContent() {
        return "❗ " + component.getContent();
    }
}