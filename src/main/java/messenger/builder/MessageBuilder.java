package messenger.builder;

/**
 * Builder — абстрактный строитель сообщений.
 * Задаёт интерфейс для создания частей объекта RichMessage.
 */
public interface MessageBuilder {
    MessageBuilder setText(String text);
    MessageBuilder setImage(String imageUrl);
    MessageBuilder setAttachment(String filePath);
    MessageBuilder addButton(String label);

    // GetResult — возвращает построенный продукт
    RichMessage build();
}
