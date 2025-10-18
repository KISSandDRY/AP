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
import deposit.domain.value.Money;

import java.math.BigDecimal;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * A command to add funds to an existing deposit account.
 * The user is prompted to select an account and then enter the amount to add.
 */
public class ReplenishAccountCommand extends AbstractCommand {
    
    private static final Logger logger = LogManager.getLogger(ReplenishAccountCommand.class);

    /**
     * Default constructor.
     */
    public ReplenishAccountCommand() { }

    @Override
    public String getName() { 
        return "replenish"; 
    }

    @Override
    public String getDescription() { 
        return "Replenish an account"; 
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
        logger.debug("Executing ReplenishAccountCommand.");

        try {
            // Step 1: Prompt the user to select an account.
            SelectionResult<DepositAccount> selectionResult = Utils.selectFromList(
                context.getScanner(),
                context.getService().getAllAccounts(),
                new ListAccountsPrinter(),
                "Select an account to replenish",
                false // Do not allow an "All" option.
            );

            // Step 2: Process the user's selection.
            return selectionResult.selectedItem()
                .map(account -> handleReplenishment(context, account))
                .orElse(CommandResult.failure("No account was selected."));

        } catch (EmptyListException e) {
            logger.warn("Replenish operation failed: No accounts exist in the system to select from.");
            return CommandResult.failure("No accounts are available to replenish.");

        } catch (CommandCancelledException e) {
            logger.info("User cancelled the replenish account operation.");
            return CommandResult.failure("Replenishment was cancelled.");

        } catch (Exception e) {
            logger.error("An unexpected error occurred during replenishment.", e);
            return CommandResult.failure("Replenishment failed: " + e.getMessage());
        }
    }

    /**
     * Handles the user interaction for gathering the replenishment amount and calling the service.
     *
     * @param context The command context.
     * @param selectedAccount The account chosen by the user.
     * @return A {@link CommandResult} indicating the outcome.
     */
    private CommandResult handleReplenishment(CommandContext context, DepositAccount selectedAccount) {
        logger.info("User selected account with ID {} to replenish.", selectedAccount.getId());

        String currencySymbol = selectedAccount.getAmount().currency().getSymbol();

        // Prompt user for the amount to add.
        String amountStr = Utils.readNonEmptyString(context.getScanner(), "Enter amount to add (" + currencySymbol + "): ");
        Money topUpAmount = new Money(new BigDecimal(amountStr), selectedAccount.getAmount().currency());
        logger.debug("User entered amount: '{}'", amountStr);

        // Call the service to perform the replenishment.
        context.getService().replenishAccount(selectedAccount.getId(), topUpAmount);

        System.out.println("Account replenished successfully.");
        logger.info("Successfully replenished account {} with {}.", selectedAccount.getId(), topUpAmount);

        // Return the updated account, which could be useful for pipelines.
        return CommandResult.success(selectedAccount);
    }
}
