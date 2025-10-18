package console.commands.deposit;

import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.exceptions.EmptyListException;
import console.exceptions.CommandCancelledException;
import console.util.Utils;
import console.util.ParsedArgs;
import console.util.SelectionResult;
import console.util.printer.ListDepositsPrinter;
import deposit.domain.Deposit;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * A command to remove a deposit product from the system.
 * The user is prompted to select a specific deposit from a list of all available ones.
 */
public class RemoveDepositCommand extends AbstractCommand {

    private static final Logger logger = LogManager.getLogger(RemoveDepositCommand.class);
    
    /**
     * Default constructor.
     */
    public RemoveDepositCommand() { }

    @Override
    public String getName() {
        return "remove";
    }

    @Override
    public String getDescription() {
        return "Removes a deposit from the system.";
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
        logger.debug("Executing RemoveDepositCommand.");

        try {
            // Use the unified selection utility to show options and get user choice.
            SelectionResult<Deposit> selectionResult = Utils.selectFromList(
                context.getScanner(),
                context.getService().getAllDeposits(),
                new ListDepositsPrinter(),
                "Select a deposit to remove",
                false // Do not allow an "All" option.
            );

            // Process the user's selection in a functional style.
            return selectionResult.selectedItem()
                .map(depositToRemove -> {
                    // This block executes only if the user selected an item.
                    logger.info("User selected deposit '{}' (ID: {}) for removal.", 
                        depositToRemove.getInfo().depositName(), depositToRemove.getId());
                    boolean success = context.getService().removeDeposit(depositToRemove.getId());

                    if (success) {
                        System.out.println("Deposit '" + depositToRemove.getInfo().depositName() + "' was removed successfully.");
                        logger.info("Successfully removed deposit with ID: {}.", depositToRemove.getId());
                        return CommandResult.success(depositToRemove);
                    }

                    logger.warn("Service reported failure to remove deposit with ID: {}.", depositToRemove.getId());
                    return CommandResult.failure("Could not remove the specified deposit.");
                })
                .orElse(CommandResult.failure("No deposit was selected.")); // This is returned if the user made no selection.

        } catch (EmptyListException e) {
            logger.warn("Remove operation aborted: No deposits exist in the system to remove.");
            return CommandResult.failure("No deposits are available to remove.");

        } catch (CommandCancelledException e) {
            logger.info("User cancelled the remove deposit operation.");
            return CommandResult.failure("Deposit removal was cancelled.");

        } catch (Exception e) {
            logger.error("An unexpected error occurred during deposit removal.", e);
            return CommandResult.failure("An error occurred during removal: " + e.getMessage());
        }
    }
}
