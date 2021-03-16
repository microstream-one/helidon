package io.helidon.integrations.microstream.cache;

import java.nio.file.Path;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.helidon.config.ClasspathConfigSource;
import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import one.microstream.cache.Cache;
import one.microstream.cache.CacheConfiguration;

public class CacheTest {

	@TempDir
	Path tempDir;

	@Test
	public void createCacheTest()
	{
		CacheConfiguration<Integer, String> config = MicrostreamCacheConfigurationBuilder.builder(Integer.class, String.class)
				.build();
		CacheBuilder<Integer, String> builder = CacheBuilder.builder(config, Integer.class, String.class);
		Cache<Integer, String> cache = builder.build("testCache");

		cache.put(1, "Hello");

		cache.close();
	}

	@Test
	public void createCacheFromConfigTest()
	{
		Config helidonConfig = Config.builder().addSource(ClasspathConfigSource.create("/microstreamCacheConfig.yml"))
				.addSource(ConfigSources.create(Map.of("cache.microstream.storage.storage-directory", tempDir.toString())))
				.build();

		CacheConfiguration<Integer, String> config = MicrostreamCacheConfigurationBuilder
				.builder(helidonConfig.get("cache.microstream"), Integer.class, String.class)
				.build();

		Cache<Integer, String> cache = CacheBuilder.builder(config, Integer.class, String.class).build("Cache_IntStr");

		cache.put(1, "Hello");

		cache.close();
	}
}
