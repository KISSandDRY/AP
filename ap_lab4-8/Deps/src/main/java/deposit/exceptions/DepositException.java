package deposit.exceptions;

/**
 * The base exception class for application-specific errors related to deposits.
 * Extends {@link RuntimeException} to represent unchecked exceptions.
 */
public class DepositException extends RuntimeException {

    /**
     * Constructs a DepositException with the specified detail message.
     *
     * @param message the detail message explaining the error
     */
    public DepositException(final String message) {
        super(message);
    }

    /**
     * Constructs a DepositException with the specified detail message and cause.
     *
     * @param message the detail message explaining the error
     * @param cause the underlying cause of the exception
     */
    public DepositException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
