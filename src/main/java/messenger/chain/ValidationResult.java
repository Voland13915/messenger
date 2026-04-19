package messenger.chain;

/**
 * Результат прохождения цепочки.
 * Либо успех (можно отправлять), либо блокировка с причиной.
 */
public class ValidationResult {

    private final boolean valid;
    private final String  reason; // причина блокировки (null если valid=true)

    private ValidationResult(boolean valid, String reason) {
        this.valid  = valid;
        this.reason = reason;
    }

    /** Все проверки пройдены — отправлять можно */
    public static ValidationResult ok() {
        return new ValidationResult(true, null);
    }

    /** Запрос заблокирован — отправлять нельзя */
    public static ValidationResult blocked(String reason) {
        return new ValidationResult(false, reason);
    }

    public boolean isValid()  { return valid; }
    public String  getReason(){ return reason; }
}