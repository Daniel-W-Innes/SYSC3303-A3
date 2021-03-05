package util;

import java.io.IOException;
import java.util.Properties;

/**
 * The configuration file loader. Loads the properties from a given CONFIG_FILE_NAME.
 *
 * @version Feb 27, 2021
 */
public class Config {
    /**
     * The file name of the configuration file.
     */
    private static final String CONFIG_FILE_NAME = "config.cfg";
    /**
     * The configuration properties from the configuration file.
     */
    private final Properties properties;

    /**
     * The default constructor to load the configuration file.
     *
     * @throws IOException If there is a problem loading the file.
     */
    public Config() throws IOException {
        properties = new Properties();
        properties.load(this.getClass().getClassLoader().
                getResourceAsStream(CONFIG_FILE_NAME));
    }

    /**
     * Get a property from the configuration file.
     *
     * @param key The key/label of the property in the file.
     * @return The property as a string.
     */
    public String getProperty(String key) {
        return this.properties.getProperty(key);
    }

    /**
     * Get a property as a integer from the configuration file.
     *
     * @param key The key/label of the property in the file.
     * @return The property as a integer.
     * @throws NumberFormatException If the property does not contain a parsable integer.
     */
    public int getIntProperty(String key) {
        return Integer.parseInt(getProperty(key));
    }

    /**
     * Get a property as a float from the configuration file.
     *
     * @param key The key/label of the property in the file.
     * @return The property as a integer.
     * @throws NumberFormatException If the property does not contain a parsable float.
     */
    public float getFloatProperty(String key) {
        return Float.parseFloat(getProperty(key));
    }
}