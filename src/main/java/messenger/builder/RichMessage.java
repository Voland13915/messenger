package messenger.builder;

import java.util.List;
import java.util.ArrayList;

/**
 * Product — сложное сообщение (RichMessage).
 * Включает текст, изображение, вложения и кнопки.
 * Создаётся исключительно через Builder.
 */
public class RichMessage {
    private final String sender;
    private final String text;
    private final String imageUrl;
    private final String attachmentPath;
    private final List<String> buttons;

    // Конструктор пакетного уровня — только Builder может создать объект
    RichMessage(String sender, String text, String imageUrl, String attachmentPath, List<String> buttons) {
        this.sender = sender;
        this.text = text;
        this.imageUrl = imageUrl;
        this.attachmentPath = attachmentPath;
        this.buttons = new ArrayList<>(buttons);
    }

    public void display() {
        System.out.println("  [RichMessage] от " + sender + ":");
        if (text != null)           System.out.println("    Текст: " + text);
        if (imageUrl != null)       System.out.println("    Изображение: " + imageUrl);
        if (attachmentPath != null) System.out.println("    Вложение: " + attachmentPath);
        if (!buttons.isEmpty())     System.out.println("    Кнопки: " + buttons);
    }
}
