package messenger.prototype;

// Prototype
interface Message {
    /* Clone() */
    Message clone();
    String getContent();
    String getSender();
    String getChatId();
}

//ConcretePrototype1
class TextMessage implements Message {
    private final String content;
    private final String sender;
    private String chatId;

    public TextMessage(String content, String sender, String chatId) {
        this.content = content;
        this.sender  = sender;
        this.chatId  = chatId;
    }

    /* Clone() — вернуть копию самого себя */
    @Override
    public Message clone() {
        return new TextMessage(this.content, this.sender, this.chatId);
    }

    public void setChatId(String chatId) { this.chatId = chatId; }

    @Override public String getContent() { return content; }
    @Override public String getSender()  { return sender; }
    @Override public String getChatId()  { return chatId; }
}

//ConcretePrototype2
class MediaMessage implements Message {
    private final String content;
    private final String sender;
    private String chatId;
    private final String mediaUrl;

    public MediaMessage(String content, String sender, String chatId, String mediaUrl) {
        this.content  = content;
        this.sender   = sender;
        this.chatId   = chatId;
        this.mediaUrl = mediaUrl;
    }

    /* Clone() — вернуть копию самого себя */
    @Override
    public Message clone() {
        return new MediaMessage(this.content, this.sender, this.chatId, this.mediaUrl);
    }

    public void setChatId(String chatId) { this.chatId = chatId; }

    @Override public String getContent() { return content; }
    @Override public String getSender()  { return sender; }
    @Override public String getChatId()  { return chatId; }
    public String getMediaUrl()          { return mediaUrl; }
}

//Client
class MessageForwarder {
    /* prototype — ссылка на прототип */
    private final Message prototype;

    public MessageForwarder(Message prototype) {
        this.prototype = prototype;
    }

    /* Operation() — p = prototype->Clone() */
    public Message forward(String newChatId) {
        Message p = prototype.clone();
        ((TextMessage) p).setChatId(newChatId);
        return p;
    }
}

// Демонстрация
class PrototypeDemo {
    public static void main(String[] args) {
        System.out.println("=== Prototype: MessageForwarder ===");

        TextMessage original = new TextMessage("Привет!", "alice", "chat_1");
        System.out.println("  Оригинал:  [" + original.getChatId() + "] " + original.getSender() + ": " + original.getContent());
        MessageForwarder forwarder = new MessageForwarder(original);
        Message forwarded = forwarder.forward("chat_2");
        System.out.println("  Переслано: [" + forwarded.getChatId() + "] " + forwarded.getSender() + ": " + forwarded.getContent());

        MediaMessage media = new MediaMessage("Фото с вечеринки", "bob", "chat_1", "https://cdn.example.com/photo.jpg");
        System.out.println("  Оригинал:  [" + media.getChatId() + "] " + media.getSender() + ": " + media.getContent());
        MediaMessage mediaClone = (MediaMessage) media.clone();
        mediaClone.setChatId("chat_3");
        System.out.println("  Клон:      [" + mediaClone.getChatId() + "] " + mediaClone.getSender() + ": " + mediaClone.getContent());
    }
}