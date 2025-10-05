package console.commands.system;

import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.util.Utils;
import console.util.ParsedArgs;
import deposit.domain.Deposit;
import deposit.domain.DepositAccount;

import java.util.Map;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import java.util.Comparator;

/**
 * A command that sorts a list of items received from a preceding command in a pipeline.
 * <p>
 * This command is designed to operate on pipeline data. It identifies the type of data in the pipeline
 * (either {@link Deposit} or {@link DepositAccount}) and sorts the list based on user-specified fields.
 * The sort criteria can be provided as command-line arguments (e.g., {@code sort rate dsc}) or
 * through an interactive prompt if no arguments are given.
 * </p>
 * <p>
 * The sorted list replaces the original list in the command pipeline for subsequent commands.
 * </p>
 * Example: {@code list --accounts | sort term dsc | print}
 */
public class SortCommand extends AbstractCommand {

    private final Map<String, Comparator<Deposit>> depositComparators = new HashMap<>();
    private final Map<String, Comparator<DepositAccount>> accountComparators = new HashMap<>();

    /**
     * Constructor. Intializes sort command with all known sort fields.
     */
    public SortCommand() {
        depositComparators.put("bank", Comparator.comparing(d -> d.getInfo().bankName()));
        depositComparators.put("name", Comparator.comparing(d -> d.getInfo().depositName()));
        depositComparators.put("rate", Comparator.comparing(d -> d.getInterestRate().getRateValue()));
        depositComparators.put("currency", Comparator.comparing(d -> d.getInfo().currency().name()));

        accountComparators.put("amount", Comparator.comparing(acc -> acc.getAmount()));
        accountComparators.put("term", Comparator.comparing(acc -> acc.getTerm()));
    }

    @Override
    public String getName() { 
        return "sort"; 
    }

    @Override
    public String getDescription() { 
        return "Sorts data from a pipeline by specified fields."; 
    }

    @Override
    public String getUsage() {
        return getName() + " <field>... [asc|dsc]\n\n" +
               "This command must be used after a command that provides a list (e.g., 'list --accounts | sort term dsc').";
    }

    @Override
    public CommandResult executeLogic(
        final CommandContext context, 
        final ParsedArgs args
    ) {
        Optional<List> pipedData = context.getPipelineData(List.class);

        if (pipedData.isEmpty() || pipedData.get().isEmpty()) 
            return CommandResult.success();

        List<?> items = pipedData.get();
        Object firstItem = items.get(0);

        // Check the type of the first item to decide which logic to use.
        if (firstItem instanceof Deposit) {

            // This will generate an unchecked cast warning, which is suppressed.
            @SuppressWarnings("unchecked")
            List<Deposit> depositList = (List<Deposit>) items;

            return sortItems(context, depositList, depositComparators, args);
        }

        if (firstItem instanceof DepositAccount) {

            @SuppressWarnings("unchecked")
            List<DepositAccount> accountList = (List<DepositAccount>) items;

            return sortItems(context, accountList, accountComparators, args);
        }

        return CommandResult.failure("Data in pipeline is of an unknown type and cannot be sorted by this command.");
    }

    private <T> CommandResult sortItems(
        final CommandContext context, List<T> items, 
        final Map<String, Comparator<T>> comparators, 
        final ParsedArgs args
    ) {
        Comparator<T> finalComparator;
        List<String> positionalArgs = args.getPositionalArgs();

        if (!positionalArgs.isEmpty()) 
            finalComparator = buildComparatorFromArgs(positionalArgs, comparators);
        else 
            finalComparator = buildComparatorFromUserInput(context.getScanner(), comparators);

        if (finalComparator == null) 
            return CommandResult.failure("No valid sort fields provided.");

        // Sort the list and put the new, sorted list back into the pipeline.
        items.sort(finalComparator);
        context.setPipelineData(items);

        return CommandResult.success();
    }

    private <T> Comparator<T> buildComparatorFromArgs(
        final List<String> args, 
        final Map<String, Comparator<T>> comparators
    ) {
        String direction = args.contains("dsc") ? "dsc" : "asc";
        
        Comparator<T> combined = null;

        for (var field : args) {
            if (field.matches("asc|dsc")) 
                continue;
            
            Comparator<T> c = comparators.get(field.toLowerCase());

            if (c != null) 
                combined = (combined == null) ? c : combined.thenComparing(c);
        }

        if (combined != null && direction.equals("dsc")) 
            return combined.reversed();
        
        return combined;
    }

    private <T> Comparator<T> buildComparatorFromUserInput(
        final java.util.Scanner scanner, 
        final Map<String, Comparator<T>> comparators
    ) {
        System.out.println("Available sort fields: " + String.join(", ", comparators.keySet()));
        String input = Utils.readLine(scanner, "Enter fields to sort by (e.g., 'rate bank') > ").trim();

        return buildComparatorFromArgs(Arrays.asList(input.split("\\s+")), comparators);
    }
}
