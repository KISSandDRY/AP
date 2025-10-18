package deposit.interest.rates;

import deposit.integration.nbu.NBU;
import deposit.domain.value.PercentageRate;
import deposit.interest.api.IInterestRate;

import java.io.IOException;
import java.io.Serializable;
import java.io.ObjectInputStream;
import java.math.BigDecimal;

/**
 * An implementation of {@link IInterestRate} representing a floating interest rate.
 * The rate is dynamically calculated based on the National Bank's interest rate
 * plus a fixed delta determined at construction.
 * This class subscribes to NBU updates to adjust its rate accordingly.
 */
public final class FloatingInterestRate implements IInterestRate, Serializable {

    // A small constant to ensure the rate is always greater than zero.
    private static final BigDecimal MIN_RATE = new BigDecimal("0.0001");
   
    // The new maximum rate constraint.
    private static final BigDecimal MAX_RATE = new BigDecimal("0.5");

    /**
     * The current floating interest rate.
     */
    private PercentageRate currentRate;

    /**
     * The fixed delta added to the NBU base rate to derive this floating rate.
     */
    private final BigDecimal delta;

    /**
     * Constructs a FloatingInterestRate based on a desired target rate.
     * It calculates the delta relative to the current NBU interest rate
     * and subscribes to NBU updates to keep the rate current.
     *
     * @param wantedRate the target interest rate this floating rate aims to approximate
     */
    public FloatingInterestRate(final PercentageRate wantedRate) {
        this.delta = calculateDelta(wantedRate);
        subscribeAndInitialize();
    }

    @Override
    public void update(final PercentageRate nbuRate, final PercentageRate tax) {
        updateRate(nbuRate);
    }

    /**
     * Recalculates the current rate and clamps it between a small positive value and 0.5.
     *
     * @param nbuRate the latest NBU interest rate
     */
    private void updateRate(final PercentageRate nbuRate) {
        BigDecimal newCalculatedRate = nbuRate.rate().add(delta)
            .max(MIN_RATE)
            .min(MAX_RATE);  

        this.currentRate = new PercentageRate(newCalculatedRate);
    }

    private BigDecimal calculateDelta(PercentageRate wantedRate) {
        PercentageRate nbuRate = NBU.getInstance().getInterestRate();
        return wantedRate.rate().subtract(nbuRate.rate());
    }

    private void subscribeAndInitialize() {
        NBU.getInstance().subscribe(this);

        // Initialize its rate based on the current NBU rate.
        updateRate(NBU.getInstance().getInterestRate());
    }

    /**
     * This special method is called automatically by the JVM during deserialization.
     * It allows the object to re-initialize its transient state or re-establish
     * runtime connections, such as subscribing to the NBU singleton.
     *
     * @param in the ObjectInputStream to read the object's fields from.
     * @throws IOException if an I/O error occurs.
     * @throws ClassNotFoundException if the class of a serialized object cannot be found.
     */
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        // Step 1: Perform the default deserialization to restore the fields (delta, currentRate).
        in.defaultReadObject();

        // Step 2: "Wake up" the object by re-subscribing it to the live NBU singleton
        // and initializing its rate to the LATEST NBU value.
        subscribeAndInitialize();
    }

    /**
     * Returns the current floating interest rate.
     *
     * @return the current interest rate
     */
    @Override
    public PercentageRate getRate() {
        return this.currentRate;
    }

    /**
     * Returns the name of this interest rate strategy.
     *
     * @return "Floating Interest Rate"
     */
    @Override
    public String getName() {
        return "Floating Interest";
    }

    /**
     * Returns a string representation with the name and current rate percentage.
     *
     * @return string in the format "Floating Interest"
     */
    @Override
    public String toString() {
        return String.format("%s", getName());
    }
}
