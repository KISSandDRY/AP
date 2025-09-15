package negfib;

/**
 * The NegFib class calculates negative Fibonacci value based on index.
 * <p>
 * Negative Fibonacci numbers:
 * F_-n = (-1)^(n+1)*F_n
 * </p>
 */
public class NegFib {
    private final int number;
    private long value = 0;

    /**
     * Constructs a NegFib object with the given index number.
     *
     * @param number index number
     * @throws IllegalArgumentException if number is greater than 0
     */
    public NegFib(int number) {
        if (number > 0)
            throw new IllegalArgumentException("Error: expected negative number.");

        this.number = -number;

        calculateValue();
    }

    /**
     * Returns the value on index number.
     *
     * @return the value number
     */
    public long getValue() {
        return value;
    }

    /**
     * Returns the index number of NegFib sequince.
     *
     * @return the index number
     */
    public int getNumber() {
        return number;
    }

    private void calculateValue() {
        long a = 0, b = 1;

        if (number >= 1)
            value = 1;

        for (int i = 1; i < number; i++) {
            value = a + b;
            a = b;
            b = value;
        }

        if ((number & 1) == 0)
            value *= -1;
    }
}
