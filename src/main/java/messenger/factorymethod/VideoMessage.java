package messenger.factorymethod;

/**
 * ConcreteProduct — видеосообщение.
 */
public class VideoMessage extends Message {
    private int durationSec;

    public VideoMessage(String sender, String title, int durationSec) {
        super(sender, title);
        this.durationSec = durationSec;
    }

    @Override
    public void display() {
        System.out.println("[VIDEO] " + sender + ": \"" + content + "\" (" + durationSec + " сек)");
    }
}
