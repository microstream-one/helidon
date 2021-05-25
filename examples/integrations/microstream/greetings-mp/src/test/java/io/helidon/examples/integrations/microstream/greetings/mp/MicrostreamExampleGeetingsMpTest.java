package io.helidon.examples.integrations.microstream.greetings.mp;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;

import javax.inject.Inject;
import javax.json.JsonObject;
import javax.ws.rs.client.WebTarget;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.helidon.microprofile.tests.junit5.HelidonTest;

@HelidonTest
public class MicrostreamExampleGeetingsMpTest {

    @Inject
    private WebTarget webTarget;

    @TempDir
    static Path tempDir;

    @BeforeAll
    private static void beforeAll() {
        System.setProperty("one.microstream.storage.greetings.storage-directory", tempDir.toString());
    }

    @Test
    public void testGreeting() {
        JsonObject response = webTarget.path("/greet").request().get(JsonObject.class);

        assertEquals("Hello World!", response.getString("message"), "response should be 'Hello World' ");
    }

}
