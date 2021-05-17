package io.helidon.integrations.microstream.core;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Qualifier annotation for Microstream EmbeddedStorageManager.
 *
 */
@Qualifier
@Retention(RUNTIME)
@Target({FIELD, PARAMETER})
public @interface MicrostreamStorage {
    /**
     * Specifies the configuration node used to configure the EmbeddedStorageManager instance to be created.
     *
     * @return the config node
     */
    String configNode();
}
