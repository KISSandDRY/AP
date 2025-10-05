package deposit.domain.value;

import deposit.exceptions.ValidationException;

import java.io.Serializable;

/**
 * Represents a time period specified in months.
 * The term period must be a positive integer.
 * Implements {@link Comparable} for natural ordering by duration.
 *
 * @param months the number of months representing the term period; must be positive
 */
public final record TermPeriod(int months) implements Comparable<TermPeriod>, Serializable {

    /**
     * Compact constructor that validates the months value to ensure it is positive.
     *
     * @throws ValidationException if months is less than or equal to zero
     */
    public TermPeriod {
        if (months <= 0) 
            throw new ValidationException("Term period must be a positive number of months.");
    }

    /**
     * Compares this TermPeriod with another based on the number of months.
     *
     * @param other the other TermPeriod to compare against
     * @return a negative integer, zero, or a positive integer as this object is less than,
     *         equal to, or greater than the specified object.
     */
    @Override
    public int compareTo(final TermPeriod other) {
        return Integer.compare(this.months, other.months);
    }

    /**
     * Returns a string representation of the term period, formatted as "{months} months".
     *
     * @return a string representation of the term period
     */
    @Override
    public final String toString() {
        return months() + " m.";
    }
}
