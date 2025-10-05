package console.commands.system;

import deposit.domain.Deposit;
import deposit.domain.DepositAccount;
import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.util.ParsedArgs;
import console.util.printer.ListAccountsPrinter;
import console.util.printer.ListDepositsPrinter;

import java.util.List;
import java.util.Optional;

/**
 * A command that prints data received from a preceding command in a pipeline.
 * <p>
 * This command is designed to be a terminal operation in a command chain. It retrieves a list
 * of objects from the {@link CommandContext}'s pipeline, determines its type, and prints it to the
 * console in a formatted way. It uses specialized printers for known types like {@link Deposit}
 * and {@link DepositAccount}, and a generic print method for unknown types.
 * </p>
 * <p>
 * Example: {@code list --deposits | sort rate | print}
 * </p>
 */
public class PrintCommand extends AbstractCommand {

    /**
     * Default constructor.
     */
    public PrintCommand() { }

    @Override
    public String getName() { 
        return "print"; 
    }

    @Override
    public String getDescription() { 
        return "Prints the data from the previous command in a pipeline."; 
    }

    @Override
    public String getUsage() { 
        return "| " + getName(); 
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult executeLogic(
        final CommandContext context, 
        final ParsedArgs args
    ) {
        Optional<List> pipedData = context.getPipelineData(List.class);

        if (pipedData.isEmpty() || pipedData.get().isEmpty()) 
            return CommandResult.failure("No data in the pipeline to print.");

        List<?> data = pipedData.get();

        if (data.get(0) instanceof Deposit) 
            new ListDepositsPrinter().print((List<Deposit>) data);
        else if (data.get(0) instanceof DepositAccount)
            new ListAccountsPrinter().print((List<DepositAccount>) data);
        else
            data.forEach(System.out::println);

        return CommandResult.success();
    }
}
