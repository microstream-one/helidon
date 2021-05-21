package io.helidon.integrations.microstream.cdi;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Creates a cache based upon the Microstream JCache implementation.
 * <br>See <a href="https://manual.docs.microstream.one/cache/overview">Microstream JCache</a>
 * <p>
 * Specify the cache name by the name property.
 * <p>
 * The configNode property expects an existing Helidon config node providing configuration properties for the cache.
 * <br>See <a href="https://manual.docs.microstream.one/cache/configuration">Microstream JCache configuration</a>
 * <br>If not provided the properties below "one.microstream.cache.default" will be used if existing.
 * Otherwise the build in defaults are applied.
 *
 */
@Qualifier
@Retention(RUNTIME)
@Target({PARAMETER, FIELD})
public @interface MicrostreamCache {
    /**
     * Specifies the configuration node used to configure the EmbeddedStorageManager instance to be created.
     *
     * @return the configuration node
     */
    String configNode() default "one.microstream.cache.default";

    /**
     * Specifies the cache name.
     *
     * @return the cache name
     */
    String name();
}
