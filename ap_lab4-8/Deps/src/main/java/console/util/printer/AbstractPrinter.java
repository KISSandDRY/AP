package console.util.printer;

import console.util.api.IPrinter;

import java.util.List;
import java.util.ArrayList;

/**
 * Abstract base class for printers that output items in a table view format.
 * 
 * @param <T> the type of items to be printed
 */
public abstract class AbstractPrinter<T> implements IPrinter<T> {

    /**
     * Default constructor.
     */
    public AbstractPrinter () { }

    /**
     * Abstract method to print a list of items.
     * Concrete subclasses must implement this method to prepare headers and rows,
     * then call {@link #printTable(List, List)} to display the table.
     *
     * @param items the list of items to print
     */
    @Override
    public abstract void print(final List<T> items);

    /**
     * Helper method that formats and prints data as a table with dynamic column widths.
     * Calculates column widths based on headers and data, renders a border, and prints the table.
     *
     * @param headers the list of column header strings
     * @param rows a list of rows, where each row is a list of string values representing columns
     */
    protected void printTable(
        final List<String> headers, 
        final List<List<String>> rows
    ) {
        if (headers.isEmpty()) 
            return;

        // 1. Calculate the maximum width for each column
        List<Integer> columnWidths = new ArrayList<>();

        for (int i = 0; i < headers.size(); i++) {
            int maxWidth = headers.get(i).length();

            for (var row : rows) 
                if (i < row.size() && row.get(i).length() > maxWidth) 
                    maxWidth = row.get(i).length();
            
            columnWidths.add(maxWidth);
        }

        // 2. Create the format string for each line (e.g., "| %-10s | %-20s |")
        // Build the format string for each row
        StringBuilder formatBuilder = new StringBuilder();
        
        for (var width : columnWidths) 
            formatBuilder.append("| %-").append(width).append("s ");
        
        formatBuilder.append("|%n");
        String formatString = formatBuilder.toString();

        // 3. Create the border line (e.g., "+------------+----------------------+")
        StringBuilder borderBuilder = new StringBuilder();

        for (var width : columnWidths) 
            borderBuilder.append("+").append("-".repeat(width + 2));
        
        borderBuilder.append("+");
        String border = borderBuilder.toString();

        // 4. Print the table. 
        // border, headers, border, rows, border
        System.out.println(border);
        System.out.printf(formatString, headers.toArray());
        System.out.println(border);

        for (var row : rows) 
            System.out.printf(formatString, row.toArray());
        
        System.out.println(border);
    }
}
