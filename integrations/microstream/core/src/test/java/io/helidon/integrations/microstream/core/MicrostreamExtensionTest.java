package io.helidon.integrations.microstream.core;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.nio.file.Path;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.helidon.microprofile.tests.junit5.HelidonTest;
import one.microstream.storage.types.EmbeddedStorageManager;

@HelidonTest
public class MicrostreamExtensionTest {

	@TempDir
	static Path tempDir;

	@Inject
	@MicrostreamStorage(configNode = "one.microstream.storage.my_storage")
	EmbeddedStorageManager storage;

	@BeforeAll
	private static void beforeAll() {
		System.setProperty("one.microstream.storage.my_storage.storage-directory", tempDir.toString());
	}

	@AfterAll
	private static void afterAll() {
		System.clearProperty("one.microstream.storage.my_storage.storage-directory");
	}

	@Test
	public void testInjectedInstances() {
		System.out.println("storage 1: " + storage);
		assertThat(storage, notNullValue());
		assertThat(storage.isRunning(), equalTo(true));
	}

}