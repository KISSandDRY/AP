package console.commands.system;

import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.util.ParsedArgs;

/**
 * A simple command that clears the console screen.
 * <p>
 * It delegates the screen-clearing action to the {@link console.CommandInterpreter}.
 * </p>
 */
public class ClearCommand extends AbstractCommand {

    /**
     * Default constructor.
     */
    public ClearCommand() { }
    
    @Override
    public String getName() { 
        return "clear"; 
    }

    @Override
    public String getDescription() { 
        return "Clears the console screen."; 
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
        context.getInterpreter().clearScreen();

        return CommandResult.success();
    }
}
