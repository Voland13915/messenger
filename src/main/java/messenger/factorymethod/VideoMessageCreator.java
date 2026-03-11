package messenger.factorymethod;

public class VideoMessageCreator extends MessageCreator {
    @Override
    public Message factoryMethod(String data) { return new VideoMessage(data); }
}