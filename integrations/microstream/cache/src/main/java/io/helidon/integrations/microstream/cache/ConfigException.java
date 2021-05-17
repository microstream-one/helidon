package io.helidon.integrations.microstream.cache;

/**
 * RuntimeException thrown in case of Microstream Cache configuration problems.
 *
 */
public class ConfigException extends RuntimeException {

    /**
     * creates a new ConfigException.
     *
     * @param message exception message
     * @param cause the cause
     */
    public ConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * creates a new ConfigException.
     *
     * @param message exception message
     */
    public ConfigException(String message) {
        super(message);
    }

}
