package console.commands.storage;

import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.util.ParsedArgs;

import java.util.Optional;

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
        Optional<String> depositsFile = args.getOption("deposits");
        Optional<String> accountsFile = args.getOption("accounts");

        // If no flags are provided, save everything to default locations.
        if (depositsFile.isEmpty() && accountsFile.isEmpty() && args.getPositionalArgs().isEmpty()) {
            System.out.println("Saving all data to default files...");

            try {
                boolean success = context.getService().saveAllData();
                return success ? CommandResult.success("Default save complete.") : CommandResult.failure("Default save failed.");

            } catch (Exception e) {
                return CommandResult.failure("Error during default save: " + e.getMessage());
            }
        }

        try {
            if (depositsFile.isPresent()) {
                String file = depositsFile.get();
                System.out.println("Saving deposits to " + file + "...");
                context.getService().saveDeposits(file);
            }

            if (accountsFile.isPresent()) {
                String file = accountsFile.get();
                System.out.println("Saving accounts to " + file + "...");
                context.getService().saveAccounts(file);
            }
            
            System.out.println("Save operation successful.");

            return CommandResult.success();

        } catch (Exception e) {
            return CommandResult.failure("Save operation failed: " + e.getMessage());
        }
    }
}
