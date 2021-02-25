package io.helidon.integrations.microstream.health;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.eclipse.microprofile.health.HealthCheckResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import io.helidon.integrations.microstream.health.MicrostreamHealthCheck;
import one.microstream.storage.types.EmbeddedStorageManager;

public class MicrostreamHealthTest {
	private EmbeddedStorageManager embeddedStorageManager;

	@BeforeEach
	void init() throws IOException {
		embeddedStorageManager = Mockito.mock(EmbeddedStorageManager.class);
	}

	private void setMicrostreamStatus(boolean isRunning) {
		Mockito.when(embeddedStorageManager.isRunning()).thenReturn(isRunning);
	}

	@Test
	void testStatusRunning() {
		setMicrostreamStatus(true);
		MicrostreamHealthCheck check = MicrostreamHealthCheck.create(embeddedStorageManager);
		HealthCheckResponse response = check.call();
		assertThat(response.getState(), is(HealthCheckResponse.State.UP));
	}

	@Test
	void testStatusNotRunning() {
		setMicrostreamStatus(false);
		MicrostreamHealthCheck check = MicrostreamHealthCheck.create(embeddedStorageManager);
		HealthCheckResponse response = check.call();
		assertThat(response.getState(), is(HealthCheckResponse.State.DOWN));
	}

	@Test
	void testStatusTimeout() {

		Mockito.when(embeddedStorageManager.isRunning()).then((x) -> {
			Thread.sleep(100);
			return true;
		});

		MicrostreamHealthCheck check = MicrostreamHealthCheck.builder(embeddedStorageManager)
			.timeout(20, TimeUnit.MILLISECONDS).build();

		HealthCheckResponse response = check.call();
		assertThat(response.getState(), is(HealthCheckResponse.State.DOWN));
	}
}
