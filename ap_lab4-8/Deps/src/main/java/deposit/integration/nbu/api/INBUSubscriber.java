package deposit.integration.nbu.api;

import deposit.domain.value.PercentageRate;

/**
 * Defines a component that can receive updates about NBU rates.
 */
public interface INBUSubscriber {
    /**
     * Called by the NBU singleton when rates are updated.
     * @param interestRate The new base interest rate.
     * @param tax The new tax.
     */
    void update(final PercentageRate interestRate, final PercentageRate tax);
}
