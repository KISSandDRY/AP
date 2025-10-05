package deposit.domain;

import deposit.domain.api.IStorable;
import deposit.domain.value.Money;
import deposit.domain.value.TermPeriod;
import deposit.exceptions.ValidationException;

import java.util.UUID;
import java.util.Objects;
import java.io.Serializable;

/**
 * Represents an account for a specific deposit containing the invested amount and term.
 * Supports replenishing funds and enforces validation against deposit policies.
 * Each account is uniquely identified by a UUID.
 */
public final class DepositAccount implements IStorable, Serializable {

    /** Serialization version ID. */
    private static final long serialVersionUID = 1L;

    /** Unique identifier for this account. */
    private final UUID id; 
    //TODO: make deterministic generate based on content, 
    //fix hashCode and equals for deposit and account to address 
    //issue where two or more objects with the same content but 
    //different ids

    /** The deposit associated with this account. */
    private final Deposit deposit;

    /** The term period selected for this account. */
    private final TermPeriod term;

     /** The amount of money invested in this account. */
    private Money amount;

    /**
     * Constructs a new deposit account with the given deposit, initial amount, and term.
     * Validates that the term and amount fall within the deposit's allowed ranges,
     * and that the currency matches the deposit currency.
     *
     * @param deposit the associated deposit, must not be null
     * @param initialAmount the initial invested amount, must be within allowed range and correct currency
     * @param term the term period, must be within deposit's allowed term range
     * @throws ValidationException if validation fails on amounts, term, or currency
     */
    public DepositAccount(
        final Deposit deposit, 
        final Money initialAmount, 
        final TermPeriod term
    ) throws ValidationException {
        this.id = UUID.randomUUID();
        this.deposit = Objects.requireNonNull(deposit, "Deposit cannot be null.");

        if (!deposit.getPolicy().termRange().contains(term)) 
            throw new ValidationException("Term " + term.months() + " months is outside the allowed range for this deposit.");
        
        if (!deposit.getPolicy().amountRange().contains(initialAmount)) 
            throw new ValidationException("Amount " + initialAmount + " is outside the allowed range for this deposit.");
        
        if (!deposit.getInfo().currency().equals(initialAmount.currency())) 
            throw new ValidationException("Account currency must match deposit currency.");

        this.term = term;
        this.amount = initialAmount;
    }

    /**
     * Returns the unique identifier of this deposit account.
     *
     * @return the UUID of the account
     */
    @Override
    public UUID getId() {
        return id;
    }

    /**
     * Returns the associated deposit.
     *
     * @return the deposit
     */
    public Deposit getDeposit() {
        return deposit;
    }

    /**
     * Returns the term period of this account.
     *
     * @return the term period
     */
    public TermPeriod getTerm() {
        return term;
    }

    /**
     * Returns the current amount invested in the account.
     *
     * @return the invested amount
     */
    public Money getAmount() {
        return amount;
    }

    /**
     * Replenishes the account by adding the specified amount.
     * Validates that the deposit policy allows replenishment,
     * and the new amount remains within allowed bounds.
     *
     * @param topUpAmount the amount to add
     * @throws ValidationException if replenishment is disallowed or validation fails
     */
    public void replenish(final Money topUpAmount) throws ValidationException {

        if (!deposit.getPolicy().canReplenish()) 
            throw new ValidationException("This deposit does not allow replenishment.");
        
        Money newAmount = this.amount.add(topUpAmount);
        if (!deposit.getPolicy().amountRange().contains(newAmount)) 
            throw new ValidationException("New total amount " + newAmount + " would be outside the allowed range.");
        
        this.amount = newAmount;
    }

    /**
     * Calculates the interest profit on the current amount for the specified term using the deposit's strategy and rate.
     *
     * @return the calculated interest profit as a Money object
     */
    public Money calculateProfit() {
        return deposit.getInterestStrategy()
                .calculateProfit(amount, term, deposit.getInterestRate());
    }

    /**
     * Calculates the total on the current amount for the specified term using the deposit's strategy and rate.
     *
     * @return the calculated total as a Money object
     */
    public Money calculate() {
        return deposit.getInterestStrategy()
                .calculate(amount, term, deposit.getInterestRate());
    }

    /**
     * Compares this deposit account with another for equality based on their UUIDs.
     *
     * @param o the other object to compare
     * @return true if both accounts have the same UUID
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof DepositAccount that)) return false;

        return Objects.equals(id, that.id);
    }

    /**
     * Returns the hash code for this deposit account, based on UUID.
     *
     * @return hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of this deposit account showing key fields.
     *
     * @return a string representing the deposit account
     */
    @Override
    public String toString() {
        return "\"" + this.getClass().getName() + "\": {\n" 
                + "  deposit\":" + deposit 
                + ",\n  \"amount\":" + amount 
                + ",\n  \"term\":" + term + "\n}";
    }
}
