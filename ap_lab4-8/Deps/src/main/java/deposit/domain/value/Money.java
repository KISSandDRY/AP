package deposit.domain.value;

import deposit.exceptions.ValidationException;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Represents a monetary value with a specific currency.
 * Uses {@link BigDecimal} for precise decimal representation.
 * Provides operations like addition, multiplication, and comparison.
 * Ensures amount is non-negative and rounded to two decimal places.
 *
 * @param amount BigDecimal principal amount
 * @param currency Currency object
 */
public final record Money(BigDecimal amount, Currency currency) implements Comparable<Money>, Serializable {

    /**
     * Constant representing zero amount in Ukrainian Hryvnia.
     */
    public static final Money ZERO_UAH = new Money(BigDecimal.ZERO, Currency.UAH);

    /**
     * Constructs a {@code Money} instance.
     * Validates non-null and non-negative amount, rounds to two decimals using HALF_UP.
     *
     * @param amount the monetary amount, non-null and >= 0
     * @param currency the currency of the amount, non-null
     * @throws ValidationException if amount is negative
     * @throws NullPointerException if amount or currency is null
     */
    public Money {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");

        if (amount.signum() < 0) 
            throw new ValidationException("Money amount cannot be negative: " + amount);

        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Convenience constructor for creating {@code Money} from a string amount.
     *
     * @param amount the amount as a string
     * @param currency the currency of the amount
     */
    public Money(final String amount, final Currency currency) {
        this(new BigDecimal(amount), currency);
    }

    /**
     * Adds the specified {@code Money} to this one.
     * Both amounts must have the same currency.
     *
     * @param other another Money instance
     * @return a new Money instance representing the sum
     * @throws ValidationException if currencies differ
     */
    public Money add(final Money other) {
        if (!this.currency.equals(other.currency)) 
            throw new ValidationException("Cannot add money of different currencies: " + this.currency + " and " + other.currency);
        
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * Multiplies this {@code Money} by a factor.
     *
     * @param factor the multiplier as a BigDecimal
     * @return a new Money instance representing the deposit
     */
    public Money multiply(final BigDecimal factor) {
        return new Money(this.amount.multiply(factor), this.currency);
    }
    
    /**
     * Compares this {@code Money} to another for order.
     * Requires both monies to use the same currency.
     *
     * @param other the other Money instance
     * @return negative if less, zero if equal, positive if greater
     * @throws ValidationException if currencies differ
     */
    @Override
    public int compareTo(final Money other) {
        if (!this.currency.equals(other.currency)) 
            throw new ValidationException("Cannot compare money of different currencies.");
        
        return this.amount.compareTo(other.amount);
    }

    /**
     * Returns a string representation of this Money formatted with two decimals followed by the currency symbol.
     *
     * @return formatted money string, e.g. "123.45 $"
     */
    @Override
    public String toString() {
        return String.format("%.2f %s", amount, currency.getSymbol());
    }
}
