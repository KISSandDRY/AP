package deposit.domain.api;

import deposit.domain.Deposit;
import deposit.domain.value.Currency;
import deposit.interest.InterestRateType;
import deposit.interest.InterestStrategyType;

/**
 * Builder interface for creating and configuring {@link Deposit} instances.
 * Supports setting various deposit parameters such as names, currency, interest,
 * term limits, and capabilities like withdrawal and replenishment.
 * 
 * The builder allows chaining method calls and ends with {@link #build()} to create the Deposit.
 */
public interface IDepositBuilder {

    /**
     * Resets the builder to its initial state, clearing all set values.
     *
     * @return the current builder instance for chaining
     */
    IDepositBuilder reset();

    /**
     * Sets the name of the deposit.
     *
     * @param depositName the name of the deposit
     * @return the current builder instance for chaining
     */
    IDepositBuilder setDepositName(final String depositName);

    /**
     * Sets the name of the bank offering the deposit.
     *
     * @param bankName the bank name
     * @return the current builder instance for chaining
     */
    IDepositBuilder setBankName(final String bankName);

    /**
     * Sets the currency for the deposit.
     *
     * @param currency the currency of the deposit
     * @return the current builder instance for chaining
     */
    IDepositBuilder setCurrency(final Currency currency);

    /**
     * Sets the frequency in months for interest payouts.
     *
     * @param payoutFrequency frequency in months
     * @return the current builder instance for chaining
     */
    IDepositBuilder setPayoutFrequency(final int payoutFrequency);

    /**
     * Sets the interest rate and its type.
     *
     * @param rate the interest rate value
     * @param type the interest rate type
     * @return the current builder instance for chaining
     */
    IDepositBuilder setInterestRate(final double rate, final InterestRateType type);

    /**
     * Sets the interest calculation strategy.
     *
     * @param type the interest strategy type
     * @return the current builder instance for chaining
     */
    IDepositBuilder setInterestStrategy(InterestStrategyType type);

    /**
     * Sets the minimum deposit amount allowed.
     *
     * @param minAmount minimum amount
     * @return the current builder instance for chaining
     */
    IDepositBuilder setMinAmount(final double minAmount);

    /**
     * Sets the maximum deposit amount allowed.
     *
     * @param maxAmount maximum amount
     * @return the current builder instance for chaining
     */
    IDepositBuilder setMaxAmount(final double maxAmount);

    /**
     * Sets the minimum term period in months.
     *
     * @param minMonths minimum term in months
     * @return the current builder instance for chaining
     */
    IDepositBuilder setMinMonths(final int minMonths);

    /**
     * Sets the maximum term period in months.
     *
     * @param maxMonths maximum term in months
     * @return the current builder instance for chaining
     */
    IDepositBuilder setMaxMonths(final int maxMonths);

    /**
     * Sets whether early withdrawal is allowed.
     *
     * @param state true if early withdrawal is allowed, false otherwise
     * @return the current builder instance for chaining
     */
    IDepositBuilder setCanWithdrawEarly(final boolean state);

    /**
     * Sets whether replenishment of the deposit is allowed.
     *
     * @param state true if replenishment is allowed, false otherwise
     * @return the current builder instance for chaining
     */
    IDepositBuilder setCanReplenish(final boolean state);

    /**
     * Builds and returns the configured {@link Deposit} instance.
     *
     * @return the built Deposit
     */
    Deposit build();
}
