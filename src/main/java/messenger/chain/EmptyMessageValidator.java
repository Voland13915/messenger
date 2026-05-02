package messenger.chain;

public class EmptyMessageValidator extends MessageValidator {

    @Override
    public ValidationResult handleRequest(ValidationRequest request) {
        if (request.getText() == null || request.getText().isBlank()) {
            System.out.println("[EmptyMessageValidator] Заблокировано: пустое сообщение");
            return ValidationResult.blocked("Сообщение не может быть пустым");
        }
        return passToSuccessor(request);
    }
}