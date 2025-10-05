package deposit.integration.nbu;

import deposit.domain.value.PercentageRate;

import java.util.Random;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Background thread that periodically updates the National Bank interest rate and tax.
 * Generates pseudo-random fluctuations on rates within predefined boundaries.
 * Updates the rates in the {@link NBU} singleton and notifies subscribers.
 * <p>
 * The updater runs as a daemon thread and can be terminated gracefully.
 */
public final class NBUUpdater extends Thread {

    private static final Logger logger = LogManager.getLogger(NBUUpdater.class);

    private final NBU nbu = NBU.getInstance();
    private final Random random = new Random();

    private volatile boolean running = true;
    private volatile long updateIntervalMillis;

    /**
     * Constructs an NBUUpdater that updates rates at the given interval.
     *
     * @param updateIntervalMillis the number of milliseconds between consecutive updates
     */
    public NBUUpdater(final long updateIntervalMillis) {
        this.updateIntervalMillis = updateIntervalMillis;

        setDaemon(true);
        setName("NBU-Updater-Thread");
    }

    /**
     * Signals this updater thread to terminate and interrupts it if sleeping.
     */
    public void terminate() {
        running = false;
        this.interrupt();
    }

    /**
     * Main thread loop that sleeps for the configured interval and then updates rates.
     * Catches interruptions and exceptions, terminating gracefully on interrupt.
     */
    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(updateIntervalMillis);

                updateInterestRate();
                updateTax();

            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();

            } catch (Exception e) {
                logger.log(Level.ERROR, e.getMessage());
            }
        }
    }

    /**
     * Updates the National Bank interest rate applying a random delta multiplier
     * constrained between 1% and 30%.
     */
    private void updateInterestRate() {
        BigDecimal oldRate = nbu.getInterestRate().rate();

        // Generate a random delta between 0.9 and 1.1
        BigDecimal rateDelta = BigDecimal.valueOf(0.9 + random.nextDouble() * 0.2)
                                       .setScale(4, RoundingMode.HALF_UP);
                                       
        BigDecimal newRateValue = oldRate.multiply(rateDelta)
                                        .max(new BigDecimal("0.01")) // don't go below 1%
                                        .min(new BigDecimal("0.30")); // or above 30%
                                        
        nbu.setInterestRate(new PercentageRate(newRateValue));
    }

    /**
     * Updates the National Bank tax rate applying a random delta multiplier
     * constrained between 15% and 32%.
     */
    private void updateTax() {
        PercentageRate oldTax = nbu.getTax();
        BigDecimal taxDelta = BigDecimal.valueOf(0.98 + random.nextDouble() * 0.04)
                                      .setScale(4, RoundingMode.HALF_UP);
                                      
        BigDecimal newTax = oldTax.rate().multiply(taxDelta)
                                         .max(new BigDecimal("0.15")) // don't go below 15%
                                         .min(new BigDecimal("0.32")); // or above 32%

        nbu.setTax(new PercentageRate(newTax));
    }
}
