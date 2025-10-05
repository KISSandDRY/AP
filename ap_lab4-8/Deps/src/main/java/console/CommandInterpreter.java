package console;

import app.AppInfo;
import console.commands.api.AbstractCommand;
import console.commands.account.*;
import console.commands.deposit.*;
import console.commands.system.*;
import console.util.Utils;
import console.commands.storage.*;
import deposit.service.DepositService;

import java.util.Map;
import java.util.Arrays;
import java.util.Scanner;
import java.util.LinkedHashMap;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * The {@code CommandInterpreter} class provides the main user interaction loop
 * for console-based command execution.
 * <p>
 * It reads user input, identifies and executes commands, supports piped command
 * sequences, and suggests the closest command names for unrecognized input.
 */
public class CommandInterpreter {

    private static final Logger logger = LogManager.getLogger(CommandInterpreter.class);

    /** Registered command name -> command instance mappings. */
    private final Map<String, AbstractCommand> commands = new LinkedHashMap<>();

    /** Shared execution context for all commands. */
    private final CommandContext context;
    
    /** Console input prompt symbol. */
    private final String PROMPT = "> ";

    /**
     * Creates a new command interpreter and initializes available commands.
     *
     * @param service the {@link DepositService} used by commands
     */
    public CommandInterpreter(final DepositService service) {
        this.context = new CommandContext(service, new Scanner(System.in), this);

        initializeCommands();

        logger.log(Level.INFO, "Commands initialized.");
    }

    /**
     * Clears the console screen using ANSI escape codes.
     */
    public void clearScreen() {
        System.out.print("\033[2J\033[H");
    }

    /**
     * Pauses execution and waits for the user to press Enter.
     * Useful for sequential console interactions.
     */
    public void pause() {
        System.out.println("Press Enter to continue...");
        context.getScanner().nextLine();

        clearScreen();
    }

    /**
     * Starts the main input loop.
     * <p>
     * Continuously reads user commands, executes them, and handles command pipelines.
     * Typing {@code "exit"} ends the session.
     */
    public void start() {
        clearScreen();

        System.out.printf("%s (type 'help' for commands)\n", AppInfo.getFullAppName());

        while (true) {
            System.out.print(PROMPT);
            String inputLine = context.getScanner().nextLine().trim();

            if (inputLine.isEmpty()) 
                continue;

            if (inputLine.equalsIgnoreCase("exit")) 
                break;

            String[] pipedCommands = inputLine.split("\\s*\\|\\s*");
            CommandResult finalResult = null;

            // Clear any data from the previous pipeline
            context.setPipelineData(null);

            try {
                for (var commandString : pipedCommands) {
                    String[] parts = commandString.split("\\s+");
                    String commandName = parts[0].toLowerCase();
                    String[] args = Arrays.copyOfRange(parts, 1, parts.length);

                    AbstractCommand cmd = commands.get(commandName);
                    if (cmd == null) {
                        suggestClosestCommand(commandName);

                        throw new Exception("Command not found: " + commandName);
                    }

                    // The same context is passed to every command in the chain.
                    // The command itself will read from/write to the context's pipeline data.
                    finalResult = cmd.execute(context, args);

                    // Stop the pipeline on failure
                    if (!finalResult.isSuccess()) 
                        break; 
                }

                if (finalResult != null && !finalResult.isSuccess()) {
                    System.out.println(finalResult.getMessage().orElse("An unknown error occurred."));
                    logger.log(Level.ERROR, finalResult.getMessage().orElse("An unknown error occurred."));
                }

            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                logger.log(Level.ERROR, "An error occurred: " + e.getMessage());
            }
        }

        System.out.println("Exiting application.");
    }

    /**
     * Registers multiple commands into the interpreter.
     *
     * @param cmds one or more command instances
     */
    private void registerCommand(final AbstractCommand... cmds) {
        for (var cmd : cmds)
            commands.put(cmd.getName().toLowerCase(), cmd);
    }

    /**
     * Suggests the closest known command name if the input is unrecognized.
     *
     * @param input the unrecognized command name
     */
    private void suggestClosestCommand(final String input) {
        String bestMatch = null;
        int minDist = Integer.MAX_VALUE;

        for (var name : commands.keySet()) {
            int dist = Utils.getLevenshteinDistance(input, name);

            if (dist < minDist) {
                minDist = dist;
                bestMatch = name;
            }
        }

        if (minDist <= 3)
            System.out.printf("Unknown command '%s'. Did you mean '%s'?%n", input, bestMatch);
        else 
            System.out.printf("Unknown command '%s'. Type 'help' for a list of commands.%n", input);
        
    }

    /**
     * Registers and initializes all available console commands.
     */
    private void initializeCommands() {
        HelpCommand helpCommand = new HelpCommand(this.commands);

        registerCommand(
            new AddDepositCommand(),
            new RemoveDepositCommand(),
            new ListCommand(),
            new SearchCommand(),
            new SortCommand(),
            new SuggestDepositsCommand(),

            new OpenAccountCommand(),
            new CloseAccountCommand(),
            new ReplenishAccountCommand(),
            new CalcAccountProfitCommand(),

            new PrintCommand(),
            new NBUStatsCommand(),
            new SaveCommand(),
            new LoadCommand(),
            new ClearCommand(),
            new PauseCommand(),
            new VersionCommand(),
            helpCommand
        );
    }
}
