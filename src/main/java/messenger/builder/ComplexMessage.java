package messenger.builder;

public class ComplexMessage {
    private String text;
    private String sender;
    private String attachmentUrl;
    private String quotedText;
    private String location;

    public void setText(String text)            { this.text = text; }
    public void setSender(String sender)        { this.sender = sender; }
    public void setAttachment(String url)       { this.attachmentUrl = url; }
    public void setQuote(String quotedText)     { this.quotedText = quotedText; }
    public void setLocation(String coordinates) { this.location = coordinates; }

    @Override
    public String toString() {
        return "ComplexMessage{" +
                "sender='"      + sender        + '\'' +
                ", text='"      + text          + '\'' +
                ", quote='"     + quotedText    + '\'' +
                ", attachment='"+ attachmentUrl + '\'' +
                ", location='"  + location      + '\'' +
                '}';
    }

    public String getText() { return text; }
}

