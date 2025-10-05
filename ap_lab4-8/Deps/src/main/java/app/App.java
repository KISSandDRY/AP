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

import org.apache.logging.log4j.Level;
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
    
    /**
     * Main app class. Resposible for configuring and starting app.
     *
     * @param args CLI arguments passed.
     */
    public App(final String[] args) {
        this.startupFlags = defineStartupFlags();
        ParsedArgs parsedArgs = ArgParser.parse(args);

        logger.log(Level.INFO, "CLI arguments parsed.");
    
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

        logger.log(Level.INFO, "Configuration sources loaded.");

        // Handle immediate-exit flags BEFORE initializing the full application
        if (parsedArgs.hasFlag("version") || parsedArgs.hasFlag("v")) {
            printVersion(); 
            return;
        }
    
        if (parsedArgs.hasFlag("help") || parsedArgs.hasFlag("h")) {
            printHelp(); 
            return;
        }

        if (parsedArgs.hasFlag("config") || parsedArgs.hasFlag("c")) {
            System.out.println(config);
            return;
        }

        // Setting up services and resources
        NBUUpdater nbuUpdater = new NBUUpdater(config.getInt(AppConfigKeys.NBU_UPDATE_INTERVAL));

        String depositsFile = config.getString(AppConfigKeys.DEPOSITS_FILE);
        String accountsFile = config.getString(AppConfigKeys.ACCOUNTS_FILE);

        IDepositRepository<Deposit> depositRepo = new FileDepositRepository<>(depositsFile);
        IDepositRepository<DepositAccount> accountRepo = new FileDepositRepository<>(accountsFile);
        DepositService service = new DepositService(depositRepo, accountRepo);

        logger.log(Level.INFO, "App is setted up.");

        // Handle runtime flags
        if (parsedArgs.hasFlag("autoload") || parsedArgs.hasFlag("a")) 
            service.loadAllData();

        //TODO: add autosave flag

        // Start the Application
        nbuUpdater.start();
        Runtime.getRuntime().addShutdownHook(new Thread(nbuUpdater::terminate));
        new CommandInterpreter(service).start();
    }

    /**
     * Entry point. App starts here.
     *
     * @param args CLI arguments passed to application.
     */
    public static void main(final String[] args) {
        new App(args);
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
