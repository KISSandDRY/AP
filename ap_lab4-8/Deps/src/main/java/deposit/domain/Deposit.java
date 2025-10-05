package deposit.domain;

import deposit.domain.api.IStorable;
import deposit.interest.api.IInterestRate;
import deposit.interest.api.IInterestStrategy;

import java.util.UUID;
import java.util.Objects;
import java.io.Serializable;

/**
 * Represents a deposit with associated information, policy, interest strategy, and interest rate.
 * Each Deposit is uniquely identified by a UUID.
 * Implements {@link IStorable} for storage-related identity and {@link Serializable} for object serialization.
 */
public final class Deposit implements IStorable, Serializable {

    /** Unique identifier for the deposit. */
    private final UUID id;

    /** General information about the deposit (e.g. name, description, payout frequency). */
    private final DepositInfo info; 

    /** The policy rules such as term range, allowed replenishment, or withdrawal conditions. */
    private final DepositPolicy policy;

    /** The interest calculation strategy (simple, compound, etc.). */
    private final IInterestStrategy interestStrategy;

    /** The interest rate associated with this deposit. */
    private final IInterestRate interestRate;

    /**
     * Creates a new Deposit instance with the specified parameters.
     *
     * @param info the deposit information
     * @param policy the deposit policy
     * @param interestStrategy the interest calculation strategy used for this deposit
     * @param interestRate the interest rate applied to this deposit
     * @return a new Deposit instance
     */
    static Deposit create(
        final DepositInfo info, 
        final DepositPolicy policy,
        final IInterestStrategy interestStrategy,
        final IInterestRate interestRate
    ) {
        return new Deposit(info, policy, interestStrategy, interestRate);
    }

    /**
     * Private constructor for Deposit used by the static factory method.
     * Generates a unique UUID for the deposit instance.
     *
     * @param info the deposit information, not null
     * @param policy the deposit policy, not null
     * @param interestStrategy the interest calculation strategy, not null
     * @param interestRate the interest rate, not null
     */
    private Deposit(
        final DepositInfo info, 
        final DepositPolicy policy, 
        final IInterestStrategy interestStrategy,
        final IInterestRate interestRate
    ) {
        this.id = UUID.randomUUID();
        this.info = Objects.requireNonNull(info, "DepositInfo must not be null");
        this.policy = Objects.requireNonNull(policy, "DepositPolicy must not be null");
        this.interestStrategy = Objects.requireNonNull(interestStrategy, "Interest strategy must not be null");
        this.interestRate = Objects.requireNonNull(interestRate, "Interest rate must not be null");
    }

    /**
     * Returns the unique identifier of this deposit.
     *
     * @return the UUID of the deposit
     */
    @Override
    public UUID getId() { 
        return id;
    }

    /**
     * Returns the deposit information details.
     *
     * @return the DepositInfo object
     */
    public DepositInfo getInfo() { 
        return info; 
    }

    /**
     * Returns the deposit policy.
     *
     * @return the DepositPolicy object
     */
    public DepositPolicy getPolicy() { 
        return policy; 
    }

    /**
     * Returns the interest calculation strategy used for this deposit.
     *
     * @return the interest strategy
     */
    public IInterestStrategy getInterestStrategy() { 
        return interestStrategy; 
    }

    /**
     * Returns the interest rate applied to this deposit.
     *
     * @return the interest rate
     */
    public IInterestRate getInterestRate() {
        return interestRate;
    }

    /**
     * Compares this deposit to another object for equality based on UUID.
     *
     * @param o the other object to compare
     * @return true if this and the other object are the same deposit
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Deposit)) return false;

        Deposit deposit = (Deposit) o;
        return Objects.equals(id, deposit.id);
    }

    /**
     * Returns the hash code for this deposit, based on its UUID.
     *
     * @return the hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /**
     * Returns a string representation of this deposit with detailed fields.
     *
     * @return the string representation in JSON-like format
     */
    @Override
    public String toString() {
        return "\"" + this.getClass().getName() + "\": {\n" 
                + "  \"info\": " + info 
                + ",\n  \"policy\": " + policy 
                + ",\n  \"strategy\": " + interestStrategy 
                + ",\n  \"rate\"=" + interestRate + "\n}";
    }
}
