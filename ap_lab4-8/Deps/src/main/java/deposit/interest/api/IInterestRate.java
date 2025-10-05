package deposit.interest.api;

import deposit.domain.value.PercentageRate;
import deposit.integration.nbu.api.INBUSubscriber;

import java.math.BigDecimal;

/**
 * Interface representing an interest rate as a PercentageRate object.
 * Extends INBUSubscriber for integration with NBU (National Bank) updates.
 */
public interface IInterestRate extends INBUSubscriber {

    /**
     * Returns the current interest rate as a PercentageRate object.
     *
     * @return the current interest rate
     */
    PercentageRate getRate();

    /**
     * Convenience method to get the raw BigDecimal value of the interest rate.
     * This is equivalent to calling {@code getRate().rate()}.
     *
     * @return the raw BigDecimal representation of the interest rate
     */
    default BigDecimal getRateValue() {
        return getRate().rate();
    }


    /**
     * Gets the name of this interest rate type.
     * 
     * @return a string representing the name of the interest rate
     */
    String getName();
}
