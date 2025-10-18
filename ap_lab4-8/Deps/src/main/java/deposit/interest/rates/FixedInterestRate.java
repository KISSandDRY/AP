package deposit.interest.rates;

import deposit.interest.api.IInterestRate;
import deposit.domain.value.PercentageRate;

import java.io.Serializable;
import java.util.Objects;

/**
 * An immutable implementation of {@link IInterestRate} that represents a fixed interest rate.
 * The rate does not change and the update method has no effect.
 */
public final class FixedInterestRate implements IInterestRate, Serializable {

    /** The fixed interest rate value. */
    private final PercentageRate rate;

    /**
     * Constructs a FixedInterestRate with the specified fixed rate.
     *
     * @param rate the fixed interest rate, must not be null
     * @throws NullPointerException if rate is null
     */
    public FixedInterestRate(final PercentageRate rate) throws NullPointerException {
        this.rate = Objects.requireNonNull(rate, "Rate cannot be null");
    }

    /**
     * Does nothing since this is a fixed rate that does not update.
     *
     * @param interestRate ignored
     * @param tax ignored
     */
    @Override
    public void update(final PercentageRate interestRate, final PercentageRate tax) { }

    /**
     * Returns the fixed interest rate.
     *
     * @return the fixed interest rate
     */
    @Override
    public PercentageRate getRate() {
        return rate;
    }

    /**
     * Returns the name of this interest rate strategy.
     *
     * @return "Fixed Interest Rate"
     */
    @Override
    public String getName() {
        return "Fixed Interest";
    }

    /**
     * Returns a string representation of this fixed interest rate with name and rate percentage.
     *
     * @return string in the format "Fixed Interest"
     */
    @Override
    public String toString() {
        return String.format("%s", getName());
    }
}
