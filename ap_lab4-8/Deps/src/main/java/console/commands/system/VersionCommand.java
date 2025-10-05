package console.commands.system;

import app.AppInfo;
import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.util.ParsedArgs;

/**
 * A command that prints the application's name and version information.
 * It retrieves this information from the {@link app.AppInfo} class.
 */
public class VersionCommand extends AbstractCommand {

    /**
     * Default constructor.
     */
    public VersionCommand() { }

    @Override
    public String getName() { 
        return "version"; 
    }

    @Override
    public String getDescription() { 
        return "Shows the application version."; 
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
        System.out.println(AppInfo.getFullAppName());

        return CommandResult.success();
    }
}
