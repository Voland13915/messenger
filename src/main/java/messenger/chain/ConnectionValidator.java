package messenger.chain;

public class ConnectionValidator extends MessageValidator {

    @Override
    public ValidationResult handleRequest(ValidationRequest request) {
        if (!request.isConnected()) {
            System.out.println("[ConnectionValidator] Заблокировано: нет соединения с сервером");
            return ValidationResult.blocked("Нет соединения с сервером");
        }
        return passToSuccessor(request);
    }
}