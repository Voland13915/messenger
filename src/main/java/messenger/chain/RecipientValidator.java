package messenger.chain;

public class RecipientValidator extends MessageValidator {

    @Override
    public ValidationResult handleRequest(ValidationRequest request) {
        if (request.getRecipient() == null || request.getRecipient().isBlank()) {
            System.out.println("[RecipientValidator] Заблокировано: получатель не выбран");
            return ValidationResult.blocked("Выберите чат перед отправкой");
        }
        return passToSuccessor(request);
    }
}