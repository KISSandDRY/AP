package deposit.interest.api;

import deposit.domain.value.Money;
import deposit.domain.value.TermPeriod;

/**
 * Strategy interface for interest calculation.
 * Provides methods to calculate profit and total interest amount
 * based on an amount, term period, and interest rate.
 */
public interface IInterestStrategy {

    /**
     * Calculates the profit (interest earned) on a principal amount
     * over a given term period using a specified interest rate.
     * 
     * @param amount the principal amount on which interest is calculated
     * @param term the term period for which interest is calculated
     * @param interestRate the interest rate to apply
     * @return the calculated profit as a Money object
     */
    Money calculateProfit(final Money amount, final TermPeriod term, final IInterestRate interestRate);

    /**
     * Calculates the total amount including the principal and interest 
     * based on the given amount, term, and interest rate.
     * 
     * @param amount the principal amount
     * @param term the term period
     * @param interestRate the interest rate applied to the amount
     * @return the total amount including interest as a Money object
     */
    Money calculate(final Money amount, final TermPeriod term, final IInterestRate interestRate);

    /**
     * Gets the name of this interest calculation strategy.
     * 
     * @return a string representing the name of the strategy
     */
    String getName();
}
