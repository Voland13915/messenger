package messenger.factorymethod;

/**
 * ConcreteCreator — создаёт текстовые сообщения.
 * Замещает фабричный метод, возвращая TextMessage.
 */
public class TextMessageCreator extends MessageCreator {

    @Override
    public Message createMessage(String sender, String content) {
        return new TextMessage(sender, content);
    }
}
