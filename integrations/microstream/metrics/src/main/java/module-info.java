module io.helidon.integrations.microstream {
	exports io.helidon.integrations.microstream.metrics;

	requires transitive io.helidon.common;
	requires transitive io.helidon.config;
	requires transitive io.helidon.metrics;
	requires transitive microstream.storage.embedded;
}