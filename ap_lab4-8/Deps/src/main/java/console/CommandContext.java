package console;

import deposit.service.DepositService;

import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Optional;

/**
 * Holds shared state and services during command execution.
 * Supports passing data between commands in a pipeline.
 */
public class CommandContext {

    private final Map<String, Object> dataStore = new HashMap<>();
    private final DepositService service;
    private final Scanner scanner;
    private final CommandInterpreter interpreter;

    /**
     * Constructs a CommandContext with the given service, scanner, and interpreter.
     *
     * @param service the DepositService used by commands
     * @param scanner the Scanner to read user input
     * @param interpreter the CommandInterpreter responsible for interpreting commands
     */
    public CommandContext(
        final DepositService service, 
        final Scanner scanner, 
        final CommandInterpreter interpreter
    ) {
        this.service = service;
        this.scanner = scanner;
        this.interpreter = interpreter;
    }

    /**
     * Returns the DepositService associated with this context.
     *
     * @return the DepositService instance
     */
    public DepositService getService() {
        return service;
    }

    /**
     * Returns the Scanner for user input.
     *
     * @return the Scanner instance
     */
    public Scanner getScanner() {
        return scanner;
    }

    /**
     * Returns the CommandInterpreter used in this context.
     *
     * @return the CommandInterpreter instance
     */
    public CommandInterpreter getInterpreter() {
        return interpreter;
    }

    /**
     * Stores data that can be shared between commands in a pipeline.
     *
     * @param data the data to store in the pipeline context
     */
    public void setPipelineData(final Object data) {
        dataStore.put("PIPELINE_DATA", data);
    }

    /**
     * Retrieves data passed from the previous command in a pipeline.
     *
     * @param type the expected class type of the data
     * @param <T> the type parameter inferred from the expected type
     * @return an Optional containing the data if present and matching the type
     */
    public <T> Optional<T> getPipelineData(final Class<T> type) {
        Object data = dataStore.get("PIPELINE_DATA");
        if (data != null && type.isAssignableFrom(data.getClass())) 
            return Optional.of(type.cast(data));

        return Optional.empty();
    }
}
