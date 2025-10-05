package console.util;

import java.util.Optional;

/**
 * Represents the result of a user selection from a list.
 * Contains either a selected item or an indication that the "All" option was selected.
 *
 * @param <T> the type of the selected item
 * @param selectedItem the optionally selected item
 * @param isAll true if the user selected the "All" option
 */
public final record SelectionResult<T>(Optional<T> selectedItem, boolean isAll) {

    /**
     * Creates a SelectionResult indicating the "All" option was selected.
     *
     * @param <T> the type parameter
     * @return a SelectionResult representing "All" selection
     */
    public static <T> SelectionResult<T> all() {
        return new SelectionResult<>(Optional.empty(), true);
    }

    /**
     * Creates a SelectionResult with a specific selected item.
     *
     * @param <T> the type parameter
     * @param item the selected item
     * @return a SelectionResult representing a single item selection
     */
    public static <T> SelectionResult<T> item(final T item) {
        return new SelectionResult<>(Optional.of(item), false);
    }
    
    /**
     * Creates a SelectionResult indicating no item was selected.
     *
     * @param <T> the type parameter
     * @return a SelectionResult representing no selection
     */
    public static <T> SelectionResult<T> none() {
        return new SelectionResult<>(Optional.empty(), false);
    }
}
