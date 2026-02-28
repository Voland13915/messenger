package messenger.factorymethod;

/**
 * ConcreteProduct — сообщение с изображением.
 */
public class ImageMessage extends Message {
    private String imageUrl;

    public ImageMessage(String sender, String imageUrl) {
        super(sender, "[image]");
        this.imageUrl = imageUrl;
    }

    @Override
    public void display() {
        System.out.println("[IMAGE] " + sender + " отправил изображение: " + imageUrl);
    }
}
