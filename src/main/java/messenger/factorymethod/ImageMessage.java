package messenger.factorymethod;

public class ImageMessage implements Message {
    private final String imageUrl;

    public ImageMessage(String imageUrl) { this.imageUrl = imageUrl; }

    @Override
    public void send() { System.out.println("  [ImageMessage] Отправка изображения: " + imageUrl); }

    @Override
    public String getType() { return "IMAGE"; }
}