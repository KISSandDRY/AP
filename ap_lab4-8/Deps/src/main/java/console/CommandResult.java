package console;

import java.util.Optional;

/**
 * Represents the result of executing a command, including success status, optional data, and optional message.
 * Provides factory methods to create success or failure results and type-safe access to returned data.
 */
public class CommandResult {
    private final Object data;
    private final boolean isSuccess;
    private final String message;

    private CommandResult(
        final Object data, 
        final boolean isSuccess, 
        final String message
    ) {
        this.data = data;
        this.isSuccess = isSuccess;
        this.message = message;
    }

    /**
     * Creates a successful CommandResult wrapping the provided data.
     * 
     * @param data the data resulting from command execution (can be null)
     * @return a CommandResult indicating success with data
     */
    public static CommandResult success(final Object data) {
        return new CommandResult(data, true, null);
    }

    /**
     * Creates a successful CommandResult without any data.
     *
     * @return a CommandResult indicating success without data
     */
    public static CommandResult success() {
        return new CommandResult(null, true, null);
    }

    /**
     * Creates a failed CommandResult with an explanatory message.
     * 
     * @param message the failure message
     * @return a CommandResult indicating failure with message
     */
    public static CommandResult failure(final String message) {
        return new CommandResult(null, false, message);
    }

    /**
     * Attempts to retrieve the contained data as the specified type.
     * 
     * @param <T> the expected type of the data
     * @param type the Class object representing the expected type
     * @return an Optional containing the data cast to the specified type if available and compatible, or empty otherwise
     */
    public <T> Optional<T> getData(final Class<T> type) {
        if (data != null && type.isAssignableFrom(data.getClass())) 
            return Optional.of(type.cast(data));
        
        return Optional.empty();
    }

    /**
     * Indicates whether the command execution was successful.
     *
     * @return true if successful, false otherwise
     */   
    public boolean isSuccess() { 
        return isSuccess; 
    }

    /**
     * Returns an optional failure message if the command failed.
     *
     * @return an Optional containing the failure message or empty if none
     */
    public Optional<String> getMessage() { 
        return Optional.ofNullable(message); 
    }
}
