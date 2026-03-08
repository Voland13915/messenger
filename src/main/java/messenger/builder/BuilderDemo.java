package messenger.builder;

//Builder
interface MessageBuilder {
    /* BuildPart() */
    MessageBuilder setText(String text);
    MessageBuilder setSender(String sender);
    MessageBuilder setAttachment(String attachmentUrl);
    MessageBuilder setQuote(String quotedText);
    MessageBuilder setLocation(String coordinates);

    /* GetResult() */
    ComplexMessage getResult();
}

// Product
class ComplexMessage {
    private String text;
    private String sender;
    private String attachmentUrl;
    private String quotedText;
    private String location;

    public void setText(String text)               { this.text = text; }
    public void setSender(String sender)           { this.sender = sender; }
    public void setAttachment(String url)          { this.attachmentUrl = url; }
    public void setQuote(String quotedText)        { this.quotedText = quotedText; }
    public void setLocation(String coordinates)    { this.location = coordinates; }

    @Override
    public String toString() {
        return "ComplexMessage{" +
                "sender='"     + sender        + '\'' +
                ", text='"     + text          + '\'' +
                ", quote='"    + quotedText    + '\'' +
                ", attachment='"+ attachmentUrl + '\'' +
                ", location='" + location      + '\'' +
                '}';
    }
}

//ConcreteBuilder
class ConcreteMessageBuilder implements MessageBuilder {
    private final ComplexMessage message = new ComplexMessage();

    /* BuildPart() */
    @Override
    public MessageBuilder setText(String text) {
        message.setText(text);
        return this;
    }

    @Override
    public MessageBuilder setSender(String sender) {
        message.setSender(sender);
        return this;
    }

    @Override
    public MessageBuilder setAttachment(String attachmentUrl) {
        message.setAttachment(attachmentUrl);
        return this;
    }

    @Override
    public MessageBuilder setQuote(String quotedText) {
        message.setQuote(quotedText);
        return this;
    }

    @Override
    public MessageBuilder setLocation(String coordinates) {
        message.setLocation(coordinates);
        return this;
    }

    /* GetResult() */
    @Override
    public ComplexMessage getResult() {
        return message;
    }
}

// Director
class MessageDirector {
    /* builder — ссылка на строитель */
    private final MessageBuilder builder;

    public MessageDirector(MessageBuilder builder) {
        this.builder = builder;
    }

    /* Construct() — для всех объектов в структуре: builder->BuildPart() */
    public void construct(String sender, String text, String quote, String attachment, String location) {
        builder.setSender(sender)
                .setText(text)
                .setQuote(quote)
                .setAttachment(attachment)
                .setLocation(location);
    }
}

// Демонстрация
class BuilderDemo {
    public static void main(String[] args) {
        System.out.println("=== Builder: ConcreteMessageBuilder ===");

        ConcreteMessageBuilder builder = new ConcreteMessageBuilder();
        MessageDirector director = new MessageDirector(builder);
        director.construct("alice", "Встретимся завтра?", "Договорились!", "https://cdn.example.com/map.jpg", "53.9045,27.5615");
        ComplexMessage complexMessage = builder.getResult();
        System.out.println("  " + complexMessage);
    }
}