package app;

/**
 * A centralized interface to define all configuration keys used in the application.
 * This prevents typos and makes configuration management easier.
 */
public final class AppConfigKeys {

    /**
     * Private constructor to not be able to make instance of ConfigKeys class.
     */
    private AppConfigKeys() { }

    // File paths
    /** Configuration key for deposits data file path */
    public static final String DEPOSITS_FILE = "db.deposits.file";

    /** Configuration key for accounts data file path */
    public static final String ACCOUNTS_FILE = "db.accounts.file";

    // NBU settings
    /** Configuration key for NBU update interval in milliseconds */
    public static final String NBU_UPDATE_INTERVAL = "nbu.update.interval.ms";
}
