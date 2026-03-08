package messenger.factorymethod;

//Product
interface Message {
    void send();
    String getType();
}

//ConcreteProduct1
class TextMessage implements Message {
    private final String content;

    public TextMessage(String content) {
        this.content = content;
    }

    @Override
    public void send() {
        System.out.println("  [TextMessage] Отправка текста: " + content);
    }

    @Override
    public String getType() { return "TEXT"; }
}

//ConcreteProduct2
class ImageMessage implements Message {
    private final String imageUrl;

    public ImageMessage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public void send() {
        System.out.println("  [ImageMessage] Отправка изображения: " + imageUrl);
    }

    @Override
    public String getType() { return "IMAGE"; }
}

//ConcreteProduct3
class VideoMessage implements Message {
    private final String videoUrl;

    public VideoMessage(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    @Override
    public void send() {
        System.out.println("  [VideoMessage] Отправка видео: " + videoUrl);
    }

    @Override
    public String getType() { return "VIDEO"; }
}

//Creator
abstract class MessageCreator {

    /* FactoryMethod() — возвращает объект типа Product */
    public abstract Message factoryMethod(String data);

    /* AnOperation() — использует фабричный метод: product = FactoryMethod() */
    public void sendMessage(String data) {
        Message product = factoryMethod(data);
        product.send();
    }
}

//ConcreteCreator1
class TextMessageCreator extends MessageCreator {

    /* FactoryMethod() — return new ConcreteProduct */
    @Override
    public Message factoryMethod(String data) {
        return new TextMessage(data);
    }
}

//ConcreteCreator2
class ImageMessageCreator extends MessageCreator {

    /* FactoryMethod() — return new ConcreteProduct */
    @Override
    public Message factoryMethod(String data) {
        return new ImageMessage(data);
    }
}

//ConcreteCreator3
class VideoMessageCreator extends MessageCreator {

    /* FactoryMethod() — return new ConcreteProduct */
    @Override
    public Message factoryMethod(String data) {
        return new VideoMessage(data);
    }
}

// Демонстрация
class FactoryMethodDemo {
    public static void main(String[] args) {
        System.out.println("=== Factory Method: MessageCreator ===");

        new TextMessageCreator().sendMessage("Как дела?");
        new ImageMessageCreator().sendMessage("https://cdn.example.com/photo.jpg");
        new VideoMessageCreator().sendMessage("https://cdn.example.com/video.mp4");
    }
}