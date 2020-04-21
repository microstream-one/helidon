/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.helidon.tests.bookstore;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParser;

import com.oracle.bedrock.runtime.Application;
import com.oracle.bedrock.runtime.LocalPlatform;
import com.oracle.bedrock.runtime.console.CapturingApplicationConsole;
import com.oracle.bedrock.runtime.options.Arguments;
import com.oracle.bedrock.runtime.options.Console;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class MainTest {

    private static String appJarPathSE = System.getProperty("app.jar.path.se", "please-set-app.jar.path.se");
    private static String appJarPathMP = System.getProperty("app.jar.path.mp", "please-set-app.jar.path.mp");

    private static Application application;
    private static LocalPlatform localPlatform = LocalPlatform.get();
    private static int port = localPlatform.getAvailablePorts().next();
    private static URL healthUrl;

    private static final String MODULE_NAME_MP = "io.helidon.tests.apps.bookstore.mp";
    private static final String MODULE_NAME_SE = "io.helidon.tests.apps.bookstore.se";

    @BeforeAll
    static void setup() throws Exception {
        System.out.println("Using port number" + port);
        healthUrl = new URL("http://localhost:" + port + "/health");
        appJarPathSE = Paths.get(appJarPathSE).normalize().toString();
        appJarPathMP = Paths.get(appJarPathMP).normalize().toString();
    }

    @AfterEach
    void stopTheApplication() throws Exception {
        if (application != null) {
            application.close();
        }
        application = null;
        waitForApplicationDown();
    }

    /**
     * Start the application using the application jar and classpath
     *
     * @param appJarPath    Path to jar file with application
     * @param javaArgs      Additional java arguments to pass
     * @throws Exception
     */
    void startTheApplication(String appJarPath, List<String> javaArgs) throws Exception {
        Arguments args = toArguments(appJarPath, javaArgs, null);
        application = localPlatform.launch("java", args);
        waitForApplicationUp();
    }

    /**
     * Start the application using the application jar and module path
     *
     * @param appJarPath    Path to jar file with application
     * @param javaArgs      Additional java arguments to pass
     * @param moduleName    Name of application's module that contains Main
     * @throws Exception
     */
    void startTheApplicationModule(String appJarPath, List<String> javaArgs, String moduleName) throws Exception {
        Arguments args = toArguments(appJarPath, javaArgs, moduleName);
        application = localPlatform.launch("java", args);
        waitForApplicationUp();
    }

    @Test
    void exitOnStartedSe() throws Exception {
        runExitOnStartedTest("se");
    }

    @Test
    void exitOnStartedMp() throws Exception {
        runExitOnStartedTest("mp");
    }

    private void runExitOnStartedTest(String edition) throws Exception {
        Arguments args = toArguments(editionToJarPath(edition), List.of("-Dexit.on.started=!"), null);
        CapturingApplicationConsole console = new CapturingApplicationConsole();
        application = localPlatform.launch("java", args, Console.of(console));
        Queue<String> stdOut = console.getCapturedOutputLines();
        long maxTime = System.currentTimeMillis() + (10 * 1000);
        do {
            if (stdOut.stream().anyMatch(line -> line.contains("exit.on.started"))) {
                return;
            }
            Thread.sleep(500);
        } while (System.currentTimeMillis() < maxTime);

        String eol = System.getProperty("line.separator");
        Assertions.fail("quickstart " + edition + " did not exit as expected." + eol
                        + eol
                        + "stdOut: " + stdOut + eol
                        + eol
                        + " stdErr: " + console.getCapturedErrorLines());
    }

    @Test
    void basicTestJsonP() throws Exception {
        runJsonFunctionalTest("se", "jsonp");
    }

    @Test
    void basicTestJsonB() throws Exception {
        runJsonFunctionalTest("se", "jsonb");
    }

    @Test
    void basicTestJackson() throws Exception {
        runJsonFunctionalTest("se", "jackson");
    }

    @Test
    void basicTestJsonMP() throws Exception {
        runJsonFunctionalTest("mp", "");
    }

    @Test
    void basicTestMetricsHealthSE() throws Exception {
        runMetricsAndHealthTest("se", "jsonp", false);
    }

    @Test
    void basicTestMetricsHealthMP() throws Exception {
        runMetricsAndHealthTest("mp", "", false);
    }

    @Test
    void basicTestMetricsHealthJsonB() throws Exception {
        runMetricsAndHealthTest("se", "jsonb", false);
    }

    @Test
    void basicTestMetricsHealthJackson() throws Exception {
        runMetricsAndHealthTest("se", "jackson", false);
    }

    @Test
    void basicTestMetricsHealthSEModules() throws Exception {
        runMetricsAndHealthTest("se", "jsonp", true);
    }

    @Test
    void basicTestMetricsHealthMPModules() throws Exception {
        runMetricsAndHealthTest("mp", "", true);
    }

    /**
     * Run some basic CRUD operations on the server. The server supports
     * running with any of our three JSON libraries: jsonp, jsonb, jackson.
     * So we set a system property to select the library to use before starting
     * the server
     *
     * @param edition "mp", "se"
     * @param jsonLibrary "jsonp", "jsonb" or "jackson"
     * @throws Exception on test failure
     */
    private void runJsonFunctionalTest(String edition, String jsonLibrary) throws Exception {
        HttpURLConnection conn;
        String json = getBookAsJson();
        int numberOfBooks = 1000;
        List<String> systemPropertyArgs = new LinkedList<>();

        systemPropertyArgs.add("-Dbookstore.size=" + numberOfBooks);
        if (jsonLibrary != null && !jsonLibrary.isEmpty()) {
            systemPropertyArgs.add("-Dapp.json-library=" + jsonLibrary);
        }

        startTheApplication(editionToJarPath(edition), systemPropertyArgs);

        conn = getURLConnection("GET", "/books");
        assertThat("HTTP response GET books", conn.getResponseCode(), is(200));

        JsonParser parser = Json.createParser(conn.getInputStream());
        parser.next();
        JsonArray bookArray = parser.getArray();
        assertThat("Number of books", bookArray.size(), is(numberOfBooks));

        conn = getURLConnection("POST", "/books");
        writeJsonContent(conn, json);
        assertThat("HTTP response POST", conn.getResponseCode(), is(200));

        conn = getURLConnection("GET", "/books/123456");
        assertThat("HTTP response GET good ISBN", conn.getResponseCode(), is(200));
        JsonReader jsonReader = Json.createReader(conn.getInputStream());
        JsonObject jsonObject = jsonReader.readObject();
        assertThat("Checking if correct ISBN", jsonObject.getString("isbn"), is("123456"));

        conn = getURLConnection("GET", "/books/0000");
        assertThat("HTTP response GET bad ISBN", conn.getResponseCode(), is(404));

        conn = getURLConnection("GET", "/books");
        assertThat("HTTP response list books", conn.getResponseCode(), is(200));

        conn = getURLConnection("DELETE", "/books/123456");
        assertThat("HTTP response delete book", conn.getResponseCode(), is(200));
    }

    /**
     * Run some basic metrics and health operations on the server. The server supports
     * running with any of our three JSON libraries: jsonp, jsonb, jackson.
     * So we set a system property to select the library to use before starting
     * the server
     *
     * @param edition "mp", "se"
     * @param jsonLibrary "jsonp", "jsonb" or "jackson"
     * @param useModules true to use modulepath, false to use classpath
     * @throws Exception on test failure
     */
    private void runMetricsAndHealthTest(String edition, String jsonLibrary, boolean useModules) throws Exception {

        List<String> systemPropertyArgs = new LinkedList<>();
        if (jsonLibrary != null && !jsonLibrary.isEmpty()) {
            systemPropertyArgs.add("-Dapp.json-library=" + jsonLibrary);
        }

        if (useModules) {
            startTheApplicationModule(editionToJarPath(edition), systemPropertyArgs, editionToModuleName(edition));
        } else {
            startTheApplication(editionToJarPath(edition), systemPropertyArgs);
        }

        HttpURLConnection conn;

        // Get Prometheus style metrics
        conn = getURLConnection("GET", "/metrics");
        conn.setRequestProperty("Accept", "*/*");
        assertThat("Checking Prometheus Metrics response\"", conn.getResponseCode(), is(200));
        String s = readAllAsString(conn.getInputStream());

        // Make sure we got prometheus metrics
        assertThat("Making sure we got Prometheus format", s.startsWith("# TYPE"));

        // Get JSON encoded metrics
        conn = getURLConnection("GET", "/metrics");
        assertThat("Checking JSON Metrics response\"", conn.getResponseCode(), is(200));

        // Makes sure we got JSON metrics
        JsonReader jsonReader = Json.createReader(conn.getInputStream());
        JsonObject jsonObject = jsonReader.readObject();
        assertThat("Checking request count",
                   jsonObject.getJsonObject("vendor").getInt("requests.count") > 0);

        // Get JSON encoded metrics/base
        conn = getURLConnection("GET", "/metrics/base");
        assertThat("Checking JSON Base Metrics response", conn.getResponseCode(), is(200));

        // Makes sure we got JSON metrics
        jsonReader = Json.createReader(conn.getInputStream());
        jsonObject = jsonReader.readObject();
        assertThat("Checking thread count", jsonObject.getInt("thread.count") > 0);

        // Get JSON encoded health check
        conn = getURLConnection("GET", "/health");
        assertThat("Checking health response", conn.getResponseCode(), is(200));

        jsonReader = Json.createReader(conn.getInputStream());
        jsonObject = jsonReader.readObject();
        assertThat("Checking health outcome", jsonObject.getString("outcome"), is("UP"));
        assertThat("Checking health status", jsonObject.getString("status"), is("UP"));

        // Verify that built-in health checks are disabled in MP according to
        // 'microprofile-config.properties' setting in bookstore application
        if (edition.equals("mp")) {
            assertThat("Checking built-in health checks disabled",
                       jsonObject.getJsonArray("checks").size(), is(0));
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"se", "mp"})
    void routing(String edition) throws Exception {

        startTheApplication(editionToJarPath(edition), Collections.emptyList());
        HttpURLConnection conn;

        conn = getURLConnection("GET", "/boo%6bs");

        if ("se".equals(edition)) {
            assertThat("Checking encode URL response SE", conn.getResponseCode(), is(200));
        } else {
            // JAXRS does not decode URLs before matching
            assertThat("Checking encode URL response MP", conn.getResponseCode(), is(404));
        }

        conn = getURLConnection("GET", "/badurl");
        assertThat("Checking encode URL response", conn.getResponseCode(), is(404));
    }

    private HttpURLConnection getURLConnection(String method, String path) throws Exception {
        URL url = new URL("http://localhost:" + port + path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Accept", "application/json");
        System.out.println("Connecting: " + method + " " + url);
        return conn;
    }

    private String getBookAsJson() throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream("book.json");
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } else {
            throw new IOException("Could not find resource book.json");
        }
    }

    private String readAllAsString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        return reader.lines().collect(Collectors.joining(System.lineSeparator()));
    }

    private void writeJsonContent(HttpURLConnection conn, String json) throws IOException {
        int jsonLength = json.getBytes().length;

        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Content-Length", Integer.toString(jsonLength));
        conn.setFixedLengthStreamingMode(jsonLength);
        OutputStream outputStream = conn.getOutputStream();
        outputStream.write(json.getBytes());
        outputStream.close();
    }

    private void waitForApplicationUp() throws Exception {
        waitForApplication(healthUrl, true);
    }

    private void waitForApplicationDown() throws Exception {
        waitForApplication(healthUrl, false);
    }

    /**
     * Wait for the application to be up or down.
     *
     * @param url URL to ping. URL should return 200 when application is up.
     * @param toBeUp true if waiting to come up, false if waiting to go down
     * @throws Exception on a failure
     */
    private void waitForApplication(URL url, boolean toBeUp) throws Exception {
        long timeout = 10 * 1000; // 10 seconds should be enough to start/stop the server
        long now = System.currentTimeMillis();
        String operation = (toBeUp ? "start" : "stop");

        HttpURLConnection conn;
        int responseCode;
        do {
            System.out.println("Waiting for application to " + operation);
            if ((System.currentTimeMillis() - now) > timeout) {
                Assertions.fail("Application failed to " + operation);
            }
            Thread.sleep(500);
            try {
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(100);
                responseCode = conn.getResponseCode();
            } catch (Exception ex) {
                responseCode = -1;
            }
        } while ((toBeUp && responseCode != 200) || (!toBeUp && responseCode != -1));
    }

    private static Arguments toArguments(String appJarPath, List<String> javaArgs, String moduleName) {
        if (application != null) {
            Assertions.fail("Can't start the application, it is already running");
        }

        List<String> startArgs = new ArrayList<>(javaArgs);
        startArgs.add("-Dserver.port=" + port);

        if (moduleName != null && ! moduleName.isEmpty() ) {
            File jarFile = new File(appJarPath);
            // --module-path target/bookstore-se.jar:target/libs -m io.helidon.tests.apps.bookstore.se/io.helidon.tests.apps.bookstore.se.Main
            startArgs.add("--module-path");
            startArgs.add(appJarPath + ":" + jarFile.getParent() + "/libs");
            startArgs.add("-m");
            startArgs.add(moduleName + "/" + moduleName + ".Main");
        } else {
            startArgs.add("-jar");
            startArgs.add(appJarPath);
        }
        return Arguments.of(startArgs);
    }

    private static String editionToJarPath(String edition) {
        if ("se".equals(edition)) {
            return appJarPathSE;
        } else if ("mp".equals(edition)) {
            return appJarPathMP;
        } else {
            throw new IllegalArgumentException("Invalid edition '" + edition + "'. Must be 'se' or 'mp'");
        }
    }

    private static String editionToModuleName(String edition) {
        if ("se".equals(edition)) {
            return MODULE_NAME_SE;
        } else if ("mp".equals(edition)) {
            return MODULE_NAME_MP;
        } else {
            throw new IllegalArgumentException("Invalid edition '" + edition + "'. Must be 'se' or 'mp'");
        }
    }
}