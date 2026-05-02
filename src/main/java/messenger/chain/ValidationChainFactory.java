package messenger.chain;

public class ValidationChainFactory {

    public static MessageValidator build() {
        MessageValidator emptyCheck      = new EmptyMessageValidator();
        MessageValidator lengthCheck     = new LengthValidator();
        MessageValidator recipientCheck  = new RecipientValidator();
        MessageValidator connectionCheck = new ConnectionValidator();

        emptyCheck
                .setSuccessor(lengthCheck)
                .setSuccessor(recipientCheck)
                .setSuccessor(connectionCheck);

        return emptyCheck;
    }
}