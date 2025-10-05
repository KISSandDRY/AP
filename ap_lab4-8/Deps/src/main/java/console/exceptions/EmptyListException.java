package console.exceptions;

/**
 * An exception thrown by utilities when an operation cannot proceed
 * because a required list of items is empty.
 */
public final class EmptyListException extends RuntimeException {

    /**
     * Constructs EmptyListException a with a default message.
     *
     * @param message the detail message explaining the error
     */
    public EmptyListException(final String message) {
        super(message);
    }
}
