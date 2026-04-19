package messenger.chain;

/**
 * Объект запроса — передаётся по цепочке обработчиков.
 * Каждый ConcreteHandler читает нужные ему поля.
 */
public class ValidationRequest {

    private final String text;       // текст сообщения
    private final String recipient;  // получатель
    private final boolean connected; // есть ли соединение с сервером

    public ValidationRequest(String text, String recipient, boolean connected) {
        this.text      = text;
        this.recipient = recipient;
        this.connected = connected;
    }

    public String  getText()        { return text; }
    public String  getRecipient()   { return recipient; }
    public boolean isConnected()    { return connected; }
}