package messenger.factorymethod;

/**
 * ConcreteCreator — создаёт видеосообщения.
 * content интерпретируется как название видео; длительность фиксирована для демонстрации.
 */
public class VideoMessageCreator extends MessageCreator {

    private final int durationSec;

    public VideoMessageCreator(int durationSec) {
        this.durationSec = durationSec;
    }

    @Override
    public Message createMessage(String sender, String content) {
        return new VideoMessage(sender, content, durationSec);
    }
}
