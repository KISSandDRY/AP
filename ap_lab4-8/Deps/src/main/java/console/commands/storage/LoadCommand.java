package console.commands.storage;

import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.util.ParsedArgs;

import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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

    private static final Logger logger = LogManager.getLogger(LoadCommand.class);

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
        logger.debug("Executing LoadCommand with args: {}", args.getOptions());
        Optional<String> depositsFile = args.getOption("deposits");
        Optional<String> accountsFile = args.getOption("accounts");

        // If no specific flags are given, load everything from default files.
        if (depositsFile.isEmpty() && accountsFile.isEmpty()) {
            System.out.println("Loading all data from default files...");
            logger.info("Attempting to load all data from default repository files.");

            try {
                boolean success = context.getService().loadAllData();

                if (success) {
                    logger.info("Default load operation completed successfully.");
                    return CommandResult.success("Default load complete.");
                } else {
                    logger.warn("Default load operation failed as reported by the service.");
                    return CommandResult.failure("Default load failed.");
                }

            } catch (Exception e) {
                logger.error("An exception occurred during default data load.", e);
                return CommandResult.failure("Error during default load: " + e.getMessage());
            }
        }

        // Handle specific file loads
        try {
            if (depositsFile.isPresent()) {
                String file = depositsFile.get();
                System.out.println("Loading deposits from " + file + "...");
                logger.info("Attempting to load deposits from specified file: {}", file);
                context.getService().loadDeposits(file);
                logger.info("Successfully loaded deposits from {}.", file);
            }

            if (accountsFile.isPresent()) {
                String file = accountsFile.get();
                System.out.println("Loading accounts from " + file + "...");
                logger.info("Attempting to load accounts from specified file: {}", file);
                context.getService().loadAccounts(file);
                logger.info("Successfully loaded accounts from {}.", file);
            }
            
            return CommandResult.success("Load operation successful.");

        } catch (Exception e) {
            logger.error("An exception occurred during specific file load operation.", e);
            return CommandResult.failure("Load operation failed: " + e.getMessage());
        }
    }
}
