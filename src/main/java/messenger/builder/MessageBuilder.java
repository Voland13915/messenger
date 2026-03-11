package messenger.builder;

public interface MessageBuilder {
    MessageBuilder setText(String text);
    MessageBuilder setSender(String sender);
    MessageBuilder setAttachment(String attachmentUrl);
    MessageBuilder setQuote(String quotedText);
    MessageBuilder setLocation(String coordinates);
    ComplexMessage getResult();
}