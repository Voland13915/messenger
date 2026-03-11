package messenger.factorymethod;

public class ImageMessageCreator extends MessageCreator {
    @Override
    public Message factoryMethod(String data) { return new ImageMessage(data); }
}