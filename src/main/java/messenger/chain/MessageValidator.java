package messenger.chain;

public abstract class MessageValidator {

    private MessageValidator successor = null;

    public MessageValidator setSuccessor(MessageValidator successor) {
        this.successor = successor;
        return successor;
    }

    public abstract ValidationResult handleRequest(ValidationRequest request);

    protected ValidationResult passToSuccessor(ValidationRequest request) {
        if (successor != null) {
            return successor.handleRequest(request);
        }
        return ValidationResult.ok();
    }
}