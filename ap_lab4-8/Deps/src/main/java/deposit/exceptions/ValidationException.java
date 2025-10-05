package deposit.exceptions;

/**
 * Exception thrown when a business rule or input validation fails.
 * Used to indicate that data does not meet required constraints.
 */
public final class ValidationException extends DepositException {
    
    /**
     * Constructs a ValidationException with the specified detail message.
     *
     * @param message the message describing the validation failure
     */
    public ValidationException(final String message) {
        super(message);
    }
}
