package deposit.exceptions;

/**
 * Thrown to indicate an error occurred during data persistence operations,
 * such as file I/O failures. Wraps underlying causes for diagnostic purposes.
 */
public final class DataAccessException extends DepositException {

    /**
     * Constructs a DataAccessException with the specified detail message and cause.
     *
     * @param message the detail message explaining the error
     * @param cause the underlying exception that caused this error
     */
    public DataAccessException(final String message, Throwable cause) {
        super(message, cause);
    }
}
