package messenger.strategy;

/**
 * Данные которые Context передаёт стратегии в момент вызова.
 *
 * По GoF: "Контекст может передать стратегии все необходимые данные
 * в момент его вызова."
 */
public class SendContext {

    private final String recipient;   // получатель
    private final String text;        // текст / подпись
    private final String filePath;    // путь к файлу (для FILE-стратегии)
    private final String fileType;    // "IMAGE" или "VIDEO"
    private final String coordinates; // координаты (для GEO-стратегии)
    private final String quote;       // цитата (опционально)

    // ── Текстовое сообщение ───────────────────────────────────────────
    public static SendContext forText(String recipient, String text, String quote) {
        return new SendContext(recipient, text, null, null, null, quote);
    }

    // ── Файл ─────────────────────────────────────────────────────────
    public static SendContext forFile(String recipient, String caption,
                                      String filePath, String fileType, String quote) {
        return new SendContext(recipient, caption, filePath, fileType, null, quote);
    }

    // ── Геолокация ────────────────────────────────────────────────────
    public static SendContext forLocation(String recipient, String coordinates,
                                          String quote) {
        return new SendContext(recipient, "📍 " + coordinates, null, null, coordinates, quote);
    }

    private SendContext(String recipient, String text, String filePath,
                        String fileType, String coordinates, String quote) {
        this.recipient   = recipient;
        this.text        = text;
        this.filePath    = filePath;
        this.fileType    = fileType;
        this.coordinates = coordinates;
        this.quote       = quote;
    }

    public String getRecipient()   { return recipient; }
    public String getText()        { return text; }
    public String getFilePath()    { return filePath; }
    public String getFileType()    { return fileType; }
    public String getCoordinates() { return coordinates; }
    public String getQuote()       { return quote; }
}