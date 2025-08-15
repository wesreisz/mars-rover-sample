package com.example.mars.app;

import com.example.mars.exec.BoundaryPolicy;

/**
 * Encapsulates command-line argument parsing and validation for the Mars Rover application.
 * Supports parsing boundary policy flags and error handling mode flags, with validation
 * to ensure mutually exclusive options are handled correctly.
 * 
 * <p>Supported flags:
 * <ul>
 *   <li>{@code --strict} - Use strict boundary enforcement (default)</li>
 *   <li>{@code --ignore-oob} - Ignore out-of-bounds moves</li>
 *   <li>{@code --stop-on-oob} - Stop rover on out-of-bounds detection</li>
 *   <li>{@code --fail-fast} - Stop on first error (default)</li>
 *   <li>{@code --collect-errors} - Continue processing after errors (future extension)</li>
 * </ul>
 * 
 * @see BoundaryPolicy
 * @see CliArgumentsException
 */
public class CliArguments {

    private final boolean ignoreOob;
    private final boolean stopOnOob;
    private final boolean failFast;
    private final boolean collectErrors;

    /**
     * Creates a new CliArguments instance with the specified flag settings.
     * 
     * @param ignoreOob whether to ignore out-of-bounds moves
     * @param stopOnOob whether to stop on out-of-bounds detection
     * @param failFast whether to fail fast on errors
     * @param collectErrors whether to collect errors instead of failing fast
     */
    private CliArguments(boolean ignoreOob, boolean stopOnOob, 
                        boolean failFast, boolean collectErrors) {
        this.ignoreOob = ignoreOob;
        this.stopOnOob = stopOnOob;
        this.failFast = failFast;
        this.collectErrors = collectErrors;
    }

    /**
     * Parses command-line arguments and returns a validated CliArguments instance.
     * 
     * @param args the command-line arguments to parse
     * @return parsed and validated CLI arguments
     * @throws CliArgumentsException if arguments are invalid or conflicting
     */
    public static CliArguments parse(String[] args) throws CliArgumentsException {
        boolean ignoreOob = false;
        boolean stopOnOob = false;
        boolean failFast = true;  // default
        boolean collectErrors = false;

        for (String arg : args) {
            switch (arg) {
                case "--strict":
                    ignoreOob = false;
                    stopOnOob = false;
                    break;
                case "--ignore-oob":
                    ignoreOob = true;
                    stopOnOob = false;
                    break;
                case "--stop-on-oob":
                    ignoreOob = false;
                    stopOnOob = true;
                    break;
                case "--fail-fast":
                    failFast = true;
                    collectErrors = false;
                    break;
                case "--collect-errors":
                    failFast = false;
                    collectErrors = true;
                    break;
                default:
                    throw new CliArgumentsException(formatUsageError("Unknown option: " + arg));
            }
        }

        return new CliArguments(ignoreOob, stopOnOob, failFast, collectErrors);
    }

    /**
     * Returns the appropriate boundary policy based on the parsed flags.
     * 
     * @return the boundary policy to use for mission execution
     */
    public BoundaryPolicy getBoundaryPolicy() {
        if (ignoreOob) {
            return BoundaryPolicy.IGNORE;
        }
        if (stopOnOob) {
            return BoundaryPolicy.STOP_ON_OOB;
        }
        return BoundaryPolicy.STRICT; // default
    }

    /**
     * Returns whether fail-fast mode is enabled.
     * 
     * @return true if execution should stop on first error, false otherwise
     */
    public boolean isFailFast() {
        return failFast;
    }

    /**
     * Returns whether error collection mode is enabled.
     * 
     * @return true if errors should be collected instead of stopping execution
     */
    public boolean isCollectErrors() {
        return collectErrors;
    }

    /**
     * Formats a usage error message with complete usage information.
     * 
     * @param errorMessage the specific error message to include
     * @return formatted usage message
     */
    private static String formatUsageError(String errorMessage) {
        return "Usage: java -jar mars-rovers.jar [OPTIONS]\n" +
               "Options:\n" +
               "  --strict      Fail on out-of-bounds moves (default)\n" +
               "  --ignore-oob  Skip out-of-bounds moves\n" +
               "  --stop-on-oob Stop rover on out-of-bounds\n" +
               "  --fail-fast   Stop on first error (default)\n" +
               "  --collect-errors Continue processing after errors\n" +
               "\n" +
               errorMessage;
    }
}