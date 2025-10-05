package deposit.domain;

import deposit.domain.value.Currency;
import deposit.exceptions.ValidationException;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable record representing the descriptive information of a deposit.
 * Contains deposit name, bank name, currency used, and payout frequency in months.
 *
 * @param depositName the name of the deposit
 * @param bankName the name of the bank offering the deposit
 * @param currency the currency in which the deposit is denominated
 * @param payoutFrequency the frequency (in months) at which interest is paid out
 */
public final record DepositInfo(
    String depositName,
    String bankName,
    Currency currency,
    int payoutFrequency
) implements Serializable {

    /**
     * The constructor for the record.
     * It performs validation on the input parameters.
     *
     * @throws NullPointerException if deposit name or bank name or currency is null
     * @throws ValidationException if names are blank or payout frequency is negative.
     */
    public DepositInfo {
        Objects.requireNonNull(depositName, "Deposit name cannot be null.");
        Objects.requireNonNull(bankName, "Bank name cannot be null.");
        Objects.requireNonNull(currency, "Currency cannot be null.");

        if (depositName.isBlank()) 
            throw new ValidationException("Deposit name cannot be blank.");
        
        if (bankName.isBlank()) 
            throw new ValidationException("Bank name cannot be blank.");
        
        if (payoutFrequency <= 0) 
            throw new ValidationException("Payout frequency must be a positive number.");
    }

    
    /**
     * Returns a string representation of this deposit info showing key fields.
     *
     * @return a string representing the deposit info 
     */
    @Override
    public final String toString() {
        return "\"" + this.getClass().getName() + "\": {\n" 
                + "  name\":" + depositName 
                + ",\n  \"bank\":" + bankName 
                + ",\n  \"currency\":" + currency
                + ",\n  \"payfreq\":" + payoutFrequency + "\n}";
    }
}
