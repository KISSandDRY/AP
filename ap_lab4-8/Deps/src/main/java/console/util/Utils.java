package console.util;

import console.util.api.IPrinter;
import deposit.domain.value.Currency;
import console.exceptions.EmptyListException;
import console.exceptions.CommandCancelledException;

import java.util.List;
import java.util.Scanner;
import java.util.function.Supplier;

/**
 * Utility class providing common console input and selection helper methods.
 * <p>
 * These methods handle user input, item selection from lists, and simple type parsing
 * with cancellation and validation support.
 */
public final class Utils {

    /** Prevents instantiation of utility class. */
    private Utils() {}

    /**
     * Computes the Levenshtein distance (edit distance) between two strings.
     *
     * @param a the first string
     * @param b the second string
     * @return the computed edit distance
     */
    public static int getLevenshteinDistance(final String a, final String b) {
        return levenshtein(a, b, a.length(), b.length());
    }

    /**
     * Reads a line of text from user input.
     * <p>
     * If the user enters {@code "cancel"} or {@code "exit"}, a
     * {@link CommandCancelledException} is thrown.
     *
     * @param scanner the scanner used for input
     * @param prompt  the message shown to the user
     * @return the trimmed user input
     * @throws CommandCancelledException if user cancels the input
     */
    public static String readLine(
        final Scanner scanner, 
        final String prompt
    ) throws CommandCancelledException {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();

        if (input.equalsIgnoreCase("cancel") || input.equalsIgnoreCase("exit")) 
            throw new CommandCancelledException();
        
        return input;
    }

    /**
     * Prompts the user to select an item from a list.
     *
     * @param scanner         the scanner used for input
     * @param items           the list of items to select from
     * @param printer         the printer used to display items
     * @param prompt          the message shown to the user
     * @param allowAllOption  if {@code true}, includes an "All" option
     * @param <T>             the type of items in the list
     * @return a {@link SelectionResult} representing the user’s choice
     * @throws EmptyListException if the provided list is empty
     */
    public static <T> SelectionResult<T> selectFromList(
        final Scanner scanner, 
        final List<T> items, 
        final IPrinter<T> printer, 
        final String prompt, 
        final boolean allowAllOption
    ) {
        printer.print(items);

        if (items.isEmpty()) 
            throw new EmptyListException("The list of items to select from was empty.");

        if (allowAllOption) 
            System.out.println("[A] All items");
        
        while (true) {
            String choicePrompt = allowAllOption ? "Choose an ID or 'A' for all" : "Choose an ID";
            String input = readLine(scanner, prompt + " (" + choicePrompt + "): ").trim();

            if (allowAllOption && input.equalsIgnoreCase("A")) 
                return SelectionResult.all();

            try {
                int index = Integer.parseInt(input);
                if (index >= 0 && index < items.size()) 
                    return SelectionResult.item(items.get(index));

                System.out.println("Invalid ID. Please choose from the list.");
                
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric ID.");
            }
        }
    }

    /**
     * Fetches a list of items and prints it using a provided printer.
     * Displays an empty message if no items are found.
     *
     * @param listSupplier the supplier that returns a list of items
     * @param printer      the printer used to display the list
     * @param emptyMessage message displayed when the list is empty
     * @param <T>          the type of items in the list
     * @return the fetched list (may be empty)
     */
    public static <T> List<T> fetchAndPrint(
        final Supplier<List<T>> listSupplier, 
        final IPrinter<T> printer, 
        final String emptyMessage
    ) {
        List<T> items = listSupplier.get();
        if (items.isEmpty()) {
            System.out.println(emptyMessage);
            return items;
        }
        
        System.out.println("Available items:");
        printer.print(items);

        return items;
    }

    /**
     * Prompts the user to select a currency.
     *
     * @param scanner the scanner used for input
     * @return the selected {@link Currency}
     */
    public static Currency readCurrency(final Scanner scanner) {
        while (true) {
            System.out.println("Choose a currency for your suggestion:");
            System.out.print(Currency.menu("  "));

            String input = readLine(scanner, "Input: ").trim().toUpperCase();

            try {
                return Currency.fromCode(input);
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid currency code. Please choose one of the listed options.");
            }
        }
    }
    
    /**
     * Reads a non-empty string from user input.
     *
     * @param scanner the scanner used for input
     * @param prompt  the message shown to the user
     * @return a non-empty string
     */
    public static String readNonEmptyString(final Scanner scanner, final String prompt) {
        String input;

        do {
            input = readLine(scanner, prompt);
        } while (input.isEmpty());

        return input;
    }

    /**
     * Reads a boolean (yes/no) response from the user.
     *
     * @param scanner the scanner used for input
     * @param prompt  the message shown to the user
     * @return {@code true} if user enters "y" or "yes", otherwise {@code false}
     */
    public static boolean readBoolean(final Scanner scanner, final String prompt) {
        String input = readLine(scanner, prompt).toLowerCase();

        return input.equals("y") || input.equals("yes");
    }

    /**
     * Reads a double value from user input.
     * Keeps prompting until a valid number is entered.
     *
     * @param scanner the scanner used for input
     * @param prompt  the message shown to the user
     * @return the parsed double value
     */
    public static double readDouble(final Scanner scanner, final String prompt) {
        while (true) {
            try {
                return Double.parseDouble(readLine(scanner, prompt));
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again or type 'cancel' to abort.");
            }
        }
    }

    /**
     * Reads an integer value from user input.
     * Keeps prompting until a valid integer is entered.
     *
     * @param scanner the scanner used for input
     * @param prompt  the message shown to the user
     * @return the parsed integer value
     */
    public static int readInt(final Scanner scanner, final String prompt) {
        while (true) {
            try {
                return Integer.parseInt(readLine(scanner, prompt));
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer. Try again or type 'cancel' to abort.");
            }
        }
    }

    /**
     * Recursive implementation of the Levenshtein distance algorithm.
     *
     * @param a the first string
     * @param b the second string
     * @param i current index in {@code a}
     * @param j current index in {@code b}
     * @return the minimal edit distance
     */
    private static int levenshtein(final String a, final String b, final int i, final int j) {
        if (i == 0) return j;
        if (j == 0) return i;

        int cost = (a.charAt(i - 1) == b.charAt(j - 1)) ? 0 : 1;

        int delete = levenshtein(a, b, i - 1, j) + 1;
        int insert = levenshtein(a, b, i, j - 1) + 1;
        int substitute = levenshtein(a, b, i - 1, j - 1) + cost;

        return Math.min(delete, Math.min(insert, substitute));
    }
}
