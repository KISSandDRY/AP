package console.commands.account;

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
import deposit.domain.DepositAccount;
import deposit.domain.value.Money;
import deposit.domain.value.Currency;
import deposit.domain.value.TermPeriod;

import java.math.BigDecimal;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * A command to open a new deposit account.
 * This command guides the user through selecting a deposit type, then entering
 * the initial amount and term for the new account.
 */
public class OpenAccountCommand extends AbstractCommand {
    
    private static final Logger logger = LogManager.getLogger(OpenAccountCommand.class);

    /**
     * Default constructor.
     */
    public OpenAccountCommand() { }

    @Override
    public String getName() { 
        return "open"; 
    }

    @Override
    public String getDescription() { 
        return "Opens an account with specified deposit."; 
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
        logger.debug("Executing OpenAccountCommand.");

        try {
            // Step 1: Prompt the user to select a deposit product.
            SelectionResult<Deposit> selectionResult = Utils.selectFromList(
                context.getScanner(),
                context.getService().getAllDeposits(),
                new ListDepositsPrinter(),
                "Select a deposit product to open an account for",
                false // A specific deposit must be chosen.
            );

            // Step 2: Process the selection.
            return selectionResult.selectedItem()
                .map(selectedDeposit -> handleAccountOpening(context, selectedDeposit))
                .orElse(CommandResult.failure("No deposit was selected."));

        } catch (EmptyListException e) {
            logger.warn("Open account operation failed: No deposit products are available to select from.");
            return CommandResult.failure("No deposit products are available to open an account from.");

        } catch (CommandCancelledException e) {
            logger.info("User cancelled the open account operation.");
            return CommandResult.failure("Account opening was cancelled.");

        } catch (Exception e) {
            logger.error("An unexpected error occurred while opening an account.", e);
            return CommandResult.failure("Could not open account: " + e.getMessage());
        }
    }

    /**
     * Handles the user interaction for gathering account details after a deposit has been selected.
     *
     * @param context The command context, providing access to the scanner and service.
     * @param selectedDeposit The deposit product the user has chosen.
     * @return A {@link CommandResult} indicating the outcome of the operation.
     */
    private CommandResult handleAccountOpening(CommandContext context, Deposit selectedDeposit) {
        logger.info("User selected deposit '{}' (ID: {}) to open an account.", 
            selectedDeposit.getInfo().depositName(), selectedDeposit.getId());

        Currency currency = selectedDeposit.getInfo().currency();

        // Gather necessary details from the user.
        String amountStr = Utils.readNonEmptyString(context.getScanner(), "Enter initial deposit amount (" + currency.getSymbol() + "): ");
        int termMonths = Utils.readInt(context.getScanner(), "Enter term in months: ");
        logger.debug("User entered amount: '{}', term: {}", amountStr, termMonths);

        // Create value objects from user input.
        Money initialAmount = new Money(new BigDecimal(amountStr), currency);
        TermPeriod term = new TermPeriod(termMonths);

        // Call the service to open the account.
        DepositAccount newAccount = context.getService().openAccount(selectedDeposit.getId(), initialAmount, term);

        String shortId = newAccount.getId().toString().substring(0, 8);
        System.out.println("\nAccount opened successfully! New Account ID: " + shortId);
        logger.info("Successfully opened new account with ID {}.", newAccount.getId());

        return CommandResult.success(newAccount);
    }
}
