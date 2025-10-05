package console.commands.api;

import console.CommandResult;
import console.CommandContext;

/**
 * Defines the contract for a command that can be executed in the application.
 * Provides methods for obtaining command metadata and executing the command logic.
 */
public interface ICommand {

    /**
     * Returns the unique name of the command.
     *
     * @return the command name
     */
    String getName();

    /**
     * Returns a brief description of the command's purpose.
     *
     * @return the command description
     */
    String getDescription();

    /**
     * Returns usage information or syntax help for the command.
     *
     * @return the command usage syntax
     */
    String getUsage();

    /**
     * Executes the command logic using the given context and arguments.
     *
     * @param context the shared context providing services and pipeline data
     * @param args the command-line arguments passed to the command
     * @return a {@link CommandResult} indicating the outcome of execution
     */
    CommandResult execute(final CommandContext context, final String[] args);
}
