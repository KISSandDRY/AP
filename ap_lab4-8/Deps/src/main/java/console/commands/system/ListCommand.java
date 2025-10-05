package console.commands.system;

import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.util.ParsedArgs;

/**
 * A command that fetches all deposits or all accounts and places them into the command pipeline.
 * <p>
 * This command is designed to be the starting point for a command chain. It requires the user
 * to specify exactly one of two flags: {@code --deposits} (or {@code -d}) to fetch all deposit products,
 * or {@code --accounts} (or {@code -a}) to fetch all user accounts. The resulting list is then
 * passed to the next command in the pipeline (e.g., {@code sort}, {@code search}, or {@code print}).
 * </p>
 * <p>
 * Example: {@code list --accounts | sort amount desc | print}
 * </p>
 */
public class ListCommand extends AbstractCommand {

    /**
     * Default constructor.
     */
    public ListCommand() { }

    @Override
    public String getName() {
        return "list";
    }

    @Override
    public String getDescription() {
        return "Lists deposit and/or accounts.";
    }

    @Override
    public String getUsage() {
        return getName() + " { -d | --deposits } | { -a | --accounts }\n" 
               + "Example: list --accounts | sort amount desc | print";
    }

    @Override
    public CommandResult executeLogic(
        final CommandContext context,
        final ParsedArgs args
    ) {
        boolean wantsDeposits = args.hasFlag("d") || args.hasFlag("deposits");
        boolean wantsAccounts = args.hasFlag("a") || args.hasFlag("accounts");

        // This condition is true if either both flags are present or no flags are present.
        if (wantsDeposits == wantsAccounts) 
            return CommandResult.failure(
                "Error you must specify exactly one flag: --deposits or --accounts."
            );

        if (wantsDeposits)
            context.setPipelineData(context.getService().getAllDeposits());
        else 
            context.setPipelineData(context.getService().getAllAccounts());
        
        return CommandResult.success();
    }
}
