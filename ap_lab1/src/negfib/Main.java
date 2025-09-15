package negfib;

/**
 * Main entry point for the NegFib program.
 * <p>
 * Evaluates {@literal N <= 0} number in fibonacci sequence
 * and prints result to standard output.
 * </p>
 *
 * <h2>Usage</h2>
 * 
 * <pre>
 *   javac src/negfib/Main.java
 *   java src.negfib.Main &lt;number&gt;
 * </pre>
 *
 * <h2>Input</h2>
 * <ul>
 * <li><b>number</b> – a long, the maximum NegFib number index to generate</li>
 * </ul>
 *
 * <h2>Output</h2>
 * Prints the value in NeoFib on index number.
 */
public class Main {
    /**
     * Prints help info if no arguments were passed.
     */
    static void printHelp() {
        System.out.print("Usage:\n\t$ java src.negfib.Main {number}\nExample: $ java src.negfib.Main -10");
    }

    /**
     * Program entry point.
     *
     * @param args command-line arguments:
     *             <ul>
     *             <li>{@code args[0]} – the number index</li>
     *             </ul>
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            printHelp();
            return;
        }

        final int number = Integer.parseInt(args[0]);

        NegFib res = new NegFib(number);

        System.out.println("NegFib number: " + number + "\n\tValue: " + res.getValue());
    }
}
