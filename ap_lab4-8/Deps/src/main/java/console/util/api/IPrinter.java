package console.util.api;

import java.util.List;

/**
 * A generic interface for classes that can print a list of items to the console.
 * @param <T> The type of item to print.
 */
public interface IPrinter<T> {

    /**
     * Prints a formatted representation of the list of items.
     * @param items The list of items to print.
     */
    void print(final List<T> items);
}
