package console.commands.account;

import console.CommandResult;
import console.CommandContext;
import console.util.Utils;
import console.util.ParsedArgs;
import console.util.SelectionResult;
import console.util.printer.ListAccountsPrinter;
import console.util.printer.ProfitReportPrinter;
import console.commands.api.AbstractCommand;
import console.exceptions.EmptyListException;
import console.exceptions.CommandCancelledException;
import deposit.domain.DepositAccount;

import java.util.List;
import java.util.Optional;
import java.util.Collections;

/**
 * Calculates profit for one or more deposit accounts.
 * It can process a list of accounts passed from a previous command in a pipeline
 * or interactively prompt the user to select accounts if no pipeline data is present.
 */
public class CalcAccountProfitCommand extends AbstractCommand {

    /**
     * Default constructor.
     */
    public CalcAccountProfitCommand() { }

    @Override
    public String getName() { 
        return "calc"; 
    }

    @Override
    public String getDescription() { 
        return "Calculate profit for one or all accounts (with or without tax)"; 

    }
    
    @Override
    public String getUsage() {
        return getName() + " \n  (Can be piped data from another command, like 'list --accounts | calc')";
    }

    @Override
    public CommandResult executeLogic(
        final CommandContext context, 
        final ParsedArgs args
    ) {
        try {
            // Determine which accounts to process (from pipeline or user selection).
            List<DepositAccount> accountsToProcess = getAccountsToProcess(context);

            if (accountsToProcess.isEmpty()) {
                System.out.println("No accounts were selected for profit calculation.");

                return CommandResult.success();
            }

            // Generate and print the profit report.
            new ProfitReportPrinter(context.getService()).print(accountsToProcess);

            return CommandResult.success();

        } catch (EmptyListException e) {
            return CommandResult.failure("No accounts are available to calculate profit.");

        } catch (CommandCancelledException e) {
            return CommandResult.failure("Profit calculation was cancelled.");

        } catch (Exception e) {
            // Catch any other unexpected errors during execution.
            return CommandResult.failure("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Retrieves the list of accounts to be processed, prioritizing data from the pipeline.
     * If no valid pipeline data is found, it prompts the user for a selection.
     *
     * @param context The current command context.
     * @return A list of {@link DepositAccount} to process.
     * @throws EmptyListException if no accounts exist in the system for interactive selection.
     * @throws CommandCancelledException if the user cancels the interactive selection.
     */
    @SuppressWarnings("unchecked")
    private List<DepositAccount> getAccountsToProcess(final CommandContext context) {

        // First, try to get a valid list of accounts from the pipeline.
        Optional<List<DepositAccount>> pipelineAccounts = context.getPipelineData(List.class)
            .filter(list -> !list.isEmpty() && list.get(0) instanceof DepositAccount)
            .map(list -> (List<DepositAccount>) list);

        if (pipelineAccounts.isPresent()) {
            List<DepositAccount> accounts = pipelineAccounts.get();

            return accounts;
        }

        // If no pipeline data, fall back to interactive selection.
        return selectAccountsInteractively(context);
    }

    /**
     * Handles the interactive user flow for selecting accounts.
     *
     * @param context The current command context.
     * @return A list of selected {@link DepositAccount}.
     */
    private List<DepositAccount> selectAccountsInteractively(final CommandContext context) {
        List<DepositAccount> allAccounts = context.getService().getAllAccounts();
        if (allAccounts.isEmpty()) 
            throw new EmptyListException("No accounts found.");

        SelectionResult<DepositAccount> selection = Utils.selectFromList(
            context.getScanner(),
            allAccounts,
            new ListAccountsPrinter(),
            "Select an account to calculate profit for",
            true // Allow the user to select "All"
        );

        if (selection.isAll()) 
            return allAccounts;

        return selection.selectedItem()
            .map(List::of) // If an item is present, wrap it in a List
            .orElse(Collections.emptyList()); // Otherwise, return an empty list
    }
}
