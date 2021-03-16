package io.helidon.integrations.microstream.cache;

import java.lang.reflect.Field;
import java.util.function.Predicate;

import javax.cache.configuration.CacheEntryListenerConfiguration;
import javax.cache.configuration.Factory;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;

import io.helidon.config.Config;
import one.microstream.cache.CacheConfiguration;
import one.microstream.cache.CacheConfiguration.Builder;
import one.microstream.cache.CacheConfigurationBuilderConfigurationBased;
import one.microstream.cache.CacheConfigurationPropertyNames;
import one.microstream.cache.EvictionManager;
import one.microstream.configuration.types.Configuration;

public class MicrostreamCacheConfigurationBuilder<K, V> implements CacheConfigurationPropertyNames,
CacheConfiguration.Builder<K, V>, io.helidon.common.Builder<CacheConfiguration<K, V>> {

	private Builder<K, V> cacheConfigBuilder;

	protected MicrostreamCacheConfigurationBuilder(Class<K> keyType, Class<V> valueType) {
		super();
		cacheConfigBuilder = CacheConfiguration.Builder(keyType, valueType);
	}

	protected MicrostreamCacheConfigurationBuilder(Configuration configuration, Class<K> keyType, Class<V> valueType) {
		super();
		cacheConfigBuilder = CacheConfigurationBuilderConfigurationBased.New().buildCacheConfiguration(configuration,
				CacheConfiguration.Builder(keyType, valueType));
	}

	public static MicrostreamCacheConfigurationBuilder<?,?> builder(Config config) {
		return builder(config, null, null);
	}

	/**
	 * Create a CacheConfiguration builder with default values
	 *
	 * @param <K>
	 * @param <V>
	 * @param keyType
	 * @param valueType
	 * @return
	 */
	public static <K, V> MicrostreamCacheConfigurationBuilder<K, V> builder(Class<K> keyType, Class<V> valueType) {
		return new MicrostreamCacheConfigurationBuilder<>(keyType, valueType);
	}

	/**
	 * Create a CacheConfiguration builder initialized from the supplied helidon
	 * configuration node
	 *
	 * @param <K>
	 * @param <V>
	 * @param config
	 * @param keyType
	 * @param valueType
	 * @return
	 */
	public static <K, V> MicrostreamCacheConfigurationBuilder<K, V> builder(Config config, Class<K> keyType,
			Class<V> valueType) {
		one.microstream.configuration.types.Configuration.Builder configurationBuilder = Configuration.Builder();
		if(config.exists()) {
			config.detach().asMap().get().forEach(configurationBuilder::set);
		}

		Configuration configuration = configurationBuilder.buildConfiguration();
		configuration.opt(KEY_TYPE).ifPresent((s) -> verifyType(s, keyType));
		configuration.opt(VALUE_TYPE).ifPresent((s) -> verifyType(s, valueType));

		return new MicrostreamCacheConfigurationBuilder<K, V>(configuration, keyType, valueType);
	}

	@Override
	public CacheConfiguration<K, V> build() {
		return cacheConfigBuilder.build();
	}

	@Override
	public MicrostreamCacheConfigurationBuilder<K, V> readThrough(boolean readTrough) {
		cacheConfigBuilder.readThrough(readTrough);
		return this;
	}

	@Override
	public MicrostreamCacheConfigurationBuilder<K, V> writeThrough(boolean writeThrough) {
		cacheConfigBuilder.writeThrough(writeThrough);
		return this;
	}

	@Override
	public MicrostreamCacheConfigurationBuilder<K, V> storeByValue(boolean storeByValue) {
		cacheConfigBuilder.storeByValue(storeByValue);
		return this;
	}

	@Override
	public MicrostreamCacheConfigurationBuilder<K, V> enableStatistics(boolean enableStatistics) {
		cacheConfigBuilder.enableStatistics(enableStatistics);
		return this;
	}

	@Override
	public MicrostreamCacheConfigurationBuilder<K, V> enableManagement(boolean enableManagement) {
		cacheConfigBuilder.enableManagement(enableManagement);
		return this;
	}

	@Override
	public MicrostreamCacheConfigurationBuilder<K, V> expiryPolicyFactory(Factory<ExpiryPolicy> expiryPolicyFactory) {
		cacheConfigBuilder.expiryPolicyFactory(expiryPolicyFactory);
		return this;
	}

	@Override
	public MicrostreamCacheConfigurationBuilder<K, V> evictionManagerFactory(
			Factory<EvictionManager<K, V>> evictionManagerFactory) {
		cacheConfigBuilder.evictionManagerFactory(evictionManagerFactory);
		return this;
	}

	@Override
	public MicrostreamCacheConfigurationBuilder<K, V> cacheLoaderFactory(
			Factory<CacheLoader<K, V>> cacheLoaderFactory) {
		cacheConfigBuilder.cacheLoaderFactory(cacheLoaderFactory);
		return this;
	}

	@Override
	public MicrostreamCacheConfigurationBuilder<K, V> cacheWriterFactory(
			Factory<CacheWriter<? super K, ? super V>> cacheWriterFactory) {
		cacheConfigBuilder.cacheWriterFactory(cacheWriterFactory);
		return this;
	}

	@Override
	public MicrostreamCacheConfigurationBuilder<K, V> serializerFieldPredicate(
			Predicate<? super Field> serializerFieldPredicate) {
		cacheConfigBuilder.serializerFieldPredicate(serializerFieldPredicate);
		return this;
	}

	@Override
	public Builder<K, V> addListenerConfiguration(CacheEntryListenerConfiguration<K, V> listenerConfiguration) {
		cacheConfigBuilder.addListenerConfiguration(listenerConfiguration);
		return this;
	}

	private static void verifyType(String typeName, Class<?> actualType) {
		if (!typeName.equals(actualType.getTypeName())) {
			throw new ConfigException("Microstream cache-config type missmatch, expected value from configuration: " + typeName
					+ " but got: " + actualType.getTypeName());
		}
	}
}
