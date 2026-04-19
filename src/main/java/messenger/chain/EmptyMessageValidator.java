package messenger.chain;

/**
 * ConcreteHandler 1 — проверка на пустое сообщение.
 *
 * Если текст пустой — блокирует (берёт ответственность на себя).
 * Если не пустой — передаёт преемнику: passToSuccessor(request).
 */
public class EmptyMessageValidator extends MessageValidator {

    @Override
    public ValidationResult handleRequest(ValidationRequest request) {
        if (request.getText() == null || request.getText().isBlank()) {
            // ConcreteHandler обрабатывает запрос — цепочка прерывается
            System.out.println("[EmptyMessageValidator] Заблокировано: пустое сообщение");
            return ValidationResult.blocked("Сообщение не может быть пустым");
        }
        // Не наш случай — передаём преемнику
        return passToSuccessor(request);
    }
}