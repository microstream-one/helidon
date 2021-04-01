package io.helidon.integrations.microstream.cache;

import javax.cache.Caching;

import io.helidon.config.Config;
import one.microstream.cache.types.Cache;
import one.microstream.cache.types.CacheConfiguration;
import one.microstream.cache.types.CacheManager;
import one.microstream.cache.types.CachingProvider;

public class CacheBuilder<K, V> {
	private static final String MICROSTREAM_CACHING_PROVIDER = "one.microstream.cache.types.CachingProvider";
	private CachingProvider provider;
	private CacheManager cacheManager;
	private CacheConfiguration<K, V> configuration;

	protected CacheBuilder(CacheConfiguration<K, V> configuration) {
		super();

		this.configuration = configuration;
		this.provider = (CachingProvider) Caching.getCachingProvider(MICROSTREAM_CACHING_PROVIDER);
		this.cacheManager = provider.getCacheManager();
	}

	protected CacheBuilder(Class<K> keyType, Class<V> valueType) {
		this.provider = (CachingProvider) Caching.getCachingProvider(MICROSTREAM_CACHING_PROVIDER);
		this.cacheManager = provider.getCacheManager();
		this.configuration = CacheConfiguration.Builder(keyType, valueType).build();
	}

	public static <K, V> CacheBuilder<K, V> builder(CacheConfiguration<K, V> configuration, Class<K> keyType,
			Class<V> valueType) {
		return new CacheBuilder<K, V>(configuration);
	}

	public Cache<K, V> build(String name) {
		return cacheManager.createCache(name, configuration);
	}

	public static Cache<?, ?> create(String name, Config config, Class<?> keyType, Class<?> valueType) {

		CacheConfiguration<?, ?> cacheConfig = MicrostreamCacheConfigurationBuilder.builder(config, keyType, valueType)
				.build();
		return new CacheBuilder<>(cacheConfig).build(name);
	}
}
