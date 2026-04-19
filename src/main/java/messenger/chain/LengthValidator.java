package messenger.chain;

/**
 * ConcreteHandler 2 — проверка длины сообщения.
 *
 * Блокирует если текст превышает допустимый лимит.
 */
public class LengthValidator extends MessageValidator {

    private static final int MAX_LENGTH = 4096;

    @Override
    public ValidationResult handleRequest(ValidationRequest request) {
        if (request.getText().length() > MAX_LENGTH) {
            System.out.println("[LengthValidator] Заблокировано: сообщение слишком длинное ("
                    + request.getText().length() + " символов)");
            return ValidationResult.blocked(
                    "Сообщение слишком длинное (максимум " + MAX_LENGTH + " символов)");
        }
        return passToSuccessor(request);
    }
}