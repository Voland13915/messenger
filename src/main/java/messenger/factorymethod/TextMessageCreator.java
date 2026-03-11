package messenger.factorymethod;

public class TextMessageCreator extends MessageCreator {
    @Override
    public Message factoryMethod(String data) { return new TextMessage(data); }
}