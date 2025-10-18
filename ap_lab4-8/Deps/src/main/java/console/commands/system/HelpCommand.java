package console.commands.system;

import app.AppInfo;
import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.util.ParsedArgs;

import java.util.Map;
import java.util.List;
import java.util.Optional;

/**
 * A command that provides help information to the user.
 * <p>
 * When executed without arguments, it displays a summary of all available commands.
 * When executed with a command name as an argument (e.g., {@code help sort}),
 * it displays detailed usage information for that specific command.
 * </p>
 */
public class HelpCommand extends AbstractCommand {

    private Map<String, AbstractCommand> commands;

    /**
     * Constructor. Initializes with known commands.
     *
     * @param commands {@code Map<String, AbstractCommand>} {@link AbstractCommand}
     */
    public HelpCommand(final Map<String, AbstractCommand> commands) {
        this.commands = commands;
    }

    @Override
    public String getName() { 
        return "help"; 
    }

    @Override
    public String getDescription() { 
        return "Show all commands"; 
    }

    @Override
    public String getUsage() {
        return getName() + " [command_name]";
    }

    @Override
    public CommandResult executeLogic(
        final CommandContext context, 
        final ParsedArgs args
    ) {
        List<String> positionalArgs = args.getPositionalArgs();

        // If no arguments, print the summary of all commands
        if (positionalArgs.isEmpty()) {
            printAllCommands();
            
            return CommandResult.success();
        }
    
        // If a command name is provided as an argument (e.g., "help sort")
        String commandName = positionalArgs.get(0).toLowerCase();
        Optional<AbstractCommand> commandOpt = Optional.ofNullable(commands.get(commandName));

        if (commandOpt.isPresent())
            commandOpt.get().printHelp();
        else
            System.out.println("Error: Command '" + commandName + "' not found.");

        return CommandResult.success();
    }

    /**
     * Prints a summary of all available commands.
     */
    private void printAllCommands() {
        System.out.println(AppInfo.getFullAppName());
        System.out.println("Available commands (to cancel command type cancel):");

        int maxLength = commands.keySet().stream()
                .mapToInt(String::length)
                .max().orElse(10);

        String format = "  %-" + (maxLength + 2) + "s - %s%n";

        commands.values().stream()
                // .sorted(Comparator.comparing(AbstractCommand::getName))
                .forEach(cmd -> System.out.printf(format, cmd.getName(), cmd.getDescription()));
        
        System.out.println("\nFor detailed help on a specific command, type 'help <command_name>'.");
        System.out.println("Tip: Commands can be chained with '|', e.g., 'list --deposits | sort rate dsc | print'");
    }
}
