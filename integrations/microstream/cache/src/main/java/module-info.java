module io.helidon.integrations.microstream.cache {
    exports io.helidon.integrations.microstream.cache;

    requires transitive cache.api;
    requires io.helidon.common;
    requires io.helidon.common.context;
    requires io.helidon.common.mapper;
    requires io.helidon.common.media.type;
    requires io.helidon.config;
    requires io.helidon.health;
    requires io.helidon.media.common;
    requires io.helidon.media.jsonp;
    requires transitive microstream.cache;
    requires transitive io.helidon.integrations.microstream;
}