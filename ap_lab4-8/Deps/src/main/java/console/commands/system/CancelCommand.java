package console.commands.system;

import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.util.ParsedArgs;

/**
 * A placeholder command for the "cancel" keyword.
 * <p>
 * The actual cancellation logic is handled by {@link console.util.Utils#readLine(Scanner, String)},
 * which throws a {@link console.exceptions.CommandCancelledException}. This command
 * exists primarily to be listed in the `help` output.
 * </p>
 */
public class CancelCommand extends AbstractCommand {

    /**
     * Default constructor.
     */
    public CancelCommand() { }

    @Override
    public String getName() { 
        return "cancel"; 
    }

    @Override
    public String getDescription() { 
        return "Cancels the current multi-step operation (e.g., adding a deposit)."; 
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
        System.out.println("Nothing to cancel at the top level. Use 'cancel' during an operation.");

        return CommandResult.success();
    }
}
