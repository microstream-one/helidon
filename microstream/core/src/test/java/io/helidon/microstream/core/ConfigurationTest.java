package io.helidon.microstream.core;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import io.helidon.config.ClasspathConfigSource;
import io.helidon.config.Config;
import one.microstream.storage.types.EmbeddedStorageManager;

public class ConfigurationTest {

	@Test
	public void createFromConfigTest() {
		Config helidonConfig = Config.builder().addSource(ClasspathConfigSource.create("/microstreamConfig.yml"))
				.build();
		EmbeddedStorageManagerBuilder embeddedStorageManagerBuilder = EmbeddedStorageManagerBuilder.builder();
		EmbeddedStorageManager embeddedStorageManager = embeddedStorageManagerBuilder.config(helidonConfig).build();
	
		assertNotNull(embeddedStorageManager, "embeddedStorageManager is null!");
	}
}
