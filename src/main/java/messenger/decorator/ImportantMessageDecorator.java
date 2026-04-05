package messenger.decorator;

// ConcreteDecoratorA — добавляет метку важности
public class ImportantMessageDecorator extends MessageDecorator {

    public ImportantMessageDecorator(Message component) {
        super(component);
    }

    // AddedBehavior() — добавляем ❗ перед содержимым
    @Override
    public String getContent() {
        return "❗ " + component.getContent();
    }
}