package deposit.interest;

/**
 * Enumeration representing types of interest rates.
 * Each enum constant has a code and a description.
 */
public enum InterestRateType {

    /**
     * Fixed interest rate type.
     */
    FIXED("FIXED", "Fixed Interest Rate"),

    /**
     * Floating interest rate type.
     */
    FLOATING("FLOATING", "Floating Interest Rate");

    private final String code;
    private final String description;

    /**
     * Constructs an InterestRateType with a code and description.
     *
     * @param code the unique code for the interest rate type
     * @param description a human-readable description of the type
     */
    InterestRateType(final String code, final String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Returns the code of this interest rate type.
     *
     * @return the code string
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the description of this interest rate type.
     *
     * @return the description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the InterestRateType corresponding to the given code, ignoring case.
     *
     * @param code the code string to look up
     * @return the matching InterestRateType
     * @throws IllegalArgumentException if no matching type is found
     */
    public static InterestRateType fromCode(final String code) throws IllegalArgumentException {
        for (var type : InterestRateType.values()) 
            if (type.getCode().equalsIgnoreCase(code)) 
                return type;
        
        throw new IllegalArgumentException("Invalid code for InterestRateType: " + code);
    }

    /**
     * Returns a formatted string listing all interest rate types with descriptions.
     *
     * @return the menu string
     */
    public static String menu() {
        return menu("");
    }

    /**
     * Returns a formatted string listing all interest rate types with descriptions,
     * prefixed by the given string on each line.
     *
     * @param prefix string prefix for each line
     * @return the formatted menu string
     */
    public static String menu(final String prefix) {
        StringBuilder sb = new StringBuilder();

        for (var type : InterestRateType.values()) 
            sb.append(prefix)
                .append(type.getCode())
                .append(" - ")
                .append(type.getDescription())
                .append("\n");
        
        return sb.toString();
    }
}
