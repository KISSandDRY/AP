package console.commands.storage;

import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.util.ParsedArgs;

import java.util.Optional;

/**
 * A command to load application data from files.
 * <p>
 * This command can load all data from default locations, or load from specific data
 * (deposits or accounts) to user-specified files using command-line arguments.
 * It uses the universal {@link console.util.ArgParser} to handle its arguments.
 * </p>
 * @see SaveCommand 
 */
public class LoadCommand extends AbstractCommand {

    /**
     * Default constructor.
     */
    public LoadCommand() { }

    @Override
    public String getName() {
        return "load";
    }

    @Override
    public String getDescription() {
        return "Loads deposits and/or accounts from specified files.";
    }

    @Override
    public String getUsage() {
        return getName() + " " + """
               [--deposits <file>] [--accounts <file>]

               ARGUMENTS:
                 --deposits <file>   Loads only deposits from the specified file.
                 --accounts <file>   Loads only accounts from the specified file.

               If no arguments are provided, loads all data from default files.""";
    }

    @Override
    public CommandResult executeLogic(
        final CommandContext context, 
        final ParsedArgs args
    ) {
        Optional<String> depositsFile = args.getOption("deposits");
        Optional<String> accountsFile = args.getOption("accounts");

        // If no specific flags are given, load everything from default files.
        if (depositsFile.isEmpty() && accountsFile.isEmpty()) {
            System.out.println("Loading all data from default files...");

            try {
                boolean success = context.getService().loadAllData();

                return success ? CommandResult.success("Default load complete.") : CommandResult.failure("Default load failed.");

            } catch (Exception e) {
                return CommandResult.failure("Error during default load: " + e.getMessage());
            }
        }

        // Handle specific file loads
        try {
            if (depositsFile.isPresent()) {
                String file = depositsFile.get();
                System.out.println("Loading deposits from " + file + "...");
                context.getService().loadDeposits(file);
            }

            if (accountsFile.isPresent()) {
                String file = accountsFile.get();
                System.out.println("Loading accounts from " + file + "...");
                context.getService().loadAccounts(file);
            }
            
            return CommandResult.success("Load operation successful.");

        } catch (Exception e) {
            return CommandResult.failure("Load operation failed: " + e.getMessage());
        }
    }
}
