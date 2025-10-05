package console.util;

import java.util.Set;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;

/**
 * A universal command-line argument parser.
 * Supports parsing of long flags (--verbose), combined short flags (-vdf),
 * options with values (--file=out.txt or --file out.txt), and positional arguments.
 * Recognizes '--' as a terminator to stop parsing flags.
 */
public final class ArgParser {

    /**
     * Default consructor.
     */
    public ArgParser() { }

    /**
     * Parses an array of command-line arguments into a structured {@link ParsedArgs} object.
     *
     * @param args the raw command-line arguments to parse
     * @return a {@link ParsedArgs} instance separating flags, options, and positional arguments
     */
    public static ParsedArgs parse(final String[] args) {
        Set<String> flags = new HashSet<>();
        Map<String, String> options = new HashMap<>();
        List<String> positionalArgs = new ArrayList<>();
        boolean flagsEnded = false;

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];

            if (flagsEnded) {
                positionalArgs.add(arg);
                continue;
            }

            if (arg.equals("--")) {
                flagsEnded = true;
                continue;
            }

            if (arg.startsWith("--")) {

                // Long option, e.g., --file=output.txt or --verbose
                String longOpt = arg.substring(2);

                if (longOpt.contains("=")) {

                    String[] parts = longOpt.split("=", 2);
                    options.put(parts[0], parts[1]);

                } else if (i + 1 < args.length && !args[i + 1].startsWith("-")) {

                    // Option with next argument as value, e.g., --file output.txt
                    options.put(longOpt, args[i + 1]);
                    i++; // Skip the next argument since we've consumed it
                    //
                } else {

                    // Boolean flag, e.g., --verbose
                    flags.add(longOpt);
                }

            } else if (arg.startsWith("-")) {

                // Short option(s), e.g., -vdf or -f file.txt
                String shortOpts = arg.substring(1);

                if (shortOpts.length() > 1 && (i + 1 >= args.length || args[i + 1].startsWith("-"))) {

                    // Combined boolean flags, e.g., -vdf
                    for (var c : shortOpts.toCharArray()) 
                        flags.add(String.valueOf(c));
                    
                } else if (i + 1 < args.length && !args[i + 1].startsWith("-")) {

                    // Short option with value, e.g., -f file.txt
                    options.put(shortOpts, args[i + 1]);
                    i++;

                } else {

                    // Single boolean flag, e.g., -v
                    flags.add(shortOpts);
                }

            } else {

                // Positional argument
                positionalArgs.add(arg);
            }
        }

        return new ParsedArgs(flags, options, positionalArgs);
    }
}
