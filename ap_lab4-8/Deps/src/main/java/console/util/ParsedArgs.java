package console.util;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.Optional;
import java.util.Collections;

/**
 * Container for parsed command-line arguments by {@link ArgParser}.
 * Separates boolean flags, options with values, and positional arguments for easy access.
 */
public final class ParsedArgs {

    private final Set<String> flags; // e.g., --verbose, -v
    private final Map<String, String> options; // e.g., --file=out.txt, -f out.txt
    private final List<String> positionalArgs; // e.g., the filenames at the end

    /**
     * Constructs a ParsedArgs instance with given flags, options, and positional arguments.
     * Collections are wrapped as unmodifiable to ensure immutability.
     *
     * @param flags the set of boolean flags present in the arguments
     * @param options the map of option names to their values
     * @param positionalArgs the list of positional arguments
     */
    public ParsedArgs(
        final Set<String> flags, 
        final Map<String, String> options, 
        final List<String> positionalArgs
    ) {
        this.flags = Collections.unmodifiableSet(flags);
        this.options = Collections.unmodifiableMap(options);
        this.positionalArgs = Collections.unmodifiableList(positionalArgs);
    }

    /**
     * Checks if the specified flag is present.
     *
     * @param flag the flag to check, e.g., "--verbose"
     * @return true if the flag is present, false otherwise
     */
    public boolean hasFlag(final String flag) {
        return flags.contains(flag);
    }

    /**
     * Checks if the specified short or long flag is present.
     *
     * @param longName the long flag to check, e.g., "--verbose"
     * @param shortName the short flag to check, e.g., "-v"
     * @return true if the flag is present, false otherwise
     */
    public boolean hasFlag(final String longName, final String shortName) {
        return flags.contains(longName) || flags.contains(shortName);
    }

    /**
     * Retrieves the value of an option by checking for either its long or short flag.
     *
     * @param longOptionName  the long form of the option name, e.g., "--file"
     * @param shortOptionName the short form of the option name, e.g., "-f"
     * @return an Optional containing the option value if present, or empty if not
     */
    public Optional<String> getOption(final String longOptionName, final String shortOptionName) {
        return Optional.ofNullable(options.get(longOptionName))
                       .or(() -> Optional.ofNullable(options.get(shortOptionName)));
    }

    /**
     * Retrieves the value of a specific option.
     *
     * @param optionName the option name, e.g., "--file"
     * @return an Optional containing the option value if present, or empty if not
     */
    public Optional<String> getOption(final String optionName) {
        return Optional.ofNullable(options.get(optionName));
    }

    /**
     * Retrieves the value of a specific option or returns the default value if not found.
     *
     * @param optionName the option name
     * @param defaultValue the default value to return if option is not present
     * @return the option value or the default value
     */
    public String getOptionOrDefault(final String optionName, final String defaultValue) {
        return options.getOrDefault(optionName, defaultValue);
    }

    /**
     * Returns the list of positional arguments.
     *
     * @return an unmodifiable list of positional arguments
     */
    public List<String> getPositionalArgs() {
        return positionalArgs;
    }

    /**
     * Returns the map of all options and their values.
     *
     * @return an unmodifiable map of options
     */
    public Map<String, String> getOptions() {
        return this.options;
    }
}
