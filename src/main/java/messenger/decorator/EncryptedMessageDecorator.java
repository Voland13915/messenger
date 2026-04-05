package messenger.decorator;

// ConcreteDecoratorB — добавляет метку шифрования
public class EncryptedMessageDecorator extends MessageDecorator {

    public EncryptedMessageDecorator(Message component) {
        super(component);
    }

    // AddedBehavior() — добавляем 🔒 перед содержимым
    @Override
    public String getContent() {
        return "🔒 " + component.getContent();
    }
}