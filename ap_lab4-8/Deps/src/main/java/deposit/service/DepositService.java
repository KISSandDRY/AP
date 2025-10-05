package deposit.service;

import deposit.domain.Deposit;
import deposit.domain.DepositAccount;
import deposit.domain.value.Money;
import deposit.domain.value.TermPeriod;
import deposit.exceptions.DepositNotFoundException;
import deposit.repository.api.IDepositRepository;
import deposit.repository.api.IFileBasedRepository;
import deposit.interest.api.IInterestStrategy;
import deposit.interest.strategies.TaxedInterest;

import java.util.List;
import java.util.UUID;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.function.Predicate;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Provides core business logic for managing deposits and deposit accounts.
 * <p>
 * The service interacts with repositories to perform CRUD operations,
 * calculates profit, manages persistence, and subscribes to the National Bank updates.
 */
public final class DepositService {

    private static final Logger logger = LogManager.getLogger(DepositService.class);
    
    private final IDepositRepository<Deposit> depositRepository;
    private final IDepositRepository<DepositAccount> accountRepository;

    /**
     * Creates a new {@code DepositService} and subscribes to NBU updates.
     *
     * @param depositRepository Repository for deposits.
     * @param accountRepository Repository for customer deposit accounts.
     */
    public DepositService(
        final IDepositRepository<Deposit> depositRepository, 
        final IDepositRepository<DepositAccount> accountRepository
    ) {
        this.depositRepository = depositRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * Adds a new deposit to the repository.
     *
     * @param deposit The deposit to add.
     */
    public void addDeposit(final Deposit deposit) {
        depositRepository.add(deposit);

        logger.log(Level.INFO, "Deposit added.");
    }

    /**
     * Removes a deposit by its ID.
     *
     * @param depositId The deposit ID.
     * @return {@code true} if removed successfully, otherwise {@code false}.
     */
    public boolean removeDeposit(final UUID depositId) {
        logger.log(Level.INFO, "Deposit removal started.");

        return depositRepository.remove(depositId);
    }

    /**
     * Searches for deposits that match the given filter.
     *
     * @param filter The search predicate.
     * @return List of matching deposits.
     */
    public List<Deposit> searchDeposit(final Predicate<Deposit> filter) {
        logger.log(Level.INFO, "Deposit search started.");

        return depositRepository.findAll(filter, null);
    }

    /**
     * Searches for deposit accounts that match the given filter.
     *
     * @param filter The search predicate.
     * @return List of matching accounts.
     */
    public List<DepositAccount> searchAccount(final Predicate<DepositAccount> filter) {
        logger.log(Level.INFO, "Account search started.");

        return accountRepository.findAll(filter, null);
    }

    /**
     * Returns all deposits.
     *
     * @return List of all deposits.
     */
    public List<Deposit> getAllDeposits() {
        logger.log(Level.INFO, "All Deposits returned.");

        return depositRepository.findAll(null, null);
    }
    
    /**
     * Returns a list of deposit suggestions suitable for the user's amount and term.
     *
     * @param amount User deposit amount.
     * @param term   Desired term period.
     * @return Ranked list of suggested deposits.
     */
    public List<Deposit> getSuggestions(final Money amount, final TermPeriod term) {
        logger.log(Level.INFO, "Suggestions returned.");

        return getAllDeposits().stream()
            .filter(deposit -> deposit.getInfo().currency().equals(amount.currency()))
            .filter(d -> d.getPolicy().amountRange().contains(amount))
            .filter(d -> d.getPolicy().termRange().contains(term))
            .sorted(Comparator.comparing((Deposit d) -> calculateScore(d, amount)).reversed())
            .collect(Collectors.toList());
    }

    /**
     * Calculates a relative score for a deposit to rank suggestions.
     *
     * @param deposit     The deposit.
     * @param userAmount  The user's amount.
     * @return Score value (higher is better).
     */
    private double calculateScore(final Deposit deposit, final Money userAmount) {
        double score = 0;
        score += deposit.getInterestRate().getRateValue().doubleValue() * 50;
        score += deposit.getInfo().payoutFrequency() * 2;
        score += Math.log(userAmount.amount().doubleValue() + 1);

        if (deposit.getPolicy().canReplenish()) 
            score += 5;

        if (deposit.getPolicy().canWithdrawEarly()) 
            score += 2;

        return score;
    }

    /**
     * Opens a new deposit account for a specific deposit.
     *
     * @param depositId      The deposit ID.
     * @param initialAmount  The initial deposit amount.
     * @param term           The deposit term.
     * @return The newly created deposit account.
     * @throws DepositNotFoundException if no deposit with the given ID exists.
     */
    public DepositAccount openAccount(
        final UUID depositId, 
        final Money initialAmount, 
        final TermPeriod term
    ) throws DepositNotFoundException {
        Deposit deposit = depositRepository.findById(depositId)
                .orElseThrow(() -> new DepositNotFoundException(depositId));

        DepositAccount newAccount = new DepositAccount(deposit, initialAmount, term);
        accountRepository.add(newAccount);

        logger.log(Level.INFO, "Account opened.");

        return newAccount;
    }

    /**
     * Removes an existing deposit account.
     *
     * @param accountId The account ID.
     * @return {@code true} if the account was removed.
     * @throws DepositNotFoundException if no account with the given ID exists.
     */
    public boolean closeAccount(final UUID accountId) throws DepositNotFoundException {
        logger.log(Level.INFO, "Account removal started.");

        return accountRepository.remove(accountId); 
    }

    /**
     * Replenishes an existing deposit account with a given amount.
     *
     * @param accountId The account ID.
     * @param amount    The amount to add.
     * @throws DepositNotFoundException if the account is not found.
     */
    public void replenishAccount(
        final UUID accountId, 
        final Money amount
    ) throws DepositNotFoundException {
        DepositAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new DepositNotFoundException(accountId));

        account.replenish(amount);

        logger.log(Level.INFO, "Account replenished.");
    }

   /**
     * Returns all customer deposit accounts.
     *
     * @return List of all accounts.
     */
    public List<DepositAccount> getAllAccounts() {
        logger.log(Level.INFO, "Accounts returned.");

        return accountRepository.findAll(null, null);
    }

    /**
     * Calculates the net profit after applying tax by decorating the account's
     * base interest strategy with a TaxedInterest strategy.
     *
     * @param account The account for which to calculate profit.
     * @return Net profit amount.
     */
    public Money calculateNetProfit(final DepositAccount account) {
        // 1. Get the account's original (gross) interest strategy.
        IInterestStrategy grossStrategy = account.getDeposit().getInterestStrategy();

        // 2. Decorate it on-the-fly with the TaxedInterest strategy.
        IInterestStrategy taxedStrategy = new TaxedInterest(grossStrategy);

        // 3. Use the decorator to calculate the profit. It will automatically apply the tax.
        return taxedStrategy.calculateProfit(
            account.getAmount(),
            account.getTerm(),
            account.getDeposit().getInterestRate()
        );
    }
    
    /**
     * Calculates the net total after applying tax by decorating the account's
     * base interest strategy with a TaxedInterest strategy.
     *
     * @param account The account for which to calculate total.
     * @return Net total amount.
     */
    public Money calculateNet(final DepositAccount account) {
        // 1. Get the account's original (gross) interest strategy.
        IInterestStrategy grossStrategy = account.getDeposit().getInterestStrategy();

        // 2. Decorate it on-the-fly with the TaxedInterest strategy.
        IInterestStrategy taxedStrategy = new TaxedInterest(grossStrategy);

        // 3. Use the decorator to calculate the total profit. It will automatically apply the tax.
        return taxedStrategy.calculate(
            account.getAmount(),
            account.getTerm(),
            account.getDeposit().getInterestRate()
        );
    }

    /**
     * Calculates the gross profit before taxes by directly using the account's profit calculation method.
     *
     * @param account The account for which to calculate profit.
     * @return Gross profit amount.
     */
    public Money calculateGrossProfit(final DepositAccount account) {
        return account.calculateProfit();
    }

    /**
     * Calculates the gross total before taxes by directly using the account's calculation method.
     *
     * @param account The account for which to calculate total.
     * @return Gross total amount.
     */
    public Money calculateGross(final DepositAccount account) {
        return account.calculateProfit();
    }
    
    /**
     * Saves all deposit accounts using the default file name.
     *
     * @return {@code true} if saved successfully.
     */
    public boolean saveAccounts() {
        try {
            return accountRepository.save();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Saves all deposit accounts to a specific file.
     *
     * @param accountsFile The target filename.
     * @return {@code true} if the operation succeeded and the repository supports file I/O.
     */
    public boolean saveAccounts(final String accountsFile) {
        if (depositRepository instanceof IFileBasedRepository fileRepo) {
            try {
                return fileRepo.save(accountsFile);
            } catch (Exception e) {
                return false;
            }
        }

        return false; 
    }

    /**
     * Saves all deposits using the default file name.
     *
     * @return {@code true} if saved successfully.
     */
    public boolean saveDeposits() {
        try {
            return depositRepository.save();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Saves all deposits to a specific file.
     *
     * @param depositsFile The target filename.
     * @return {@code true} if the operation succeeded and the repository supports file I/O.
     */
    public boolean saveDeposits(final String depositsFile) {
        if (depositRepository instanceof IFileBasedRepository fileRepo) {
            try {
                return fileRepo.save(depositsFile);
            } catch (Exception e) {
                return false;
            }
        }

        return false; 
    }

    /**
     * Saves both deposit and account repositories.
     *
     * @return {@code true} if both were saved successfully.
     */
    public boolean saveAllData() {
        return saveDeposits() && saveAccounts();
    }

    /**
     * Saves all data to specified files.
     *
     * @param depositsFile The deposits file.
     * @param accountsFile The accounts file.
     * @return {@code true} if both operations succeeded.
     */
    public boolean saveAllData(final String depositsFile, final String accountsFile) {
        return saveDeposits(depositsFile) && saveAccounts(accountsFile);
    }

    /**
     * Loads all deposit accounts using the default file.
     *
     * @return {@code true} if loaded successfully.
     */
    public boolean loadAccounts() {
        try {
            return accountRepository.load();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Loads deposit accounts from a specific file.
     *
     * @param accountsFile The filename to load.
     * @return {@code true} if successful and supported.
     */
    public boolean loadAccounts(final String accountsFile) {
        if (depositRepository instanceof IFileBasedRepository fileRepo) {
            try {
                return fileRepo.load(accountsFile);
            } catch (Exception e) {
                return false;
            }
        }

        return false; 
    }

    /**
     * Loads all deposits using the default file.
     *
     * @return {@code true} if loaded successfully.
     */
    public boolean loadDeposits() {
        try {
            return depositRepository.load();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Loads deposits from a specific file.
     *
     * @param depositsFile The filename to load.
     * @return {@code true} if successful and supported.
     */
    public boolean loadDeposits(final String depositsFile) {
        if (depositRepository instanceof IFileBasedRepository fileRepo) {
            try {
                return fileRepo.load(depositsFile);
            } catch (Exception e) {
                return false;
            }
        }

        return false; 
    }

    /**
     * Loads all deposits and accounts using default files.
     *
     * @return {@code true} if both loaded successfully.
     */
    public boolean loadAllData() {
        return loadDeposits() && loadAccounts();
    }

    /**
     * Loads deposits and accounts from specific files.
     *
     * @param depositsFile The deposits filename.
     * @param accountsFile The accounts filename.
     * @return {@code true} if both operations succeeded.
     */
    public boolean loadAllData(final String depositsFile, final String accountsFile) {
        return loadDeposits(depositsFile) && loadAccounts(accountsFile);
    }
}
