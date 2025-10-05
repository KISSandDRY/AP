package deposit.interest;

/**
 * Enumeration representing types of interest calculation strategies.
 * Each enum constant has a code and a description.
 */
public enum InterestStrategyType {

    /**
     * Simple interest calculation strategy.
     */
    SIMPLE("SIMPLE", "Simple Interest Strategy"),

    /**
     * Compound interest calculation strategy.
     */
    COMPOUND("COMPOUND", "Compound Interest Strategy");

    private final String code;
    private final String description;

    /**
     * Constructs an InterestStrategyType with a code and description.
     *
     * @param code the unique code for the interest strategy type
     * @param description a human-readable description of the type
     */
    InterestStrategyType(final String code, final String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * Returns the code of this interest strategy type.
     *
     * @return the code string
     */
    public String getCode() {
        return code;
    }

    /**
     * Returns the description of this interest strategy type.
     *
     * @return the description string
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the InterestStrategyType corresponding to the given code, ignoring case.
     *
     * @param code the code string to look up
     * @return the matching InterestStrategyType
     * @throws IllegalArgumentException if no matching type is found
     */
    public static InterestStrategyType fromCode(final String code) {
        for (var type : InterestStrategyType.values()) 
            if (type.getCode().equalsIgnoreCase(code)) 
                return type;
        
        throw new IllegalArgumentException("Invalid code for InterestStrategyType: " + code);
    }

    /**
     * Returns a formatted string listing all interest strategy types with descriptions.
     *
     * @return the menu string
     */
    public static String menu() {
        return menu("");
    }

    /**
     * Returns a formatted string listing all interest strategy types with descriptions,
     * prefixed by the given string on each line.
     *
     * @param prefix string prefix for each line
     * @return the formatted menu string
     */
    public static String menu(final String prefix) {
        StringBuilder sb = new StringBuilder();

        for (var type : InterestStrategyType.values()) 
            sb.append(prefix)
                .append(type.getCode())
                .append(" - ")
                .append(type.getDescription())
                .append("\n");
        
        return sb.toString();
    }
}
