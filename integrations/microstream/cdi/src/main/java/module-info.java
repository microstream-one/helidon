module io.helidon.integrations.microstream.cdi {
    exports io.helidon.integrations.microstream.cdi;

    requires transitive cache.api;
    requires io.helidon.common;
    requires io.helidon.common.context;
    requires io.helidon.common.mapper;
    requires io.helidon.common.media.type;
    requires io.helidon.config;
    requires io.helidon.health;
    requires io.helidon.integrations.microstream;
    requires io.helidon.integrations.microstream.cache;
    requires io.helidon.media.common;
    requires io.helidon.media.jsonp;
    requires transitive jakarta.enterprise.cdi.api;
    requires transitive jakarta.inject.api;
    requires jakarta.interceptor.api;
    requires java.annotation;
    requires microstream.base;
    requires microstream.cache;
    requires microstream.persistence;
    requires microstream.storage;
    requires microstream.storage.embedded;

    provides javax.enterprise.inject.spi.Extension
    with io.helidon.integrations.microstream.cdi.EmbeddedStorageManagerExtension,
    io.helidon.integrations.microstream.cdi.CacheExtension;
}