package messenger.builder;

import java.util.ArrayList;
import java.util.List;

/**
 * ConcreteBuilder — конкретный строитель сообщений.
 * Конструирует и собирает части объекта RichMessage.
 * Определяет создаваемое представление и следит за ним.
 */
public class RichMessageBuilder implements MessageBuilder {

    private final String sender;
    private String text;
    private String imageUrl;
    private String attachmentPath;
    private final List<String> buttons = new ArrayList<>();

    public RichMessageBuilder(String sender) {
        this.sender = sender;
    }

    @Override
    public MessageBuilder setText(String text) {
        this.text = text;
        return this; // fluent interface
    }

    @Override
    public MessageBuilder setImage(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    @Override
    public MessageBuilder setAttachment(String filePath) {
        this.attachmentPath = filePath;
        return this;
    }

    @Override
    public MessageBuilder addButton(String label) {
        this.buttons.add(label);
        return this;
    }

    /**
     * GetResult — создаёт и возвращает готовый продукт.
     */
    @Override
    public RichMessage build() {
        return new RichMessage(sender, text, imageUrl, attachmentPath, buttons);
    }
}
