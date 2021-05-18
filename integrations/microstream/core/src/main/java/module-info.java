module io.helidon.integrations.microstream {
    exports io.helidon.integrations.microstream.core;

    requires transitive io.helidon.common;
    requires io.helidon.common.context;
    requires io.helidon.common.mapper;
    requires io.helidon.common.media.type;
    requires transitive io.helidon.config;
    requires transitive microstream.afs;
    requires transitive microstream.base;
    requires transitive microstream.configuration;
    requires transitive microstream.persistence;
    requires transitive microstream.storage;
    requires transitive microstream.storage.embedded;
    requires transitive microstream.storage.embedded.configuration;

}