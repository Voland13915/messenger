package messenger.chain;

public class ValidationRequest {

    private final String text;
    private final String recipient;
    private final boolean connected;

    public ValidationRequest(String text, String recipient, boolean connected) {
        this.text      = text;
        this.recipient = recipient;
        this.connected = connected;
    }

    public String  getText()        { return text; }
    public String  getRecipient()   { return recipient; }
    public boolean isConnected()    { return connected; }
}