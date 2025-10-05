package deposit.domain;

import deposit.domain.api.IDepositBuilder;
import deposit.domain.value.Money;
import deposit.domain.value.Range;
import deposit.domain.value.Currency;
import deposit.domain.value.PercentageRate;
import deposit.domain.value.TermPeriod;
import deposit.interest.InterestFactory;
import deposit.interest.InterestRateType;
import deposit.interest.InterestStrategyType;
import deposit.interest.api.IInterestFactory;

import java.math.BigDecimal;

/**
 * Builder implementation for creating {@link Deposit} instances with flexible configuration.
 * Supports setting deposit properties such as name, bank, currency, payout frequency,
 * interest rate and strategy, term and amount ranges, and additional policy features.
 * 
 * Uses {@link InterestFactory} internally to create interest rate and strategy instances.
 * Implements the {@link IDepositBuilder} interface to allow fluent method chaining.
 */
public final class DepositBuilder implements IDepositBuilder {

    private final IInterestFactory interestFactory;

    private InterestStrategyType interestStrategyType;
    private InterestRateType interestRateType;
    private double interestRateValue;
    private String depositName;
    private String bankName;
    private Currency currency;
    private int payoutFrequency;
    private double minAmount;
    private double maxAmount;
    private int minMonths;
    private int maxMonths;
    private boolean canWithdrawEarly;
    private boolean canReplenish;

    /**
     * Creates a new DepositBuilder and resets it to default values.
     */
    public DepositBuilder() {
        this.interestFactory = new InterestFactory();
        reset();
    }

    /**
     * Resets this builder to default state with sensible defaults.
     *
     * @return the builder instance for chaining
     */
    @Override
    public IDepositBuilder reset() {
        depositName = null; bankName = null;
        minAmount = maxAmount = 0.0; // 0 means unlimited
        minMonths = 1; maxMonths = 0; // 0 means unlimited
        canWithdrawEarly = false; canReplenish = false;
        interestRateValue = 0.05; currency = Currency.UAH;
        payoutFrequency = 12;
        interestRateType = InterestRateType.FIXED;
        interestStrategyType = InterestStrategyType.SIMPLE;

        return this;
    }

    @Override
    public IDepositBuilder setDepositName(final String depositName) {
        this.depositName = depositName; return this;
    }

    @Override
    public IDepositBuilder setBankName(final String bankName) {
        this.bankName = bankName; return this;
    }

    @Override
    public IDepositBuilder setCurrency(final Currency currency) {
        this.currency = currency; return this;
    }

    @Override
    public IDepositBuilder setPayoutFrequency(final int payoutFrequency) {
        this.payoutFrequency = payoutFrequency; return this;
    }

    @Override
    public IDepositBuilder setMinAmount(final double minAmount) {
        this.minAmount = minAmount; return this;
    }

    @Override
    public IDepositBuilder setMaxAmount(final double maxAmount) {
        this.maxAmount = maxAmount; return this;
    }

    @Override
    public IDepositBuilder setMinMonths(final int minMonths) {
        this.minMonths = minMonths; return this;
    }

    @Override
    public IDepositBuilder setMaxMonths(final int maxMonths) {
        this.maxMonths = maxMonths; return this;
    }

    @Override
    public IDepositBuilder setCanWithdrawEarly(boolean state) {
        this.canWithdrawEarly = state; return this;
    }

    @Override
    public IDepositBuilder setCanReplenish(boolean state) {
        this.canReplenish = state; return this;
    }

    @Override
    public IDepositBuilder setInterestStrategy(final InterestStrategyType type) {
        this.interestStrategyType = type; return this;
    }

    @Override
    public IDepositBuilder setInterestRate(final double rate, final InterestRateType type) {
        this.interestRateValue = rate;
        this.interestRateType = type;
        return this;
    }

    /**
     * Builds a new {@link Deposit} instance from the configured values.
     *
     * @return a new Deposit configured with the builder's parameters
     */
    @Override
    public Deposit build() {
        DepositInfo info = new DepositInfo(depositName, bankName, currency, payoutFrequency);

        Money minMoney = new Money(BigDecimal.valueOf(minAmount), currency);
        Money maxMoney = (maxAmount > 0) ? new Money(BigDecimal.valueOf(maxAmount), currency) : null;

        TermPeriod minTerm = new TermPeriod(minMonths);
        TermPeriod maxTerm = (maxMonths > 0) ? new TermPeriod(maxMonths) : null;

        DepositPolicy policy = new DepositPolicy(
                new Range<>(minMoney, maxMoney),
                new Range<>(minTerm, maxTerm),
                canWithdrawEarly, canReplenish
        );

        return Deposit.create(
            info, policy,
            interestFactory.createStrategy(interestStrategyType),
            interestFactory.createRate(
                interestRateType, 
                new PercentageRate(new BigDecimal(interestRateValue))
            )
        );
    }
}
