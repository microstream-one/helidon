package io.helidon.microstream.health;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.HealthCheckResponseBuilder;

import one.microstream.storage.types.EmbeddedStorageManager;

/**
 * Microstream health check.
 */
public class MicrostreamHealthCheck implements HealthCheck {

	private static final String DEFAULT_NAME = "Microstream";
	private static final long DEFAULT_TIMEOUT_SECONDS = 10;

	private EmbeddedStorageManager embeddedStorageManager;
	private long timeoutDuration;
	private TimeUnit timeoutUnit;
	private String name;

	private MicrostreamHealthCheck() {
		super();
	}

	private MicrostreamHealthCheck(Builder builder) {
		this.embeddedStorageManager = builder.embeddedStorageManager();
		this.timeoutDuration = builder.timeoutDuration();
		this.timeoutUnit = builder.timeoutUnit();
		this.name = builder.name();
	}

	@Override
	public HealthCheckResponse call() {
		HealthCheckResponseBuilder builder = HealthCheckResponse.builder().name(name);

		try {
			CompletableFuture<Boolean> status = CompletableFuture.supplyAsync(() -> embeddedStorageManager.isRunning())
				.orTimeout(timeoutDuration, timeoutUnit);

			if (status.get() == true) {
				builder.up();
			}
			else {
				builder.down();
			}
		} catch (Throwable e) {
			builder.down();
			builder.withData("ErrorMessage", e.getMessage());
			builder.withData("ErrorClass", e.getClass().getName());
		}

		return builder.build();
	}

	/**
	 * Create a default health check for Microstream
	 * 
	 * @param embeddedStorageManager
	 * @return default health check for Microstream
	 */
	public static MicrostreamHealthCheck create(EmbeddedStorageManager embeddedStorageManager) {
		return builder(embeddedStorageManager).build();
	}

    /**
     * A fluent API builder to create a health check for Microstream.
     *
     * @param embeddedStorageManager EmbeddedStorageManager
     * @return a new builder
     */
	public static Builder builder(EmbeddedStorageManager embeddedStorageManager) {
		return new Builder(embeddedStorageManager);
	}

	public static class Builder implements io.helidon.common.Builder<MicrostreamHealthCheck> {
		
		private EmbeddedStorageManager embeddedStorageManager;
		private long timeoutDuration;
		private TimeUnit timeoutUnit;
		private String name;

		private Builder(EmbeddedStorageManager embeddedStorageManager) {
			this.embeddedStorageManager = embeddedStorageManager;
			this.name = DEFAULT_NAME;
			this.timeoutDuration = DEFAULT_TIMEOUT_SECONDS;
			this.timeoutUnit = TimeUnit.SECONDS;
		}

		@Override
		public MicrostreamHealthCheck build() {
			return new MicrostreamHealthCheck(this);
		}

		/**
		 * Customized name of the health check. Default uses
		 * {@link one.microstream.storage.types.StorageManager#databaseName()}databaseName()
		 *
		 * @param name name of the health check
		 * @return updated builder instance
		 */
		public Builder name(String name) {
			this.name = name;
			return this;
		}

		/**
		 * Set custom timeout to wait for statement execution response. Default value is
		 * 10 seconds.
		 *
		 * @param duration the maximum time to wait for statement execution response
		 * @param timeUnit the time unit of the timeout argument
		 * @return updated builder instance
		 */
		public Builder timeout(long duration, TimeUnit timeUnit) {
			this.timeoutDuration = duration;
			this.timeoutUnit = timeUnit;
			return this;
		}

		public EmbeddedStorageManager embeddedStorageManager() {
			return embeddedStorageManager;
		}

		public long timeoutDuration() {
			return timeoutDuration;
		}

		public TimeUnit timeoutUnit() {
			return timeoutUnit;
		}

		public String name() {
			return name;
		}
	}
}
