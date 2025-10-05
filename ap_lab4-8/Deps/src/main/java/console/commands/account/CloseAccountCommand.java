package console.commands.account;

import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.exceptions.EmptyListException;
import console.exceptions.CommandCancelledException;
import console.util.Utils;
import console.util.ParsedArgs;
import console.util.SelectionResult;
import console.util.printer.ListAccountsPrinter;
import deposit.domain.DepositAccount;

/**
 * A command to close an deposit account.
 * The user is prompted to select a specific account from a list of all available accounts.
 */
public class CloseAccountCommand extends AbstractCommand {

    /**
     * Default constructor.
     */
    public CloseAccountCommand() { }

    @Override
    public String getName() {
        return "close";
    }

    @Override
    public String getDescription() {
        return "Closes an account.";
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
        try {
            // Prompt the user to select one account from the list.
            SelectionResult<DepositAccount> selectionResult = Utils.selectFromList(
                context.getScanner(),
                context.getService().getAllAccounts(),
                new ListAccountsPrinter(),
                "Select an account to close",
                false // Do not allow an "All" option.
            );

            // Process the user's selection using a functional approach.
            return selectionResult.selectedItem()
                .map(accountToClose -> {
                    // This block executes only if an item was selected.
                    boolean success = context.getService().closeAccount(accountToClose.getId());

                    if (success) {
                        String shortId = accountToClose.getId().toString().substring(0, 8);
                        System.out.println("Account " + shortId + " was closed successfully.");

                        return CommandResult.success();
                    }

                    return CommandResult.failure("Failed to close the specified account.");
                })
                .orElse(CommandResult.failure("No account was selected.")); // This is returned if no item was selected.

        } catch (EmptyListException e) {
            return CommandResult.failure("No accounts are available to close.");

        } catch (CommandCancelledException e) {
            return CommandResult.failure("Account closing was cancelled.");

        } catch (Exception e) {
            return CommandResult.failure("An error occurred while closing the account: " + e.getMessage());
        }
    }
}
