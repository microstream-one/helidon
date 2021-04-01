package io.helidon.integrations.microstream.cache;

import static javax.interceptor.Interceptor.Priority.PLATFORM_BEFORE;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessInjectionPoint;

import io.helidon.common.GenericType;
import io.helidon.config.Config;
import one.microstream.cache.types.Cache;

public class CacheExtension implements Extension {

	private Config config;

	private Set<Descriptor> cacheBeans;

	private static class Descriptor {

		private final Set<Annotation> annotations;
		private final ParameterizedType types;

		private Descriptor(Set<Annotation> cacheBeans, ParameterizedType types) {
			super();
			this.annotations = cacheBeans;
			this.types = types;
		}

		public Set<Annotation> getAnnotations() {
			return annotations;
		}

		public ParameterizedType getTypes() {
			return types;
		}

		@Override
		public int hashCode() {
			return Objects.hash(annotations, types);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Descriptor other = (Descriptor) obj;
			return Objects.equals(annotations, other.annotations) && Objects.equals(types, other.types);
		}

	}

	public CacheExtension() {
		super();
		cacheBeans = new HashSet<>();
	}

	private void configure(@Observes @Priority(PLATFORM_BEFORE) Config config) {
		this.config = config;
	}

	/*
	 * Collect all injection points qualifiers for Microstream Cache
	 */
	private <T extends javax.cache.Cache<?, ?>> void processInjectionPoint(	@Observes final ProcessInjectionPoint<?, T> event) {
		if (event != null) {
			final InjectionPoint injectionPoint = event.getInjectionPoint();
			if (injectionPoint != null) {
				if(injectionPoint.getAnnotated().isAnnotationPresent(MicrostreamCache.class))
				{
					this.cacheBeans.add(
							new Descriptor(injectionPoint.getQualifiers(), (ParameterizedType) injectionPoint.getType()));
				}
			}
		}
	}

	/*
	 * create EmbeddedStorageManager beans
	 */
	private void addBeans(@Observes final AfterBeanDiscovery event, final BeanManager beanManager) {
		if (event != null && beanManager != null) {
			if (!this.cacheBeans.isEmpty()) {
				for (final Descriptor entry : this.cacheBeans) {
					assert entry != null;
					// create Microstream Cache bean
					final Set<Annotation> qualifiers = entry.getAnnotations();
					assert qualifiers != null;
					assert !qualifiers.isEmpty();

					ParameterizedType types = entry.getTypes();
					GenericType<?> keyType = GenericType.create(types.getActualTypeArguments()[0]);
					GenericType<?> valueType = GenericType.create(types.getActualTypeArguments()[1]);
					String name = getName(qualifiers);

					event.<Cache<?, ?>>addBean()
					.qualifiers(qualifiers)
					.scope(ApplicationScoped.class)
					.addTransitiveTypeClosure(Cache.class)
					.addTypes(types)
					.createWith(cc -> {
						return CacheBuilder.create(name, getConfigNode(qualifiers), keyType.rawType(),
								valueType.rawType());
					})
					.destroyWith((cache, context) -> cache.close());
				}
			}
		}
	}

	/*
	 * Get the config node that matches the name supplied by @MicrostreamStorage
	 * annotation if no name is available the full helidon config is returned
	 */
	private Config getConfigNode(Set<Annotation> qualifiers) {
		Optional<Annotation> optAnnotation = qualifiers.stream().filter(e -> e instanceof MicrostreamCache).findFirst();
		if (optAnnotation.isPresent()) {
			MicrostreamCache annotation = (MicrostreamCache) optAnnotation.get();
			String name = annotation.configNode();
			return config.get(name);
		}
		return null;
	}

	/*
	 * Get the name supplied by @MicrostreamStorage
	 * annotation if no name is available null is returned
	 */
	private String getName(Set<Annotation> qualifiers) {
		Optional<Annotation> optAnnotation = qualifiers.stream().filter(e -> e instanceof MicrostreamCache).findFirst();
		if (optAnnotation.isPresent()) {
			MicrostreamCache annotation = (MicrostreamCache) optAnnotation.get();
			String name = annotation.name();
			return name;
		}
		return null;
	}
}
