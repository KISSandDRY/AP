package deposit.domain.value;

import deposit.exceptions.ValidationException;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Represents a percentage rate stored as a decimal value between 0 and 1.
 * Enforces validation on construction to ensure rate is within bounds.
 *
 * @param rate BigDecimal
 */
public final record PercentageRate(BigDecimal rate) implements Serializable {

    /**
     * Compact constructor that validates the rate is between 0 and 1 inclusive.
     *
     * @throws ValidationException if the rate is less than 0 or greater than 1
     */
    public PercentageRate {
        if (rate.compareTo(BigDecimal.ZERO) < 0 || rate.compareTo(BigDecimal.ONE) > 0) 
            throw new ValidationException("Percentage rate must be between 0 and 1.");
    }
    
    /**
     * Creates a PercentageRate from a string representation of the decimal rate.
     *
     * @param rate the decimal rate as string, e.g. "0.12"
     */
    public PercentageRate(final String rate) {
        this(new BigDecimal(rate));
    }

    /**
     * Returns a string representation of the percentage rate as a percentage with two decimals.
     *
     * @return formatted percentage string, e.g. "12.00%"
     */
    @Override
    public String toString() {
        return String.format("%.2f%%", rate.doubleValue() * 100);
    }
}
