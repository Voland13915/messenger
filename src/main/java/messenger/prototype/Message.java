package messenger.prototype;

public interface Message {
    Message clone();
    String getContent();
    String getSender();
    String getChatId();
    void setChatId(String chatId);
}