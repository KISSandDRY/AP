package app;

import console.util.ArgParser;  
import console.util.ParsedArgs; 

import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Properties;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.lang.reflect.Modifier;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * Manages application configuration loaded from multiple sources such as
 * environment variables, files, classpath resources, and command-line arguments.
 * 
 * <p>Use {@link #init(Class, IConfigSource...)} to initialize the configuration
 * before accessing it via {@link #getInstance()}.</p>
 *
 * <p>This class is thread-safe and implemented as a singleton.</p>
 */
public final class AppConfig {

    private static final Logger logger = LogManager.getLogger(AppConfig.class);

    private static volatile AppConfig instance;

    private final Map<String, String> values;
    private final Set<String> knownKeys; // A set of keys relevant to app.

    /**
     * Private constructor to prevent instantiation.
     * Use {@link #init(Class, IConfigSource...)} to initialize the singleton instance.
     */
    private AppConfig() {
        throw new UnsupportedOperationException("Use AppConfig.init(...) to initialize");
    }

    private AppConfig(
        final Map<String, String> values, 
        final Set<String> knownKeys
    ) {
        this.values = Collections.unmodifiableMap(values);
        this.knownKeys = Collections.unmodifiableSet(knownKeys);
    }

    /**
     * Initializes the AppConfig with a class defining the known keys and various sources.
     *
     * @param keysInterface The class/interface containing constant string fields for config keys (e.g., ConfigKeys.class).
     * @param sources The configuration sources to load in order of priority.
     */
    public static synchronized void init(
        final Class<?> keysInterface, 
        final IConfigSource... sources
    ) {
        if (instance != null) 
            throw new IllegalStateException("AppConfig already initialized");

        // Use reflection to dynamically discover all defined keys from the interface.
        Set<String> discoveredKeys = new HashSet<>();
        for (var field : keysInterface.getFields()) 
            if (Modifier.isPublic(field.getModifiers()) 
                && Modifier.isStatic(field.getModifiers()) 
                && field.getType().equals(String.class)
            ) {
                try {
                    discoveredKeys.add((String) field.get(null));
                } catch (IllegalAccessException e) {
                    // This should not happen for public static fields.
                    logger.log(Level.WARN, "Could not access key from ConfigKeys: " + field.getName());
                }
            }

        Map<String, String> merged = new LinkedHashMap<>();
        for (var source : sources) 
            merged.putAll(source.load());

        instance = new AppConfig(merged, discoveredKeys);
    }

    /**
     * Returns the singleton instance of this configuration.
     *
     * @return the initialized {@code AppConfig} instance
     * @throws IllegalStateException if {@link #init(Class, IConfigSource...)} was not called
     */
    public static AppConfig getInstance() throws IllegalStateException {
        if (instance == null)
            throw new IllegalStateException("AppConfig not initialized.");

        return instance;
    }

    /**
     * Checks whether the configuration has been initialized.
     *
     * @return {@code true} if initialized, {@code false} otherwise
     */
    public static boolean isInitialized() {
        return instance != null;
    }

    /**
     * Returns a configuration value as a string.
     *
     * @param key the configuration key
     * @return the value, or {@code null} if not found
     */
    public String getString(final String key) {
        return values.get(key);
    }

    /**
     * Returns a configuration value as a string, or a default value if missing.
     *
     * @param key the configuration key
     * @param def the default value
     * @return the value or {@code def} if not found
     */
    public String getString(final String key, final String def) {
        return values.getOrDefault(key, def);
    }

    /**
     * Returns a configuration value as an integer.
     *
     * @param key the configuration key
     * @return the integer value
     * @throws NumberFormatException if the value cannot be parsed
     */
    public int getInt(final String key) throws NumberFormatException {
        String value = values.get(key);
        if (value == null)
            throw new NumberFormatException("Required configuration key not found: " + key);
        
        return Integer.parseInt(value);
    }

    /**
     * Returns a configuration value as an integer, or a default if invalid.
     *
     * @param key the configuration key
     * @param def the default value
     * @return the parsed value or {@code def} if missing or invalid
     */
    public int getInt(final String key, final int def) {
        String value = values.get(key);
        if (value == null) 
            return def; // Return default if key is not present
        
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.log(Level.WARN, "Invalid integer format for key '" + key + "'. Value was '" + value + "'. Using default value " + def + ".");
            return def; // Return default if parsing fails
        }
    }

    /**
     * Returns a configuration value as a double.
     *
     * @param key the configuration key
     * @return the double value
     * @throws NumberFormatException if the value cannot be parsed
     */
    public double getDouble(final String key) throws NumberFormatException {
        String value = values.get(key);
        if (value == null) 
            throw new NumberFormatException("Required configuration key not found: " + key);
        
        return Double.parseDouble(value);
    }

    /**
     * Returns a configuration value as a double, or a default if invalid.
     *
     * @param key the configuration key
     * @param def the default value
     * @return the parsed value or {@code def} if missing or invalid
     */
    public double getDouble(final String key, final double def) {
        String value = values.get(key);
        if (value == null) 
            return def;
        
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            logger.log(Level.WARN, "Invalid double format for key '" + key + "'. Value was '" + value + "'. Using default value " + def + ".");
            return def;
        }
    }

    /**
     * Returns a configuration value as a boolean.
     * Accepted true values are "true", "1", and "yes" (case-insensitive).
     *
     * @param key the configuration key
     * @return {@code true} or {@code false}
     * @throws IllegalStateException if {@link #init(Class, IConfigSource...)} was not called
     */
    public boolean getBoolean(final String key) {
        String val = values.get(key);
        if (val == null)
            throw new IllegalArgumentException("Required configuration key not found: " + key);
        
        return val.equalsIgnoreCase("true")
            || val.equalsIgnoreCase("1")
            || val.equalsIgnoreCase("yes");
    }

    /**
     * Returns a configuration value as a boolean, or a default if missing.
     *
     * @param key the configuration key
     * @param def the default value
     * @return the parsed boolean or {@code def} if missing
     */
    public boolean getBoolean(final String key, final boolean def) {
        String val = values.get(key);
        if (val == null) 
            return def;
        
        return val.equalsIgnoreCase("true")
            || val.equalsIgnoreCase("1")
            || val.equalsIgnoreCase("yes");
    }

    /**
     * Returns a formatted string listing all known configuration keys and their values.
     *
     * @return human-readable configuration summary
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("\"" + this.getClass().getName() + "\": {\n");

        for (var key : knownKeys.stream().sorted().toList()) {
            String value = values.getOrDefault(key, "<null>");
            sb.append(String.format("  \"%s\": \"%s\",%n", key, value));
        }

        return sb.append("}").toString();
    }

    /**
     * Represents a generic configuration source (environment, file, CLI, etc.).
     */
    public interface IConfigSource {

        /**
         * Loads configuration values as key–value pairs.
         *
         * @return a map containing configuration entries
         */
        Map<String, String> load();
    }

    /**
     * Loads configuration from environment variables.
     * Converts keys from {@code UPPER_CASE_UNDERSCORE} to {@code lower.case.dot} format.
     */
    public static final class EnvConfigSource implements IConfigSource {

        /**
         * Default constructor.
         */
        public EnvConfigSource() { }
        
        /** {@inheritDoc} */
        @Override
        public Map<String, String> load() {
            Map<String, String> map = new HashMap<>();

            for (var entry : System.getenv().entrySet()) {
                // automatically converts UPPER_CASE_WITH_UNDERSCORES to lower.case.with.dots.
                String key = entry.getKey().toLowerCase().replace('_', '.');
                map.put(key, entry.getValue());
            }

            return map;
        }
    }

    /**
     * Loads configuration from command-line arguments.
     */
    public static final class CLIConfigSource implements IConfigSource {

        private final String[] args;

        /**
         * Creates a CLI configuration source.
         *
         * @param args the command-line arguments
         */
        public CLIConfigSource(final String[] args) { 
            this.args = args; 
        }

        /** {@inheritDoc} */
        @Override
        public Map<String, String> load() {
            ParsedArgs parsedArgs = ArgParser.parse(this.args);

            return new HashMap<>(parsedArgs.getOptions());
        }
    }

    /**
     * Loads configuration from a properties file located in the classpath.
     */
    public static class ClasspathConfigSource implements IConfigSource {

        private final String resourceName;

        /**
         * Creates a classpath configuration source.
         *
         * @param resourceName the classpath resource name (e.g. {@code "config.properties"})
         */
        public ClasspathConfigSource(String resourceName) {
            this.resourceName = resourceName;
        }

        /** {@inheritDoc} */
        @Override
        public Map<String, String> load() {
            Properties props = new Properties();

            try (InputStream is = AppConfig.class.getClassLoader().getResourceAsStream(resourceName)) {
                if (is == null) {
                    logger.log(Level.WARN, "Could not find resource file '" + resourceName + "' on the classpath.");
                    return Collections.emptyMap();
                }

                props.load(is);
            } catch (IOException e) {
                logger.log(Level.WARN, "Error reading resource file '" + resourceName + "': " + e.getMessage());
            }

            Map<String, String> map = new HashMap<>();
            for (var name : props.stringPropertyNames()) 
                map.put(name, props.getProperty(name));
            
            return map;
        }
    }

    /**
     * Loads configuration from an external properties file on disk.
     */
    public static class FileConfigSource implements IConfigSource {

        private final String path;
        
        /**
         * Creates a file configuration source.
         *
         * @param path the file path to the configuration file
         */
        public FileConfigSource(final String path) { 
            this.path = path; 
        }

        /** {@inheritDoc} */
        @Override
        public Map<String, String> load() {
            Properties props = new Properties();

            try (FileInputStream fis = new FileInputStream(path)) {
                props.load(fis);
            } catch (IOException e) {
                logger.log(Level.WARN, "Could not find config file.");
            }

            Map<String, String> map = new HashMap<>();

            for (var name : props.stringPropertyNames())
                map.put(name, props.getProperty(name));
            
            return map;
        }
    }
}
