package deposit.interest.strategies;

import deposit.domain.value.Money;
import deposit.domain.value.TermPeriod;
import deposit.domain.value.PercentageRate;
import deposit.integration.nbu.NBU;
import deposit.integration.nbu.api.INBUSubscriber;
import deposit.interest.api.IInterestRate;
import deposit.interest.api.IInterestStrategy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Decorator implementation of {@link IInterestStrategy} that applies tax to the calculated interest.
 * It wraps another interest strategy and reduces profits and total amounts by a tax rate
 * retrieved and updated via {@link NBU} subscription.
 */
public final class TaxedInterest implements INBUSubscriber, IInterestStrategy, Serializable {

    /**
     * The decorated interest calculation strategy.
     */
    private final IInterestStrategy decoratedStrategy;

    /**
     * The current tax rate applied to profits.
     */
    private PercentageRate tax;

    /**
     * Constructs a TaxedInterest decorator wrapping the given interest strategy.
     * Subscribes to the NBU for tax rate updates.
     *
     * @param decoratedStrategy the base interest strategy to decorate, must not be null
     * @throws NullPointerException if decoratedStrategy is null
     */
    public TaxedInterest(final IInterestStrategy decoratedStrategy) {
        this.decoratedStrategy = Objects.requireNonNull(decoratedStrategy, "Decorated strategy cannot be null");
        NBU.getInstance().subscribe(this);
        this.tax = NBU.getInstance().getTax();
    }

    /**
     * Updates the current tax rate from the NBU subscription.
     *
     * @param interestRate ignored here
     * @param tax the updated tax rate to be applied
     */
    @Override
    public void update(final PercentageRate interestRate, final PercentageRate tax) {
        this.tax = tax;
    }


    /**
     * Calculates the net profit after applying tax to the profit
     * calculated by the decorated interest strategy.
     *
     * @param amount the principal amount
     * @param term the term period
     * @param rate the interest rate
     * @return the net profit after tax as a Money object
     */
    @Override
    public Money calculateProfit(final Money amount, final TermPeriod term, final IInterestRate rate) {
        Money grossProfit = decoratedStrategy.calculateProfit(amount, term, rate);
        
        // Net Profit = Gross Profit * (1 - tax)
        BigDecimal taxMultiplier = BigDecimal.ONE.subtract(tax.rate());
        Money netProfit = grossProfit.multiply(taxMultiplier);
        
        return netProfit;
    }

    /**
     * Calculates the total amount (principal plus interest) after applying tax.
     *
     * @param amount the principal amount
     * @param term the term period
     * @param rate the interest rate
     * @return the net total amount after tax as a Money object
     */
    @Override
    public Money calculate(final Money amount, final TermPeriod term, final IInterestRate rate) { 
        Money grossAmount = decoratedStrategy.calculate(amount, term, rate);
        
        // Net Profit = Gross Profit * (1 - tax)
        BigDecimal taxMultiplier = BigDecimal.ONE.subtract(tax.rate());
        Money netAmount = grossAmount.multiply(taxMultiplier);
        
        return netAmount;
    }

    /**
     * Returns the name of this strategy, including indication that tax is applied.
     *
     * @return decorated strategy name appended with " (After Tax)"
     */
    @Override
    public String getName() {
        return decoratedStrategy.getName() + " (After Tax)";
    }

    /**
     * Returns a string representation of this taxed interest strategy.
     *
     * @return the strategy name as a string
     */
    @Override
    public String toString() {
        return getName();
    }
}
