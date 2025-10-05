package deposit.exceptions;

import java.util.UUID;

/**
 * Exception thrown when a deposit or account with a specified ID cannot be found.
 * Includes a shortened representation of the missing entity's UUID in the message.
 */
public final class DepositNotFoundException extends DepositException {

    /**
     * Constructs a DepositNotFoundException for the given UUID.
     *
     * @param id the UUID of the deposit or account that was not found
     */
    public DepositNotFoundException(final UUID id) {
        super("No deposit or account found with ID: " + id.toString().substring(0, 8));
    }
}
