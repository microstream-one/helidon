package io.helidon.integrations.microstream.cache;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.sameInstance;
import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Predicate;

import javax.cache.configuration.Factory;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.integration.CacheLoader;
import javax.cache.integration.CacheWriter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.helidon.config.Config;
import io.helidon.config.ConfigSources;
import one.microstream.cache.types.CacheConfiguration;
import one.microstream.cache.types.EvictionManager;

public class ConfigurationTest {

	/**
	 * Test if all default properties are set.
	 */
	@Test
	public void defaultValuesTest() {
		CacheConfiguration<Integer, String> cacheConfig = MicrostreamCacheConfigurationBuilder
				.builder(Integer.class, String.class).build();

		assertAll(() -> assertThat("getKeyType", cacheConfig.getKeyType(), typeCompatibleWith(Integer.class)),
				() -> assertThat("getValueType", cacheConfig.getValueType(), typeCompatibleWith(String.class)),
				() -> assertThat("isManagementEnabled", cacheConfig.isManagementEnabled(), is(false)),
				() -> assertThat("isStatisticsEnabled", cacheConfig.isStatisticsEnabled(), is(false)),
				() -> assertThat("isReadThrough", cacheConfig.isReadThrough(), is(false)),
				() -> assertThat("isWriteThrough", cacheConfig.isWriteThrough(), is(false)),
				() -> assertThat("isStoreByValue", cacheConfig.isStoreByValue(), is(false)),
				() -> assertThat("getExpiryPolicyFactory", cacheConfig.getExpiryPolicyFactory(),
						is(CacheConfiguration.DefaultExpiryPolicyFactory())),
				() -> assertThat("getEvictionManagerFactory", cacheConfig.getEvictionManagerFactory(),
						is(CacheConfiguration.DefaultEvictionManagerFactory())),
				() -> assertThat("getCacheLoaderFactory", cacheConfig.getCacheLoaderFactory(), nullValue()),
				() -> assertThat("getCacheWriterFactory", cacheConfig.getCacheWriterFactory(), nullValue()),
				() -> assertThat("getCacheEntryListenerConfigurations",
						cacheConfig.getCacheEntryListenerConfigurations(), emptyIterable()),
				() -> assertThat("getSerializerFieldPredicate", cacheConfig.getSerializerFieldPredicate(),
						is(CacheConfiguration.DefaultSerializerFieldPredicate())));
	}

	/**
	 * Test if simple configuration values are applied. This test does not check all
	 * values
	 */
	@Test
	public void configValuesTest() {
		Map<String, String> source = Map.of("cache.management-enabled", "true", "cache.statistics-enabled", "true",
				"cache.store-by-value", "true");

		Config config = Config.builder().addSource(ConfigSources.create(source).build()).build();

		CacheConfiguration<Long, String> cacheConfig = MicrostreamCacheConfigurationBuilder
				.builder(config.get("cache"), Long.class, String.class).build();

		assertAll(() -> assertThat("getKeyType", cacheConfig.getKeyType(), typeCompatibleWith(Long.class)),
				() -> assertThat("getValueType", cacheConfig.getValueType(), typeCompatibleWith(String.class)),
				() -> assertThat("isManagementEnabled", cacheConfig.isManagementEnabled(), is(true)),
				() -> assertThat("isStatisticsEnabled", cacheConfig.isStatisticsEnabled(), is(true)),
				() -> assertThat("isStoreByValue", cacheConfig.isStoreByValue(), is(true)));
	}

	/**
	 * Test if settings from config can be altered by code
	 */
	@Test
	public void applyChangeTest() {

		Map<String, String> source = Map.of("cache.management-enabled", "true", "cache.statistics-enabled", "true",
				"cache.store-by-value", "true");

		Config config = Config.builder().addSource(ConfigSources.create(source).build()).build();

		CacheConfiguration<Long, String> cacheConfig = MicrostreamCacheConfigurationBuilder
				.builder(config.get("cache"), Long.class, String.class).disableManagement().storeByValue(false).build();

		assertAll(() -> assertThat("isManagementEnabled", cacheConfig.isManagementEnabled(), is(false)),
				() -> assertThat("isStatisticsEnabled", cacheConfig.isStatisticsEnabled(), is(true)),
				() -> assertThat("isStoreByValue", cacheConfig.isStoreByValue(), is(false)));
	}

	@Test
	public void cacheLoaderFactoryTest() {
		@SuppressWarnings("unchecked")
		Factory<CacheLoader<Integer, String>> cacheLoaderFactory = Mockito.mock(Factory.class);

		CacheConfiguration<Integer, String> cacheConfig = MicrostreamCacheConfigurationBuilder
				.builder(Integer.class, String.class).cacheLoaderFactory(cacheLoaderFactory).build();

		assertThat(cacheConfig.getCacheLoaderFactory(), sameInstance(cacheLoaderFactory));
	}

	@Test
	public void cacheWriterFactoryTest() {
		@SuppressWarnings("unchecked")
		Factory<CacheWriter<? super Integer, ? super String>> cacheWriterFactory = Mockito.mock(Factory.class);

		CacheConfiguration<Integer, String> cacheConfig = MicrostreamCacheConfigurationBuilder
				.builder(Integer.class, String.class).cacheWriterFactory(cacheWriterFactory).build();

		assertThat(cacheConfig.getCacheWriterFactory(), sameInstance(cacheWriterFactory));
	}

	@Test
	public void expiryPolicyFactoryTest() {
		@SuppressWarnings("unchecked")
		Factory<ExpiryPolicy> expiryPolicyFactory = Mockito.mock(Factory.class);

		CacheConfiguration<Integer, String> cacheConfig = MicrostreamCacheConfigurationBuilder
				.builder(Integer.class, String.class).expiryPolicyFactory(expiryPolicyFactory).build();

		assertThat(cacheConfig.getExpiryPolicyFactory(), sameInstance(expiryPolicyFactory));
	}

	@Test
	public void evictionManagerFactoryTest() {
		@SuppressWarnings("unchecked")
		Factory<EvictionManager<Integer, String>> evictionManagerFactory = Mockito.mock(Factory.class);

		CacheConfiguration<Integer, String> cacheConfig = MicrostreamCacheConfigurationBuilder
				.builder(Integer.class, String.class).evictionManagerFactory(evictionManagerFactory).build();

		assertThat(cacheConfig.getEvictionManagerFactory(), sameInstance(evictionManagerFactory));
	}

	@Test
	public void serializerFieldPredicate() {
		@SuppressWarnings("unchecked")
		Predicate<? super Field> serializerFieldPredicate = Mockito.mock(Predicate.class);

		CacheConfiguration<Integer, String> cacheConfig = MicrostreamCacheConfigurationBuilder
				.builder(Integer.class, String.class).serializerFieldPredicate(serializerFieldPredicate).build();

		assertThat(cacheConfig.getSerializerFieldPredicate(), sameInstance(serializerFieldPredicate));
	}
}
