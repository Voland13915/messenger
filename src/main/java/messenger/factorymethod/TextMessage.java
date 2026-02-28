package messenger.factorymethod;

/**
 * ConcreteProduct — текстовое сообщение.
 */
public class TextMessage extends Message {

    public TextMessage(String sender, String content) {
        super(sender, content);
    }

    @Override
    public void display() {
        System.out.println("[TEXT] " + sender + ": " + content);
    }
}
