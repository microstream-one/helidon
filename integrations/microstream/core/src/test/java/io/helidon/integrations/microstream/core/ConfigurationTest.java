package io.helidon.integrations.microstream.core;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.StringContainsInOrder.stringContainsInOrder;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.helidon.config.ClasspathConfigSource;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import io.helidon.integrations.microstream.core.EmbeddedStorageManagerBuilder;
import one.microstream.storage.types.EmbeddedStorageManager;

public class ConfigurationTest {

	@TempDir
	Path tempDir;

	@Test
	public void createFromConfigTest() {

		Config helidonConfig = Config.builder().addSource(ClasspathConfigSource.create("/microstreamConfig.yml"))
				.addSource(ConfigSources.create(Map.of("microstream.storage-directory", tempDir.toString())))
				.build();

		EmbeddedStorageManagerBuilder embeddedStorageManagerBuilder = EmbeddedStorageManagerBuilder.builder();
		EmbeddedStorageManager embeddedStorageManager = embeddedStorageManagerBuilder.config(helidonConfig.get("microstream")).build();

		assertNotNull(embeddedStorageManager, "embeddedStorageManager is null!");

		//need to compare strings as the microstream APath is not a java path
		assertThat(tempDir.toString(), stringContainsInOrder(
				Arrays.asList(embeddedStorageManager.configuration().fileProvider().baseDirectory().toPath())));
				

		assertThat(embeddedStorageManager.configuration().channelCountProvider().getChannelCount(), is(4));
		assertThat(embeddedStorageManager.configuration().housekeepingController().housekeepingIntervalMs(), is(2000L));
	}
}
