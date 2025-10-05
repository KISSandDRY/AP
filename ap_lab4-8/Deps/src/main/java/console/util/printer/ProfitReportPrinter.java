package console.util.printer;

import deposit.domain.DepositAccount;
import deposit.service.DepositService;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

/**
 * Printer class that generates a profit report for a list of {@link DepositAccount} objects.
 * Utilizes {@link DepositService} to calculate gross and net profits, and displays results in a table format.
 */
public class ProfitReportPrinter extends AbstractPrinter<DepositAccount> {

    private final DepositService service;

    /**
     * Constructs a ProfitReportPrinter with the given deposit service.
     *
     * @param service the DepositService used for profit calculations
     */
    public ProfitReportPrinter(final DepositService service) {
        this.service = service;
    }

    /**
     * Prints a profit report table including account ID, amount, gross profit, and net profit after tax.
     * Prints a message if the input list is empty.
     *
     * @param items the list of DepositAccount objects to generate the report for
     */
    @Override
    public void print(final List<DepositAccount> items) {
        if (items.isEmpty()) {
            System.out.println("No accounts to generate a report for.");
            return;
        }
        
        List<String> headers = Arrays.asList("Account ID", "Amount", "Gross Profit", "Net Profit (After Tax)", "Total Amount (Before Tax)");
        List<List<String>> rows = new ArrayList<>();
        
        for (var acc : items) {
            var grossProfit = service.calculateGrossProfit(acc).amount();
            var netProfit = service.calculateNetProfit(acc).amount();

            rows.add(Arrays.asList(
                acc.getId().toString().substring(0, 8),
                acc.getAmount().toString(),
                grossProfit.toString(),
                netProfit.toString(),
                acc.calculate().toString()
            ));
        }

        printTable(headers, rows);
    }
}
