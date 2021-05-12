module io.helidon.integrations.microstream {
	exports io.helidon.integrations.microstream.health;

	requires transitive io.helidon.health;
	requires transitive io.helidon.common;
	requires transitive microstream.storage.embedded;
}