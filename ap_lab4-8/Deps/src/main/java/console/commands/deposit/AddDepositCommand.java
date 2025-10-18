package console.commands.deposit;

import console.CommandResult;
import console.CommandContext;
import console.exceptions.CommandCancelledException;
import console.commands.api.AbstractCommand;
import console.util.Utils;
import console.util.ParsedArgs;
import deposit.domain.Deposit;
import deposit.domain.DepositBuilder;
import deposit.domain.value.Currency;
import deposit.domain.api.IDepositBuilder;
import deposit.interest.InterestRateType;
import deposit.interest.InterestStrategyType;

import java.util.Scanner;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * A command to add a new deposit.
 *
 * This command supports two modes of operation:
 * <ul>
 * <li><b>Interactive Mode:</b> If run without arguments, it guides the user through a series of prompts to configure the new deposit.</li>
 * <li><b>Command-line Mode:</b> If run with arguments (e.g., {@code --name "My Deposit" --rate "0.05,FIXED"}), it creates the deposit directly.</li>
 * </ul>
 */
public class AddDepositCommand extends AbstractCommand {

    private static final Logger logger = LogManager.getLogger(AddDepositCommand.class);

    /**
     * Default constructor.
     */
    public AddDepositCommand() { }

    @Override
    public String getName() { 
        return "add"; 
    }

    @Override
    public String getDescription() {
        return "Adds a new deposit product, either interactively or via arguments.";
    }

    @Override
    public String getUsage() {
        return "Interactive Mode: " + getName() + "\n\n" +
               "Command-line Mode: " + getName() + " { -n | --name <name> } { -b | --bank <bank> } { -r | --rate  <val,type> } { -s | --strategy <type> }\n" +
               "  Optional args: [-c | --currency <currency>] [-mia | --min-amount <val>]\n" +
               "                 [-maa | --max-amount <val>] [-mit | --min-term <months>] [-mat | --max-term <months>]\n" +
               "                 [-l] [-w]";
    }

    @Override
    public CommandResult executeLogic(
        final CommandContext context, 
        final ParsedArgs args
    ) {
        logger.debug("Executing AddDepositCommand.");

        try {
            IDepositBuilder builder = new DepositBuilder();

            // Decide which mode to run based on the presence of arguments.
            // We check for "--name/-n" as a key indicator of command-line mode.
            if (args.getOption("name", "n").isPresent()) {
                logger.info("Building deposit from command-line arguments.");
                buildFromArgs(builder, args);
            } else {
                logger.info("No arguments found. Starting interactive deposit creation.");
                buildFromInteractivePrompts(builder, context.getScanner());
            } 

            // Build and add the deposit regardless of the mode used.
            Deposit newDeposit = builder.build();
            context.getService().addDeposit(newDeposit);

            System.out.println("Deposit '" + newDeposit.getInfo().depositName() + "' added successfully!");
            logger.info("Successfully created and added new deposit with ID: {}", newDeposit.getId());

            return CommandResult.success(newDeposit);

        } catch (CommandCancelledException e) {
            logger.info("User cancelled the deposit creation process.");
            return CommandResult.failure("Deposit creation was cancelled.");

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid arguments provided for deposit creation: {}", e.getMessage());
            System.out.println("Error: " + e.getMessage());
            printHelp();             

            return CommandResult.failure(e.getMessage());

        } catch (Exception e) {
            logger.error("An unexpected error occurred during deposit creation.", e);
            return CommandResult.failure("An unexpected error occurred: " + e.getMessage());
        }
    }

    /**
     * Populates the DepositBuilder using arguments provided on the command line.
     *
     * @param builder The builder to populate.
     * @param args    The parsed command-line arguments.
     * @throws IllegalArgumentException if required arguments are missing or invalid.
     */
    private void buildFromArgs(IDepositBuilder builder, ParsedArgs args) {
        logger.debug("Parsing arguments for new deposit...");

        // Required arguments 
        String name = args.getOption("name", "n")
            .orElseThrow(() -> new IllegalArgumentException("Missing required argument: --name or -n"));

        String bank = args.getOption("bank", "b")
            .orElseThrow(() -> new IllegalArgumentException("Missing required argument: --bank or -b"));

        String rateArg = args.getOption("rate", "r")
            .orElseThrow(() -> new IllegalArgumentException("Missing required argument: --rate or -r (e.g., 0.05,FIXED)"));

        String strategyArg = args.getOption("strategy", "s")
            .orElseThrow(() -> new IllegalArgumentException("Missing required argument: --strategy or -s (e.g., SIMPLE)"));

        logger.debug("Required args parsed: name='{}', bank='{}', rate='{}', strategy='{}'", name, bank, rateArg, strategyArg);

        // Optional arguments 
        String currencyCode = args.getOption("currency", "c").orElse("UAH").toUpperCase();
        Currency currency = Currency.fromCode(currencyCode);

        // Parse complex arguments 
        String[] rateParts = rateArg.split(",");
        if (rateParts.length != 2) 
            throw new IllegalArgumentException("Invalid format for --rate/-r. Expected 'value,type' (e.g., 0.05,FIXED)");
        
        double rateValue = Double.parseDouble(rateParts[0]);
        InterestRateType rateType = InterestRateType.fromCode(rateParts[1].toUpperCase());
        InterestStrategyType strategyType = InterestStrategyType.fromCode(strategyArg.toUpperCase());

        builder.setDepositName(name)
            .setBankName(bank)
            .setCurrency(currency)
            .setInterestRate(rateValue, rateType)
            .setInterestStrategy(strategyType);

        args.getOption("min-amount", "mia").map(Double::parseDouble).ifPresent(builder::setMinAmount);
        args.getOption("max-amount", "maa").map(Double::parseDouble).ifPresent(builder::setMaxAmount);
        args.getOption("min-term", "mit").map(Integer::parseInt).ifPresent(builder::setMinMonths);
        args.getOption("max-term", "mat").map(Integer::parseInt).ifPresent(builder::setMaxMonths);

        if (args.hasFlag("l")) 
            builder.setCanReplenish(true);
        
        if (args.hasFlag("w")) 
            builder.setCanWithdrawEarly(true);
    }

    /**
     * Populates the DepositBuilder by guiding the user through a series of interactive prompts.
     *
     * @param builder The builder to populate.
     * @param scanner The scanner for reading user input.
     */
    private void buildFromInteractivePrompts(IDepositBuilder builder, Scanner scanner) {
        System.out.println("Press Enter to skip optional fields, or type 'cancel' to abort.");

        readDepositInfo(scanner, builder);
        readDepositPolicy(scanner, builder);
        readInterestRate(scanner, builder);
        readInterestStrategy(scanner, builder);
    }

    private void readDepositInfo(final Scanner scanner, final IDepositBuilder builder) {
        String depositName = Utils.readNonEmptyString(scanner, "Enter deposit name: ");
        String bankName = Utils.readNonEmptyString(scanner, "Enter bank name: ");
        int payoutFrequency = Utils.readInt(scanner, "Enter payout frequency (e.g., 12, 4, 1): ");
        Currency currency = Utils.readCurrency(scanner);

        builder.setDepositName(depositName)
                .setBankName(bankName)
                .setCurrency(currency)
                .setPayoutFrequency(payoutFrequency);
    }

    private void readDepositPolicy(final Scanner scanner, final IDepositBuilder builder) {
        double minAmount = Utils.readDouble(scanner, "Enter minimal amount: ");
        double maxAmount = Utils.readDouble(scanner, "Enter maximum amount (0 = no limit): ");

        int minMonths = Utils.readInt(scanner, "Enter minimal months: ");
        int maxMonths = Utils.readInt(scanner, "Enter maximum months (0 = no limit): ");

        boolean canWithdrawEarly = Utils.readBoolean(scanner, "Can withdraw early? (y/N): ");
        boolean canReplanish = Utils.readBoolean(scanner, "Can replanish? (y/N): ");

        builder.setMinAmount(minAmount).setMaxAmount(maxAmount)
                .setMinMonths(minMonths).setMaxMonths(maxMonths)
                .setCanWithdrawEarly(canWithdrawEarly).setCanReplenish(canReplanish); 
    }


    private void readInterestRate(final Scanner scanner, final IDepositBuilder builder) {
        InterestRateType rateType = null;
        double rateValue = 0.0;

        final String typePrompt = "Choose interest rate type:\n" +
                                InterestRateType.menu("  ") + "Input: ";

        while (true) {
            String input = Utils.readLine(scanner, typePrompt).trim().toUpperCase();

            try {
                rateType = InterestRateType.fromCode(input);
                break; 

            } catch (IllegalArgumentException e) {
                System.out.println("Invalid type. Please enter one of the codes listed above.");
            }
        }

        while (true) {
            String input = Utils.readLine(scanner, "Enter interest rate value (e.g. 0.05 for 5%): ").trim();

            try {
                rateValue = Double.parseDouble(input);
                if (rateValue <= 0 || rateValue >= 1) {
                    System.out.println("Value must be between 0 and 1 (e.g., 0.05 for 5%).");
                    continue;
                }
                break;

            } catch (NumberFormatException e) {
                System.out.println("Invalid numeric format. Please try again.");
            }
        }

        builder.setInterestRate(rateValue, rateType);
    }

    private void readInterestStrategy(final Scanner scanner, final IDepositBuilder builder) {
        InterestStrategyType strategyType = null;

        final String typePrompt = "Choose interest calculation strategy:\n" +
                                InterestStrategyType.menu("  ") + "Input: ";

        while (true) {
            String input = Utils.readLine(scanner, typePrompt).trim().toUpperCase();

            try {
                strategyType = InterestStrategyType.fromCode(input);
                break;

            } catch (IllegalArgumentException e) {
                System.out.println("Invalid type. Please enter one of the codes listed above.");
            }
        }

        builder.setInterestStrategy(strategyType);
    }
}
