package console.util.printer;

import deposit.domain.Deposit;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Printer implementation for displaying a list of {@link Deposit} objects in a table format.
 * Extends {@link AbstractPrinter} and uses its table printing capabilities.
 */
public final class ListDepositsPrinter extends AbstractPrinter<Deposit> {

    /**
     * Default constructor.
     */
    public ListDepositsPrinter() { }
    
    /**
     * Prints a list of deposits as a formatted table including columns for ID, bank name,
     * deposit name, interest rate, and policy flags for replenishment and early withdrawal.
     * Prints a message when the list is empty.
     *
     * @param items the list of {@link Deposit} objects to print
     */
    @Override
    public void print(final List<Deposit> items) {
        if (items.isEmpty()) {
            System.out.println("No deposits found.");
            return;
        }

        List<String> headers = Arrays.asList("ID", "Bank", "Deposit Name", "Currency", "Amount", "Term", "Rate", "Strategy", "Replenish", "Withdraw");
        List<List<String>> rows = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            var d = items.get(i);

            rows.add(Arrays.asList(
                String.valueOf(i),
                d.getInfo().bankName(),
                d.getInfo().depositName(),
                d.getInfo().currency().name(),
                d.getPolicy().amountRange().toString(),
                d.getPolicy().termRange().toString(),
                d.getInterestRate().toString(),
                d.getInterestStrategy().toString(),
                d.getPolicy().canReplenish() ? "Yes" : "No",
                d.getPolicy().canWithdrawEarly() ? "Yes" : "No"
            ));
        }
        
        printTable(headers, rows);
    }
}
