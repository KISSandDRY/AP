package console.commands.system;

import deposit.integration.nbu.NBU;
import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.util.ParsedArgs;

/**
 * A command that displays the current key statistics from the National Bank of Ukraine (NBU).
 * <p>
 * This command fetches the current base interest rate and the tax rate on profit
 * from the {@link NBU} singleton and prints them to the console for the user.
 * It provides a quick way to check the dynamic, system-wide financial parameters.
 * </p>
 */
public class NBUStatsCommand extends AbstractCommand {

    /**
     * Default constructor.
     */
    public NBUStatsCommand() { }
    
    @Override
    public String getName() { 
        return "nbustats"; 
    }

    @Override
    public String getDescription() { 
        return "Prints current NBU interest rate and tax."; 
    }

    @Override
    public String getUsage() { 
        return getName(); 
    }

    @Override
    public CommandResult executeLogic(
        final CommandContext context, 
        final ParsedArgs args
    ) {
        var nbu = NBU.getInstance();
        var interestRate = nbu.getInterestRate();
        var tax = nbu.getTax();

        System.out.println("Current NBU Information");
        
        System.out.println("  Base Interest Rate: " + interestRate);
        System.out.println("  Tax on Profit:      " + tax);

        return CommandResult.success();
    }
}
