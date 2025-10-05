package deposit.interest.api;

import deposit.interest.InterestRateType;
import deposit.interest.InterestStrategyType;
import deposit.domain.value.PercentageRate;

/**
 * Factory interface for creating interest rate objects and interest strategies.
 * Defines methods for generating interest rates of specified types and corresponding strategies.
 */
public interface IInterestFactory {

    /**
     * Creates an interest rate object of a specific type and value.
     *
     * @param type the type of interest rate (e.g., FIXED, FLOATING)
     * @param value the rate value as a PercentageRate
     * @return an instance of {@link IInterestRate}
     */
    IInterestRate createRate(final InterestRateType type, final PercentageRate value);

    /**
     * Creates an interest calculation strategy of a specific type.
     *
     * @param type the strategy type (e.g., SIMPLE, COMPOUND)
     * @return an instance of {@link IInterestStrategy}
     */
    IInterestStrategy createStrategy(final InterestStrategyType type);
}
