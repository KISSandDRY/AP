package deposit.interest.strategies;

import deposit.domain.value.Money;
import deposit.domain.value.TermPeriod;
import deposit.interest.api.IInterestRate;
import deposit.interest.api.IInterestStrategy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implementation of {@link IInterestStrategy} that calculates simple interest.
 * Interest is calculated linearly based on the principal, interest rate,
 * and the fraction of the term period expressed in months.
 */
public final class SimpleInterest implements IInterestStrategy, Serializable {

    private static final BigDecimal MONTHS_IN_YEAR = new BigDecimal("12");

    /**
     * Default constructor.
     */
    public SimpleInterest() { }

    /**
     * Calculates the interest profit earned using the simple interest formula:
     * Profit = Principal × Rate × (TermInMonths / 12)
     *
     * @param acount the principal amount of money
     * @param term the term period, providing the number of months
     * @param interestRate the interest rate to apply, expressed as a percentage rate
     * @return the profit (interest earned) as a Money object
     */
    @Override
    public Money calculateProfit(final Money acount, final TermPeriod term, final IInterestRate interestRate) {
        BigDecimal rate = interestRate.getRate().rate();
        BigDecimal termInMonths = new BigDecimal(term.months());

        // Formula: Profit = Acount * Rate * (TermInMonths / 12)
        BigDecimal profitAmount = acount.amount()
                .multiply(rate)
                .multiply(termInMonths)
                .divide(MONTHS_IN_YEAR, 2, RoundingMode.HALF_UP);

        return new Money(profitAmount, acount.currency());
    }

    /**
     * Calculates the total amount including the principal and the simple interest profit.
     *
     * @param amount the principal amount
     * @param term the term period
     * @param interestRate the interest rate applied
     * @return the total amount after adding interest as a Money object
     */
    @Override
    public Money calculate(final Money amount, final TermPeriod term, final IInterestRate interestRate) {
        BigDecimal profit = calculateProfit(amount, term, interestRate).amount();
        BigDecimal totalAmount = amount.amount().add(profit);

        return new Money(totalAmount, amount.currency());
    }

    /**
     * Returns the name of this interest calculation strategy.
     *
     * @return "Simple Interest"
     */
    @Override
    public String getName() { 
        return "Simple Interest"; 
    }

    /**
     * Returns a string representation of this strategy.
     *
     * @return the strategy name as a string
     */
    @Override
    public String toString() { 
        return getName(); 
    }
}
