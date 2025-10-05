package console.commands.api;

import console.CommandResult;
import console.CommandContext;
import console.util.ArgParser;
import console.util.ParsedArgs;

/**
 * An abstract base class for commands that provides automatic --help/-h flag handling.
 * Commands that accept arguments should extend this class.
 */
public abstract class AbstractCommand implements ICommand {

    /**
     * Default constructor.
     */
    public AbstractCommand() { }

    /**
     * The final execute method that cannot be overridden.
     * It acts as a template: first checking for help flags, then delegating to executeLogic.
     */
    @Override
    public final CommandResult execute(final CommandContext context, final String[] args) {
        ParsedArgs parsedArgs = ArgParser.parse(args);
        
        // Universal help flag check
        if (parsedArgs.hasFlag("h") || parsedArgs.hasFlag("help")) {
            printHelp();
            return CommandResult.success();
        }

        // If no help flag is found, call the specific logic of the concrete command.
        return executeLogic(context, parsedArgs);
    }

    /**
     * Concrete commands must implement this method to define their specific logic.
     * @param context The shared command context.
     * @param args The pre-parsed arguments for the command to use.
     * @return The result of the command's execution.
     */
    public abstract CommandResult executeLogic(final CommandContext context, final ParsedArgs args);

    /**
     * Prints the standardized help message for the command.
     */
    public final void printHelp() {
        System.out.println("Command: " + getName());
        System.out.println("  " + getDescription());
        System.out.println("\nUsage:");
        System.out.println("  " + getUsage());
    }
}
