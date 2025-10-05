package deposit.interest.strategies;

import deposit.domain.value.Money;
import deposit.domain.value.TermPeriod;
import deposit.interest.api.IInterestRate;
import deposit.interest.api.IInterestStrategy;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Implementation of {@link IInterestStrategy} that calculates compound interest.
 * Interest is compounded monthly based on the annual interest rate provided.
 */
public final class CompoundInterest implements IInterestStrategy, Serializable {

    private static final BigDecimal MONTHS_IN_YEAR = new BigDecimal("12");
    private static final int CALCULATION_SCALE = 10; // Precision for intermediate calculations
    
    /**
     * Default constructor.
     */
    public CompoundInterest() { }

    /**
     * Calculates the total amount after applying compound interest monthly for the given term.
     *
     * @param amount the principal amount of money
     * @param term the term period of the deposit/loan in months
     * @param interestRate the interest rate to apply, expressed as a percentage rate
     * @return the total amount including the principal and compounded interest
     */
    @Override
    public Money calculate(final Money amount, final TermPeriod term, final IInterestRate interestRate) {
        BigDecimal annualRate = interestRate.getRate().rate();
        BigDecimal monthlyRate = annualRate.divide(MONTHS_IN_YEAR, CALCULATION_SCALE, RoundingMode.HALF_UP);
        int termInMonths = term.months();

        // Formula: Total = Amount * (1 + MonthlyRate)^TermInMonths
        BigDecimal totalAmount = amount.amount().multiply(
            BigDecimal.ONE.add(monthlyRate).pow(termInMonths)
        );

        return new Money(totalAmount, amount.currency());
    }

    /**
     * Calculates the profit earned from compound interest over the term.
     * This is the total amount minus the principal.
     *
     * @param amount the principal amount
     * @param term the term period
     * @param interestRate the interest rate applied
     * @return the interest profit earned as Money
     */
    @Override
    public Money calculateProfit(final Money amount, final TermPeriod term, final IInterestRate interestRate) {

        // Profit = Total amount - Principal amount
        BigDecimal profitAmount = calculate(amount, term, interestRate).amount().subtract(amount.amount());

        return new Money(profitAmount, amount.currency());
    }

    /**
     * Returns the name of this interest calculation strategy.
     *
     * @return "Compound Interest"
     */
    @Override
    public String getName() { 
        return "Compound Interest"; 
    }

    /**
     * Returns a string representation of this strategy.
     *
     * @return the strategy name as string
     */
    @Override
    public String toString() { 
        return getName(); 
    }
}
