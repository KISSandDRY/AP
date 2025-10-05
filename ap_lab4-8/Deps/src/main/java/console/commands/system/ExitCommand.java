package console.commands.system;

import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.util.ParsedArgs;

/**
 * A placeholder command for the "exit" keyword.
 * <p>
 * The actual application exit logic is handled within the main loop of the
 * {@link console.CommandInterpreter}. This command exists primarily to be
 * listed in the `help` output.
 * </p>
 */
public class ExitCommand extends AbstractCommand {

    /**
     * Default constructor.
     */
    public ExitCommand() { }

    @Override
    public String getName() { 
        return "exit"; 
    }

    @Override
    public String getDescription() { 
        return "Exits the application."; 
    }

    @Override
    public String getUsage() { 
        return getName(); 
    }

    @Override
    public CommandResult executeLogic(
        final CommandContext context, 
        final ParsedArgs args
    ) {
        // The main loop handles the 'exit' keyword. This command does nothing
        // but needs to exist to be listed in 'help'.
        return CommandResult.success();
    }
}
