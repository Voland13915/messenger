package messenger.prototype;

public class MediaMessage implements Message {
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

    @Override
    public Message clone() { return new MediaMessage(this.content, this.sender, this.chatId, this.mediaUrl); }

    @Override public void setChatId(String chatId) { this.chatId = chatId; }
    @Override public String getContent()           { return content; }
    @Override public String getSender()            { return sender; }
    @Override public String getChatId()            { return chatId; }
    public String getMediaUrl()                    { return mediaUrl; }
}