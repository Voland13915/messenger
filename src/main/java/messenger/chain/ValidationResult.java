package messenger.chain;

public class ValidationResult {

    private final boolean valid;
    private final String  reason;

    private ValidationResult(boolean valid, String reason) {
        this.valid  = valid;
        this.reason = reason;
    }

    public static ValidationResult ok() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult blocked(String reason) {
        return new ValidationResult(false, reason);
    }

    public boolean isValid()  { return valid; }
    public String  getReason(){ return reason; }
}