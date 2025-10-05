package deposit.domain;

import deposit.domain.value.Money;
import deposit.domain.value.Range;
import deposit.domain.value.TermPeriod;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable record representing the rules and constraints of a deposit.
 * Defines valid ranges for deposit amounts and term periods, and flags indicating
 * whether early withdrawal and replenishment are allowed.
 *
 * @param amountRange the valid range for deposit amounts
 * @param termRange the valid range for deposit term periods
 * @param canWithdrawEarly indicates if early withdrawal is permitted
 * @param canReplenish indicates if replenishment of the deposit is allowed
 */
public final record DepositPolicy(
    Range<Money> amountRange,
    Range<TermPeriod> termRange,
    boolean canWithdrawEarly,
    boolean canReplenish 
) implements Serializable {

    /**
     * Compact constructor that validates non-nullity of amount and term ranges.
     *
     * @throws NullPointerException if amountRange or termRange is null
     */
    public DepositPolicy {
        Objects.requireNonNull(amountRange, "Amount range cannot be null.");
        Objects.requireNonNull(termRange, "Term range cannot be null.");
    }
}

