package app;

/**
 * Utility class to retrieve application metadata such as name and version.
 * Uses package implementation information to provide these details.
 */
public final class AppInfo {

    /**
     * Private constructor to prevent instantiation.
     */
    private AppInfo() { }

    private final static Package pkg = AppInfo.class.getPackage();

    /**
     * Returns the full name of the application including version in parentheses.
     *
     * @return formatted full application name, e.g. "MyApp (1.0.0)"
     */
    public static String getFullAppName() {
        return String.format("%s (%s)", getAppName(), getAppVersion());
    }

    /**
     * Returns the application name from the package implementation title.
     *
     * @return the application name or null if not specified
     */
    public static String getAppName() {
        return pkg.getImplementationTitle();
    }

    /**
     * Returns the application version from the package implementation version.
     *
     * @return the application version or null if not specified
     */
    public static String getAppVersion() {
        return pkg.getImplementationVersion();
    }

    /**
     * Returns the full application name as string representation.
     *
     * @return the full application name including version
     */
    @Override
    public String toString() {
        return getFullAppName();
    }
}
