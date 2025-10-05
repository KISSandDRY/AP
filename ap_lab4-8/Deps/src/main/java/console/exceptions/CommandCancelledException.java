package console.exceptions;

/**
 * Indicates that a command was cancelled by the user.
 * This is an unchecked exception thrown to signal that the command execution
 * was intentionally aborted.
 */
public final class CommandCancelledException extends RuntimeException {

    /**
     * Constructs a CommandCancelledException with a default cancellation message.
     */
    public CommandCancelledException() {
        super("Command cancelled by user");
    }
}
