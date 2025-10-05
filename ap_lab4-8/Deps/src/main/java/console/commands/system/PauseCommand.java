package console.commands.system;

import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.util.ParsedArgs;

/**
 * A simple command that pauses the console until the user presses the Enter key.
 */
public class PauseCommand extends AbstractCommand {

    /**
     * Default constructor.
     */
    public PauseCommand() { }

    @Override
    public String getName() { 
        return "pause"; 
    }

    @Override
    public String getDescription() {
        return "Pauses the console until Enter is pressed."; 
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
        context.getInterpreter().pause();

        return CommandResult.success();
    }
}
