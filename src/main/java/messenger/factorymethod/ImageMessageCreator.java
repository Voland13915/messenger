package messenger.factorymethod;

/**
 * ConcreteCreator — создаёт сообщения с изображением.
 * content интерпретируется как URL изображения.
 */
public class ImageMessageCreator extends MessageCreator {

    @Override
    public Message createMessage(String sender, String content) {
        return new ImageMessage(sender, content);
    }
}
