package console.commands.deposit;

import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.exceptions.CommandCancelledException;
import console.util.printer.ListDepositsPrinter;
import console.util.Utils;
import console.util.ParsedArgs;
import deposit.domain.value.Money;
import deposit.domain.value.Currency;
import deposit.domain.value.TermPeriod;

import java.math.BigDecimal;

/**
 * A command that suggests suitable deposit products based on user-provided criteria,
 * such as investment amount and term.
 */
public class SuggestDepositsCommand extends AbstractCommand {

    /**
     * Default constructor.
     */
    public SuggestDepositsCommand() { }

    @Override
    public String getName() { 
        return "suggest"; 
    }

    @Override
    public String getDescription() { 
        return "Suggests best deposits to open."; 
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
            // Step 1. Get user input
            Currency selectedCurrency = Utils.readCurrency(context.getScanner());
            String amountStr = Utils.readNonEmptyString(context.getScanner(), "Enter your desired investment amount (e.g., 50000): ");
            int termMonths = Utils.readInt(context.getScanner(), "Enter your desired term in months (e.g., 12): ");

            // Step 2. Make objects
            Money amount = new Money(new BigDecimal(amountStr), selectedCurrency);
            TermPeriod term = new TermPeriod(termMonths);

            // Step 3. Get suggestions
            var suggested = context.getService().getSuggestions(amount, term);

            // Step 4. Print suggestions
            System.out.println("\nSuggested deposits based on your criteria (best match first):");

            if (suggested.isEmpty()) 
                System.out.println("No deposits match your specified amount and term in " + selectedCurrency + ".");
            else 
                new ListDepositsPrinter().print(suggested);

            return CommandResult.success();

        } catch (CommandCancelledException e) {
            return CommandResult.failure("Suggestion cancelled.");

        } catch (Exception e) {
            return CommandResult.failure("Could not get suggestions: " + e.getMessage());
        }
    }
}
