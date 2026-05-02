package messenger.prototype;

public class MessageForwarder {
    private final Message prototype;

    public MessageForwarder(Message prototype) { this.prototype = prototype; }

    public Message forward(String newChatId) {
        Message p = prototype.clone();
        p.setChatId(newChatId);
        return p;
    }
}