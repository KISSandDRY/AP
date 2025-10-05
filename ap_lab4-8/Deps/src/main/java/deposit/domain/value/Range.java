package deposit.domain.value;

import java.io.Serializable;

/**
 * A generic, serializable record representing an inclusive range between a minimum and maximum value.
 * Supports checking if a given value falls within the range.
 * 
 * @param <T> the type of values in the range, must be {@link Comparable}
 * @param min the minimum value of the range (inclusive), may be null to represent no lower bound
 * @param max the maximum value of the range (inclusive), may be null to represent no upper bound
 */
public final record Range<T extends Comparable<T>>(T min, T max) implements Serializable {

    /**
     * Checks whether the specified value lies within this range.
     * 
     * Null min is treated as no lower bound; null max as no upper bound.
     * 
     * @param value the value to check
     * @return true if value is greater than or equal to min and less than or equal to max
     */
    public boolean contains(final T value) {
        boolean afterMin = min == null || value.compareTo(min) >= 0;

        // max can be null for "no upper limit"
        boolean beforeMax = max == null || value.compareTo(max) <= 0;

        return afterMin && beforeMax;
    }

    /**
     * Returns a compact string representation of the range using interval notation.
     *
     * @return a formatted string like "[min, max]", "[min, ∞)", "(-∞, max]", or "(-∞, ∞)".
     */
    @Override
    public String toString() {
        String lowerBound = (min != null) ? min.toString() : "-∞";
        String upperBound = (max != null) ? max.toString() : "∞";

        return String.format("[%s, %s]", lowerBound, upperBound);
    }
}
