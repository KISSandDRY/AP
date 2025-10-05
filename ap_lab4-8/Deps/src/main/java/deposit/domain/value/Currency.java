package deposit.domain.value;

import deposit.exceptions.ValidationException;

/**
 * Enumeration representing supported currencies with associated codes, descriptions, and symbols.
 */
public enum Currency {

    /**
     * Ukrainian Hryvnia currency.
     */
    UAH("UAH", "Ukrainian Hryvnia", "₴"),

    /**
     * US Dollar currency.
     */
    USD("USD", "US Dollar", "$"),

    /**
     * Euro currency.
     */
    EUR("EUR", "Euro", "€");

    private final String code;
    private final String description;
    private final String symbol;

    /**
     * Constructs a Currency enum constant with its code, description, and symbol.
     *
     * @param code the currency code (e.g., "USD")
     * @param description human-readable currency name
     * @param symbol the currency symbol (e.g., "$")
     */
    Currency(final String code, final String description, final String symbol) {
        this.code = code;
        this.description = description;
        this.symbol = symbol;
    }

    /**
     * Returns the currency code.
     *
     * @return the currency code string
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the currency description.
     *
     * @return the currency description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the currency symbol.
     *
     * @return the currency symbol string
     */
    public String getSymbol() {
        return symbol;
    }

    /**
     * Returns the Currency enum matching the given code (case-insensitive).
     *
     * @param code the currency code string to search for
     * @return the matching Currency constant
     * @throws ValidationException if no matching currency code is found
     */
    public static Currency fromCode(final String code) {
        for (var currency : Currency.values()) 
            if (currency.getCode().equalsIgnoreCase(code)) 
                return currency;

        throw new ValidationException("Invalid currency code: " + code);
    }

    /**
     * Returns a formatted menu string listing all available currencies with their codes and descriptions.
     *
     * @return a menu string representing all currency codes and descriptions
     */
    public static String menu() {
        return menu("");
    }

    /**
     * Returns a formatted menu string listing all available currencies with their codes and descriptions,
     * each line optionally prefixed by the given string.
     *
     * @param prefix string prefix for each line in the menu
     * @return the formatted menu string
     */
    public static String menu(final String prefix) {
        StringBuilder sb = new StringBuilder();

        for (var currency : Currency.values()) 
            sb.append(prefix)
                .append(currency.getCode())
                .append(" - ")
                .append(currency.getDescription())
                .append("\n");
        
        return sb.toString();
    }
}
