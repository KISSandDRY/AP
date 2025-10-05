package console.commands.system;

import console.CommandResult;
import console.CommandContext;
import console.commands.api.AbstractCommand;
import console.exceptions.CommandCancelledException;
import console.util.Utils;
import console.util.ParsedArgs;
import deposit.domain.Deposit;
import deposit.domain.DepositAccount;
import deposit.domain.value.Money;

import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A powerful and flexible command to search for {@link Deposit} products or {@link DepositAccount}s.
 *
 * This command supports multiple modes of operation:
 * <ul>
 * <li><b>Command-line Mode:</b> Filters are provided directly as arguments (e.g., {@code search rate > 0.05}).</li>
 * <li><b>Interactive Mode:</b> If no arguments are given, the user is prompted to build filters step-by-step.</li>
 * <li><b>Inferred Mode:</b> If no target flag (like {@code --accounts}) is given, the command intelligently infers whether to
 * search deposits or accounts based on the field names provided in the filters.</li>
 * </ul>
 * The results of the search are placed into the command pipeline for further processing by commands like {@code sort} or {@code print}.
 *
 * @see SortCommand
 * @see PrintCommand
 */
public class SearchCommand extends AbstractCommand {

    private final Map<String, Function<Deposit, ? extends Comparable<?>>> depositExtractors = new HashMap<>();
    private final Map<String, Function<DepositAccount, ? extends Comparable<?>>> accountExtractors = new HashMap<>();

    /**
     * Constructor. Intializes search command with all known search fields.
     */
    public SearchCommand() {
        depositExtractors.put("bank", d -> d.getInfo().bankName());
        depositExtractors.put("name", d -> d.getInfo().depositName());
        depositExtractors.put("rate", d -> d.getInterestRate().getRateValue());
        depositExtractors.put("currency", d -> d.getInfo().currency().name());
        depositExtractors.put("replenish", d -> d.getPolicy().canReplenish());
        depositExtractors.put("withdraw", d -> d.getPolicy().canWithdrawEarly());
        
        accountExtractors.put("amount", acc -> acc.getAmount());
        accountExtractors.put("term", acc -> acc.getTerm().months());
    }

    @Override
    public String getName() { 
        return "search"; 
    }

    @Override
    public String getDescription() { 
        return "Dynamically searches for deposits or accounts based on filters."; 
    }

    @Override
    public String getUsage() {
        return getName() + " [ -a | --accounts | -d |--deposits] [filter1] [filter2]...\n\n"
                + "Automatically detects search type based on filter fields.\n" 
                + "Example: search amount > 5000 | print";
    }

    @Override
    public CommandResult executeLogic(
        final CommandContext context, 
        final ParsedArgs args
    ) {
        try {
            if (args.hasFlag("accounts") || args.hasFlag("a"))
                searchAccounts(context, args);

            else if (args.hasFlag("deposits") || args.hasFlag("d")) 
                searchDeposits(context, args);

            else if (!args.getPositionalArgs().isEmpty())

                // Infer search type from the first field name in the arguments.
                inferAndExecuteSearch(context, args);
            else
                // No arguments provided, so enter interactive mode.
                interactiveSearch(context);

            return CommandResult.success();

        } catch (CommandCancelledException e) {
            return CommandResult.failure("Search cancelled.");

        } catch (IllegalArgumentException e) {
            return CommandResult.failure(e.getMessage());
        }
    }

    /**
     * Infers the search target (deposits or accounts) based on the first field
     * provided in the command-line arguments.
     */
    private void inferAndExecuteSearch(CommandContext context, ParsedArgs args) {
        String firstField = args.getPositionalArgs().get(0);
        boolean isAccountField = accountExtractors.containsKey(firstField);
        boolean isDepositField = depositExtractors.containsKey(firstField);

        if (isAccountField && !isDepositField) {
            System.out.println("--> Inferred search target: Accounts (from field '" + firstField + "')");
            searchAccounts(context, args);
        } else {
            // Default to searching deposits if the field is for deposits, ambiguous, or unknown.
            // The predicate builder will throw an error for unknown fields later.
            if (isDepositField && !isAccountField) 
                 System.out.println("--> Inferred search target: Deposits (from field '" + firstField + "')");
            
            searchDeposits(context, args);
        }
    }

    /**
     * Prompts the user to choose what to search for in interactive mode.
     */
    private void interactiveSearch(CommandContext context) {
        System.out.println("What would you like to search for?");
        String choice = Utils.readLine(context.getScanner(), " 1 - Deposits\n 2 - Accounts\nInput:  ");

        ParsedArgs emptyArgs = new ParsedArgs(Collections.emptySet(), Collections.emptyMap(), Collections.emptyList());

        if ("2".equals(choice)) 
            searchAccounts(context, emptyArgs);
        else
            searchDeposits(context, emptyArgs);
    }

    /**
     * Executes the search logic specifically for Deposits.
     * It filters the list from the pipeline, or fetches all deposits if the pipeline is empty.
     */
    @SuppressWarnings("unchecked")
    private void searchDeposits(CommandContext context, ParsedArgs args) {

        // Step 1: Get the source list to filter. Prioritize the pipeline.
        Optional<List<Deposit>> pipelineData = context.getPipelineData(List.class)
            .filter(list -> !list.isEmpty() && list.get(0) instanceof Deposit)
            .map(list -> (List<Deposit>) list);

        List<Deposit> sourceList = pipelineData.orElseGet(() -> context.getService().getAllDeposits());

        // Step 2: Build the predicate from arguments or user input.
        Predicate<Deposit> predicate = buildPredicate(context, args, depositExtractors);

        // Step 3: Apply the filter to the source list.
        List<Deposit> results = sourceList.stream()
            .filter(predicate)
            .collect(Collectors.toList());

        System.out.println("Found " + results.size() + " matching deposits.");

        context.setPipelineData(results);
    }

    /**
     * Executes the search logic specifically for DepositAccounts.
     * It filters the list from the pipeline, or fetches all accounts if the pipeline is empty.
     */
    @SuppressWarnings("unchecked")
    private void searchAccounts(CommandContext context, ParsedArgs args) {
        
        // Step 1: Get the source list to filter. Prioritize the pipeline.
        Optional<List<DepositAccount>> pipelineData = context.getPipelineData(List.class)
            .filter(list -> !list.isEmpty() && list.get(0) instanceof DepositAccount)
            .map(list -> (List<DepositAccount>) list);

        List<DepositAccount> sourceList = pipelineData.orElseGet(() -> context.getService().getAllAccounts());

        // Step 2: Build the predicate.
        Predicate<DepositAccount> predicate = buildPredicate(context, args, accountExtractors);

        // Step 3: Apply the filter to the source list.
        List<DepositAccount> results = sourceList.stream()
            .filter(predicate)
            .collect(Collectors.toList());

        System.out.println("Found " + results.size() + " matching accounts.");

        context.setPipelineData(results);
    }

    /**
     * Generic helper to build a predicate from either args or user input.
     */
    private <T> Predicate<T> buildPredicate(
        CommandContext context,
        ParsedArgs args,
        Map<String, Function<T, ? extends Comparable<?>>> extractors
    ) {
        if (args != null && !args.getPositionalArgs().isEmpty()) 
            return buildPredicateFromArgs(args.getPositionalArgs().toArray(new String[0]), extractors);
        else 
            return buildPredicateFromUserInput(context.getScanner(), extractors);
    }

    private <T> Predicate<T> buildPredicateFromArgs(String[] args, Map<String, Function<T, ? extends Comparable<?>>> extractors) {
        if (args.length % 3 != 0) 
            throw new IllegalArgumentException("Invalid arguments. Each filter must be a 'field op value' triplet.");

        Predicate<T> combined = t -> true;

        for (int i = 0; i < args.length; i += 3) 
            combined = combined.and(createPredicate(args[i], args[i+1], args[i+2], extractors));

        return combined;
    }

    private <T> Predicate<T> buildPredicateFromUserInput(Scanner scanner, Map<String, Function<T, ? extends Comparable<?>>> extractors) {
        System.out.println("\nInteractive Search Filter Setup");
        System.out.println("Available fields: " + String.join(", ", extractors.keySet()));
        System.out.println("Enter filters like 'field operator value' (e.g., 'rate > 0.05'). Type 'done' when finished.");
        
        Predicate<T> combinedPredicate = t -> true;
        while (true) {
            String input = Utils.readLine(scanner, "Add filter > ").trim();
            if (input.equalsIgnoreCase("done")) 
                break;

            if (input.isEmpty()) 
                continue;

            String[] parts = input.split("\\s+", 3);
            if (parts.length < 3) {
                System.out.println("Invalid format. Use: field operator value");
                continue;
            }

            try {
                combinedPredicate = combinedPredicate.and(createPredicate(parts[0], parts[1], parts[2], extractors));
                System.out.println("Filter added.");

            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        return combinedPredicate;
    }

    /**
     * Generic predicate creator that can handle different types and custom value objects.
     */
    private <T> Predicate<T> createPredicate(
        String field,
        String operator,
        String value,
        Map<String, Function<T, ? extends Comparable<?>>> extractors
    ) {
        Function<T, ? extends Comparable<?>> extractor = extractors.get(field.toLowerCase());
        if (extractor == null) 
            throw new IllegalArgumentException("Unknown field: '" + field + "'. Available fields are: " + extractors.keySet());

        return item -> {
            Comparable<?> propertyValue = extractor.apply(item);
            
            if (propertyValue instanceof Money moneyValue) {
                return compareNumbers(moneyValue.amount(), new BigDecimal(value), operator);

            } else if (propertyValue instanceof Number numberValue) { 
                BigDecimal propDecimal = new BigDecimal(numberValue.toString());

                return compareNumbers(propDecimal, new BigDecimal(value), operator);
            }

            if (propertyValue instanceof String strValue) 
                return compareStrings(strValue, value, operator);
            
            if (propertyValue instanceof Boolean boolPropValue) 
                return compareBooleans(boolPropValue, value, operator);

            return false;
        };
    }

    private boolean compareNumbers(BigDecimal prop, BigDecimal val, String op) {
        // For floating-point equality, it's better to compare with a fixed precision.
        // We set the scale to 4 decimal places for comparison.
        if (op.equals("==") || op.equals("=")) {
            BigDecimal scaledProp = prop.setScale(4, RoundingMode.HALF_UP);
            BigDecimal scaledVal = val.setScale(4, RoundingMode.HALF_UP);
            return scaledProp.compareTo(scaledVal) == 0;
        }

        return switch (op) {
            case ">" -> prop.compareTo(val) > 0;
            case ">=" -> prop.compareTo(val) >= 0;
            case "<" -> prop.compareTo(val) < 0;
            case "<=" -> prop.compareTo(val) <= 0;
            default -> throw new IllegalArgumentException("Invalid operator '" + op + "' for a number.");
        };
    }
    
    private boolean compareStrings(String prop, String val, String op) {
        return switch (op) {
            case "==", "=", "equals" -> prop.equalsIgnoreCase(val);
            case "!=" -> !prop.equalsIgnoreCase(val);
            case "contains" -> prop.toLowerCase().contains(val.toLowerCase());
            default -> throw new IllegalArgumentException("Invalid operator '" + op + "' for a string.");
        };
    }
    
    private boolean compareBooleans(boolean prop, String val, String op) {
        boolean boolValue = Boolean.parseBoolean(val);

        return switch (op) {
            case "==", "=" -> prop == boolValue;
            case "!=" -> prop != boolValue;
            default -> throw new IllegalArgumentException("Invalid operator '" + op + "' for a boolean.");
        };
    }
}
