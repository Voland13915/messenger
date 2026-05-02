package messenger.strategy;

public class SendContext {

    private final String recipient;
    private final String text;
    private final String filePath;
    private final String fileType;
    private final String coordinates;
    private final String quote;

    public static SendContext forText(String recipient, String text, String quote) {
        return new SendContext(recipient, text, null, null, null, quote);
    }

    public static SendContext forFile(String recipient, String caption,
                                      String filePath, String fileType, String quote) {
        return new SendContext(recipient, caption, filePath, fileType, null, quote);
    }

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