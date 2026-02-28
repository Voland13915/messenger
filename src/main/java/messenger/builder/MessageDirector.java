package messenger.builder;

/**
 * Director — распорядитель построения сообщений.
 * Конструирует объект, пользуясь интерфейсом Builder.
 * Знает порядок шагов, но не знает конкретную реализацию.
 */
public class MessageDirector {

    private final MessageBuilder builder;

    public MessageDirector(MessageBuilder builder) {
        this.builder = builder;
    }

    /**
     * Construct() — строит стандартное уведомление (текст + кнопки).
     */
    public RichMessage buildNotification(String text) {
        return builder
                .setText(text)
                .addButton("Принять")
                .addButton("Отклонить")
                .build();
    }

    /**
     * Строит медиасообщение с изображением и вложением.
     */
    public RichMessage buildMediaMessage(String text, String imageUrl, String attachment) {
        return builder
                .setText(text)
                .setImage(imageUrl)
                .setAttachment(attachment)
                .addButton("Скачать")
                .build();
    }
}
