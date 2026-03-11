package messenger.prototype;

public class TextMessage implements Message {
    private final String content;
    private final String sender;
    private String chatId;

    public TextMessage(String content, String sender, String chatId) {
        this.content = content;
        this.sender  = sender;
        this.chatId  = chatId;
    }

    @Override
    public Message clone() { return new TextMessage(this.content, this.sender, this.chatId); }

    @Override public void setChatId(String chatId) { this.chatId = chatId; }
    @Override public String getContent()           { return content; }
    @Override public String getSender()            { return sender; }
    @Override public String getChatId()            { return chatId; }
}