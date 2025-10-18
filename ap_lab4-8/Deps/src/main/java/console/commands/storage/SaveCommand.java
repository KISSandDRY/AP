package console.commands.storage;

import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.util.ParsedArgs;

import java.util.Optional;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * A command to save application data to files.
 * <p>
 * This command can save all data to default locations, or save specific data
 * (deposits or accounts) to user-specified files using command-line arguments.
 * It uses the universal {@link console.util.ArgParser} to handle its arguments.
 * </p>
 * @see LoadCommand
 */
public class SaveCommand extends AbstractCommand {

    private static final Logger logger = LogManager.getLogger(SaveCommand.class);

    /**
     * Default constructor.
     */
    public SaveCommand() { }

    @Override
    public String getName() { 
        return "save"; 
    }

    @Override
    public String getDescription() { 
        return "Saves deposits and accounts to specified file."; 
    }

    @Override
    public String getUsage() { 
        return "save [--deposits <file>] [--accounts <file>]";
    }

    @Override
    public CommandResult executeLogic(final CommandContext context, final ParsedArgs args) {
        logger.debug("Executing SaveCommand with args: {}", args.getOptions());
        Optional<String> depositsFile = args.getOption("deposits");
        Optional<String> accountsFile = args.getOption("accounts");

        // If no flags are provided, save everything to default locations.
        if (depositsFile.isEmpty() && accountsFile.isEmpty() && args.getPositionalArgs().isEmpty()) {
            System.out.println("Saving all data to default files...");
            logger.info("Attempting to save all data from default repository files.");

            try {
                boolean success = context.getService().saveAllData();

                if (success) {
                    logger.info("Default save operation completed successfully.");
                    return CommandResult.success("Default save complete.");
                } else {
                    logger.warn("Default save operation failed as reported by the service.");
                    return CommandResult.failure("Default save failed.");
                }

            } catch (Exception e) {
                logger.error("An exception occurred during default data save.", e);
                return CommandResult.failure("Error during default save: " + e.getMessage());
            }
        }

        try {
            if (depositsFile.isPresent()) {
                String file = depositsFile.get();
                System.out.println("Saving deposits to " + file + "...");

                logger.info("Attempting to save deposits from specified file: {}", file);
                context.getService().saveDeposits(file);
                logger.info("Successfully saved deposits from {}.", file);
            }

            if (accountsFile.isPresent()) {
                String file = accountsFile.get();
                System.out.println("Saving accounts to " + file + "...");
                logger.info("Attempting to save accounts from specified file: {}", file);
                context.getService().saveAccounts(file);
                logger.info("Successfully saved accounts from {}.", file);
            }
            
            System.out.println("Save operation successful.");

            return CommandResult.success();

        } catch (Exception e) {
            logger.error("An exception occurred during specific file save operation.", e);
            return CommandResult.failure("Save operation failed: " + e.getMessage());
        }
    }
}
