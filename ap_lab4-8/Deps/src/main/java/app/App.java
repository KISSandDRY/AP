package app;

import deposit.repository.FileDepositRepository;
import deposit.repository.api.IDepositRepository;
import deposit.integration.nbu.NBUUpdater;
import deposit.service.DepositService;
import deposit.domain.Deposit;
import deposit.domain.DepositAccount;
import console.CommandInterpreter;
import console.util.ArgParser;
import console.util.ParsedArgs;

import java.util.List;
import java.util.Arrays;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Represents the main application class.
 * Contains startup configuration flags used during app initialization.
 */
public final class App {

    private static final Logger logger = LogManager.getLogger(App.class);

    /**
     * Represents a command-line flag with a short name, long name, and description.
     */
    private final record Flag(String shortName, String longName, String description) {}
    
    /**
     * List of flags available during application startup.
     */
    private final List<Flag> startupFlags;

    // These fields are nullable because initialization can be stopped by a startup flag.
    private CommandInterpreter interpreter;
    private NBUUpdater nbuUpdater;
    private boolean shouldStart = true;

    /**
     * Main app class. Resposible for configuring and starting app.
     *
     * @param args CLI arguments passed.
     */
    public App(final String[] args) {
        this.startupFlags = defineStartupFlags();
        ParsedArgs parsedArgs = ArgParser.parse(args);

        logger.info("Application starting up with arguments(CLI arguments parsed): {}", Arrays.toString(args));
    
        // Initialize Configuration with Correct Priority 
        // 1. Base defaults from a properties file.
        // 2. Overrides from system environment variables.
        // 3. Highest priority overrides from command-line arguments.
        AppConfig.init(
            AppConfigKeys.class,
            new AppConfig.ClasspathConfigSource("app.properties"),
            new AppConfig.EnvConfigSource(),
            new AppConfig.CLIConfigSource(args)
        );
        AppConfig config = AppConfig.getInstance();
        logger.debug("Configuration loaded successfully: {}", config);

        if (parsedArgs.hasFlag("version") || parsedArgs.hasFlag("v")) {
            printVersion(); 
            this.shouldStart = false;
            return;
        }
    
        if (parsedArgs.hasFlag("help") || parsedArgs.hasFlag("h")) {
            printHelp(); 
            this.shouldStart = false;
            return;
        }

        if (parsedArgs.hasFlag("config") || parsedArgs.hasFlag("c")) {
            System.out.println(config);
            this.shouldStart = false;
            return;
        }

        // Initialize Services and Repositories 
        logger.info("Initializing application services...");
        this.nbuUpdater = new NBUUpdater(config.getInt(AppConfigKeys.NBU_UPDATE_INTERVAL));

        String depositsFile = config.getString(AppConfigKeys.DEPOSITS_FILE);
        String accountsFile = config.getString(AppConfigKeys.ACCOUNTS_FILE);

        IDepositRepository<Deposit> depositRepo = new FileDepositRepository<>(depositsFile);
        IDepositRepository<DepositAccount> accountRepo = new FileDepositRepository<>(accountsFile);
        DepositService service = new DepositService(depositRepo, accountRepo);

        // Handle runtime flags
        if (parsedArgs.hasFlag("autoload") || parsedArgs.hasFlag("a")) {
            logger.info("Autoload flag detected. Loading all data from files...");
            service.loadAllData();
        } 

        //TODO: add autosave flag

        this.interpreter = new CommandInterpreter(service);
        logger.info("Application setup complete. Ready to start.");
    }

    /**
     * Starts the application's main processes (NBU updater and command loop).
     * This method will not run if a startup flag like --help was used.
     */
    public void start() {
        if (!shouldStart) {
            logger.debug("Application start aborted due to startup flag.");
            return;
        }
        
        logger.info("Starting NBU updater thread...");
        nbuUpdater.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown hook activated. Terminating NBU updater.");
            nbuUpdater.terminate();
        }));

        logger.info("Starting command interpreter loop.");
        interpreter.start();
        logger.info("Application shutting down.");
    }

    /**
     * Entry point. App starts here.
     *
     * @param args CLI arguments passed to application.
     */
    public static void main(final String[] args) {
        App app = new App(args);
        app.start();
    }

    /**
     * Centralized place to define all startup flags for the application.
     */
    private List<Flag> defineStartupFlags() {
        return List.of(
            new Flag("h", "help", "Shows this help message and exits."),
            new Flag("v", "version", "Shows the application version and exits."),
            new Flag("c", "config", "Shows the application current configuration."),
            new Flag("a", "autoload", "Automatically loads data from default files on startup.")
        );
    }

    /**
     * Prints application version.
     */
    private void printVersion() {
        System.out.println(AppInfo.getFullAppName());
    }

    /**
     * Prints help message.
     */
    private void printHelp() {
        printVersion();
        System.out.println("Usage: java -jar Deps.jar [flags] [options]");

        System.out.println("\nFlags:");
        for (var flag : startupFlags) 
            System.out.printf("  -%-4s --%-18s - %s%n", flag.shortName(), flag.longName(), flag.description());
        
        System.out.println("\nOptions (can be set in app.properties, as ENV_VARS, or as CLI args):");

        System.out.printf("  --%s=<path>%n", AppConfigKeys.DEPOSITS_FILE);
        System.out.printf("  --%s=<path>%n", AppConfigKeys.ACCOUNTS_FILE);
        System.out.printf("  --%s=<ms>%n", AppConfigKeys.NBU_UPDATE_INTERVAL);
    }
}
