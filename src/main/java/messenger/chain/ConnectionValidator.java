package messenger.chain;

/**
 * ConcreteHandler 4 — проверка соединения с сервером.
 *
 * Последний в цепочке перед отправкой.
 * Блокирует если нет активного WebSocket-соединения.
 */
public class ConnectionValidator extends MessageValidator {

    @Override
    public ValidationResult handleRequest(ValidationRequest request) {
        if (!request.isConnected()) {
            System.out.println("[ConnectionValidator] Заблокировано: нет соединения с сервером");
            return ValidationResult.blocked("Нет соединения с сервером");
        }
        // Все проверки пройдены — конец цепочки
        return passToSuccessor(request);
    }
}