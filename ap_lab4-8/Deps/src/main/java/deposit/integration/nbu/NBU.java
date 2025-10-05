package deposit.integration.nbu;

import deposit.domain.value.PercentageRate;
import deposit.integration.nbu.api.INBUSubscriber;

import java.util.List;
import java.math.BigDecimal;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Singleton class representing the National Bank's interest rate and tax provider.
 * Maintains current interest rate and tax values, supports subscription for updates.
 * Subscribers implementing {@link INBUSubscriber} are notified on rate or tax changes.
 */
public final class NBU {

    private static volatile NBU instance;

    private final List<INBUSubscriber> subscribers;

    private volatile PercentageRate interestRate;
    private volatile PercentageRate tax;

    private static final PercentageRate DEFAULT_INTEREST_RATE = new PercentageRate(new BigDecimal("0.13"));
    private static final PercentageRate DEFAULT_TAX = new PercentageRate("0.23");

    /**
     * Private constructor initializing default interest rate, tax, and subscriber list.
     */
    private NBU() {
        this.interestRate = DEFAULT_INTEREST_RATE;
        this.tax = DEFAULT_TAX;
        this.subscribers = new CopyOnWriteArrayList<>();
    }

    /**
     * Returns the singleton instance of NBU, creating it if necessary.
     * This method is synchronized for thread-safety.
     *
     * @return the singleton NBU instance
     */
    public static synchronized NBU getInstance() {
        if (instance == null) 
            instance = new NBU();
        
        return instance;
    }

    /**
     * Returns the current National Bank interest rate.
     *
     * @return the interest rate as PercentageRate
     */
    public PercentageRate getInterestRate() {
        return interestRate;
    }

    /**
     * Returns the current tax rate.
     *
     * @return the tax rate as PercentageRate
     */
    public PercentageRate getTax() {
        return tax;
    }

    /**
     * Updates the interest rate and notifies all subscribers of the change.
     *
     * @param rate the new interest rate
     */
    public void setInterestRate(final PercentageRate rate) {
        this.interestRate = rate;

        notifySubscribers();
    }

    /**
     * Updates the tax rate and notifies all subscribers of the change.
     *
     * @param tax the new tax rate
     */
    public void setTax(final PercentageRate tax) {
        this.tax = tax;

        notifySubscribers();
    }
    
    /**
     * Subscribes a new observer to receive updates of changes in interest rate and tax.
     *
     * @param subscriber the subscriber to add
     */
    public void subscribe(final INBUSubscriber subscriber) {
        if (subscriber != null) 
            subscribers.add(subscriber);
    }

    /**
     * Unsubscribes an observer from receiving updates.
     *
     * @param subscriber the subscriber to remove
     */
    public void unsubscribe(final INBUSubscriber subscriber) {
        if (subscriber != null) 
            subscribers.remove(subscriber);
    }

    /**
     * Notifies all registered subscribers of the current interest rate and tax values.
     */
    private void notifySubscribers() {
        for (var subscriber : subscribers) 
            subscriber.update(this.interestRate, this.tax);
    }
}
