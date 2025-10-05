package console.util.printer;

import deposit.domain.DepositAccount;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Printer implementation for displaying a list of {@link DepositAccount} objects in a table format.
 * Extends {@link AbstractPrinter} to leverage table printing utilities.
 */
public final class ListAccountsPrinter extends AbstractPrinter<DepositAccount> {

    /**
     * Default constructor.
     */
    public ListAccountsPrinter() { }

    /**
     * Prints the list of deposit accounts as a formatted table.
     * If the list is empty, a message indicating no accounts are found is printed.
     *
     * @param items the list of DepositAccount objects to print
     */
    @Override
    public void print(final List<DepositAccount> items) {
        if (items.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        List<String> headers = Arrays.asList("Index", "Account ID", "Deposit Name", "Bank Name", "Amount", "Term");
        List<List<String>> rows = new ArrayList<>();

        for (int i = 0; i < items.size(); i++) {
            var acc = items.get(i);

            rows.add(Arrays.asList(
                String.valueOf(i),
                acc.getId().toString().substring(0, 8),
                acc.getDeposit().getInfo().depositName(),
                acc.getDeposit().getInfo().bankName(),
                acc.getAmount().toString(),
                acc.getTerm().toString()
            ));
        }

        printTable(headers, rows);
    }
}
