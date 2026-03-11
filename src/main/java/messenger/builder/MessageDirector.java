package messenger.builder;

public class MessageDirector {
    private final MessageBuilder builder;

    public MessageDirector(MessageBuilder builder) { this.builder = builder; }

    public void construct(String sender, String text, String quote, String attachment, String location) {
        builder.setSender(sender)
                .setText(text)
                .setQuote(quote)
                .setAttachment(attachment)
                .setLocation(location);
    }
}