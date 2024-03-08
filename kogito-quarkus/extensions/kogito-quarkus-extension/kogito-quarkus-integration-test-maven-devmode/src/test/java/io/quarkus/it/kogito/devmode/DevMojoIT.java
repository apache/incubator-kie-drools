/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.quarkus.it.kogito.devmode;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.maven.it.RunAndCheckMojoTestBase;
import io.quarkus.maven.it.verifier.MavenProcessInvoker;
import io.quarkus.maven.it.verifier.RunningInvoker;
import io.quarkus.test.devmode.util.DevModeTestUtils;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import net.jcip.annotations.NotThreadSafe;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;

/*
 *  Test inspired by https://github.com/quarkusio/quarkus/blob/c8919cfb8abbc3df49dd1febd74b998417b0367e/integration-tests/maven/src/test/java/io/quarkus/maven/it/DevMojoIT.java#L218
 */
//@DisableForNative: it is not yet available as of 1.11, and I doubt is ever needed for this module
@NotThreadSafe
public class DevMojoIT extends RunAndCheckMojoTestBase {

    private static final String PROPERTY_MAVEN_REPO_LOCAL = "maven.repo.local";
    private static final String PROPERTY_MAVEN_SETTINGS = "maven.settings";
    private static final String MAVEN_REPO_LOCAL = System.getProperty(PROPERTY_MAVEN_REPO_LOCAL);
    private static final String MAVEN_SETTINGS = System.getProperty(PROPERTY_MAVEN_SETTINGS);

    private static final long INIT_POLL_DELAY = 3;
    private static final TimeUnit INIT_POLL_DELAY_UNIT = TimeUnit.SECONDS;
    private static final long INIT_POLL_TIMEOUT = 2;
    private static final TimeUnit INIT_POLL_TIMEOUT_UNIT = TimeUnit.MINUTES;
    private static final long RELOAD_POLL_DELAY = INIT_POLL_DELAY;
    private static final TimeUnit RELOAD_POLL_DELAY_UNIT = INIT_POLL_DELAY_UNIT;
    private static final long RELOAD_POLL_TIMEOUT = 3;
    private static final TimeUnit RELOAD_POLL_TIMEOUT_UNIT = TimeUnit.MINUTES;
    private static final String QUARKUS_RANDOM_HTTP_PORT_PATTERN = "(?<=localhost:)[0-9]+";
    private static final String QUARKUS_APP_NAME_PROP_HANDLER = "-Dquarkus.application.name=";

    private final Pattern quarkusRandomHttpPortPattern = Pattern.compile(QUARKUS_RANDOM_HTTP_PORT_PATTERN);
    private final Map<String, String> randomHttpPorts = new HashMap<>();

    private final Logger LOGGER = LoggerFactory.getLogger(DevMojoIT.class);

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private String getRestResponse(String port) {
        AtomicReference<String> resp = new AtomicReference<>();
        // retry on exceptions for connection refused, connection errors, etc. which will occur until the Kogito Quarkus maven project is fully built and running
        await().pollDelay(INIT_POLL_DELAY, INIT_POLL_DELAY_UNIT)
                .atMost(INIT_POLL_TIMEOUT, INIT_POLL_TIMEOUT_UNIT).until(() -> {
                    try {
                        String content = DevModeTestUtils.get("http://localhost:" + port + "/control");
                        resp.set(content);
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                });
        return resp.get();
    }

    /* copy-paste from quarkus */
    @Override
    protected void run(boolean performCompile, String... options) throws MavenInvocationException {
        assertThat(testDir).isDirectory();
        running = new RunningInvoker(testDir, false);
        String applicationName = "";
        final List<String> args = new ArrayList<>(4 + options.length);
        if (performCompile) {
            args.add("compile");
        }
        args.add("quarkus:dev");
        boolean hasDebugOptions = false;
        for (String option : options) {
            args.add(option);
            if (option.trim().startsWith("-Ddebug=") || option.trim().startsWith("-Dsuspend=")) {
                hasDebugOptions = true;
            }
            // We have to respect the run method interface, that's why we are using options instead of a proper parameter
            // or even better, returning the actual port directly without a collector
            if (option.trim().startsWith(QUARKUS_APP_NAME_PROP_HANDLER)) {
                applicationName = option.split("=")[1];
            }
        }
        if (!hasDebugOptions) {
            // if no explicit debug options have been specified, let's just disable debugging
            args.add("-Ddebug=false");
        }

        // Since the Kogito extension split, this requires more memory, going for a default of 1GB, per surefire.
        args.add("-Djvm.args=-Xmx1024m");
        // Disable devservices
        args.add("-Dquarkus.kogito.devservices.enabled=false");
        // Let Quarkus figure a random port
        args.add("-Dquarkus.http.port=0");
        args.addAll(getProvidedMavenProperties());

        running.execute(args, Collections.emptyMap());

        final File logFile = ensureLogFile();
        AtomicLong filePointer = new AtomicLong();
        String finalApplicationName = applicationName;

        await().pollDelay(INIT_POLL_DELAY, INIT_POLL_DELAY_UNIT)
                .atMost(INIT_POLL_TIMEOUT, INIT_POLL_TIMEOUT_UNIT)
                .until(() -> {
                    filePointer.set(seekRandomHttpPort(logFile, finalApplicationName, filePointer.get()));
                    return randomHttpPorts.containsKey(finalApplicationName);
                });
    }

    private String run(String appName) throws MavenInvocationException {
        LOGGER.info("Running test for {}", appName);
        run(true, QUARKUS_APP_NAME_PROP_HANDLER + appName);
        return randomHttpPorts.get(appName);
    }

    /**
     * Randomly access the log information and tries to match the port listened by the maven process
     * Since the file is frequently written by another process, this method can be called inside await method.
     *
     * @return the updated file pointer (last byte read in the file), so callers can call this method again and continue seek for the required port
     * @see MavenProcessInvoker
     */
    private long seekRandomHttpPort(File logFile, String appName, long filePointer) {
        long len = logFile.length();
        if (len < filePointer) {
            filePointer = len;
        } else if (len > filePointer) {
            try (RandomAccessFile raf = new RandomAccessFile(logFile, "r")) {
                raf.seek(filePointer);
                String line;
                while ((line = raf.readLine()) != null) {
                    Matcher matcher = quarkusRandomHttpPortPattern.matcher(line);
                    if (matcher.find()) {
                        randomHttpPorts.computeIfAbsent(appName, k -> matcher.group());
                        LOGGER.info("App {} has port {}", appName, randomHttpPorts.get(appName));
                        return filePointer;
                    }
                }
                filePointer = raf.getFilePointer();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return filePointer;
    }

    private File ensureLogFile() {
        final File workingDirName = running.getWorkingDirectory();
        if (workingDirName.isDirectory()) {
            final File[] files = workingDirName.listFiles((dir, name) -> name.equals("build-" + dir.getName() + ".log"));
            if (files != null && files.length > 0) {
                return files[0];
            }
        }
        throw new IllegalStateException("Couldn't find log file for project in the directory: " + workingDirName);
    }

    private List<String> getProvidedMavenProperties() {
        List<String> additionalArguments = new ArrayList<>();
        if (MAVEN_REPO_LOCAL != null) {
            additionalArguments.add(String.format("-D%s=%s", PROPERTY_MAVEN_REPO_LOCAL, MAVEN_REPO_LOCAL));
        }
        if (MAVEN_SETTINGS != null) {
            /*
             * Invoker would fail if the received settings.xml file did not exist.
             * That can happen when ${session.request.userSettingsFile.path} is passed as value for maven.settings
             * property from the pom.xml and at the same time user does not have settings.xml in ~/.m2/ nor they provided
             * specific settings.xml using -s argument.
             */
            if (new File(MAVEN_SETTINGS).exists()) {
                additionalArguments.add(String.format("-s %s", MAVEN_SETTINGS));
            }
        }
        return additionalArguments;
    }

    @Test
    public void testBPMN2HotReload() throws Exception {
        testDir = initProject("projects/classic-inst", "projects/project-intrumentation-reload-bpmn");
        String httpPort = run("testBPMN2HotReload");
        assertThat(httpPort).isNotEmpty();

        final File controlSource = new File(testDir, "src/main/java/control/RestControl.java");

        // await Quarkus
        await().pollDelay(INIT_POLL_DELAY, INIT_POLL_DELAY_UNIT)
                .atMost(INIT_POLL_TIMEOUT, INIT_POLL_TIMEOUT_UNIT).until(() -> getRestResponse(httpPort).contains("Hello, v1"));

        LOGGER.info("[testBPMN2HotReload] Starting bpmn process");
        given().baseUri("http://localhost:" + httpPort)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\n" +
                        "    \"s1\": \"v1\"," +
                        "    \"s2\": \"v2\"" +
                        "}")
                .contentType(ContentType.JSON)
                .when()
                .post("/" + "simple")
                .then()
                .statusCode(201)
                .body("s2", is("Hello, v1"));

        // --- Change #1
        LOGGER.info("[testBPMN2HotReload] Beginning Change #1");
        File source = new File(testDir, "src/main/resources/simple.bpmn2");
        filter(source, Collections.singletonMap("\"Hello, \"+", "\"Ciao, \"+"));
        filter(controlSource, Collections.singletonMap("\"Hello, \"+", "\"Ciao, \"+"));
        await().pollDelay(RELOAD_POLL_DELAY, RELOAD_POLL_DELAY_UNIT)
                .atMost(RELOAD_POLL_TIMEOUT, RELOAD_POLL_TIMEOUT_UNIT).until(() -> getRestResponse(httpPort).contains("Ciao, v1"));

        LOGGER.info("[testBPMN2HotReload] Starting bpmn process");
        given().baseUri("http://localhost:" + httpPort)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\n" +
                        "    \"s1\": \"v1\"," +
                        "    \"s2\": \"v2\"" +
                        "}")
                .contentType(ContentType.JSON)
                .when()
                .post("/" + "simple")
                .then()
                .statusCode(201)
                .body("s2", is("Ciao, v1"));

        // --- Change #2
        LOGGER.info("[testBPMN2HotReload] Beginning Change #2");
        filter(source, Collections.singletonMap("\"Ciao, \"+", "\"Bonjour, \"+"));
        filter(controlSource, Collections.singletonMap("\"Ciao, \"+", "\"Bonjour, \"+"));
        await().pollDelay(RELOAD_POLL_DELAY, RELOAD_POLL_DELAY_UNIT)
                .atMost(RELOAD_POLL_TIMEOUT, RELOAD_POLL_TIMEOUT_UNIT).until(() -> getRestResponse(httpPort).contains("Bonjour, v1"));

        LOGGER.info("[testBPMN2HotReload] Starting bpmn process");
        given().baseUri("http://localhost:" + httpPort)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\n" +
                        "    \"s1\": \"v1\"," +
                        "    \"s2\": \"v2\"" +
                        "}")
                .contentType(ContentType.JSON)
                .when()
                .post("/" + "simple")
                .then()
                .statusCode(201)
                .body("s2", is("Bonjour, v1"));

        LOGGER.info("[testBPMN2HotReload] done.");
    }

    @Test
    public void testDMNHotReload() throws Exception {
        testDir = initProject("projects/classic-inst", "projects/project-intrumentation-reload-dmn");
        final String httpPort = run("testDMNHotReload");

        final File controlSource = new File(testDir, "src/main/java/control/RestControl.java");

        // await Quarkus
        await().pollDelay(INIT_POLL_DELAY, INIT_POLL_DELAY_UNIT)
                .atMost(INIT_POLL_TIMEOUT, INIT_POLL_TIMEOUT_UNIT).until(() -> getRestResponse(httpPort).contains("Hello, v1"));

        LOGGER.info("[testDMNHotReload] Evaluate DMN");
        given().baseUri("http://localhost:" + httpPort)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\n" +
                        "    \"name\": \"v1\"   " +
                        "}")
                .contentType(ContentType.JSON)
                .when()
                .post("/" + "hello")
                .then()
                .statusCode(200)
                .body("greeting", is("Hello, v1"));

        // --- Change #1
        LOGGER.info("[testDMNHotReload] Beginning Change #1");
        File source = new File(testDir, "src/main/resources/hello.dmn");
        filter(source, Collections.singletonMap("\"Hello, \"+", "\"Ciao, \"+"));
        filter(controlSource, Collections.singletonMap("\"Hello, \"+", "\"Ciao, \"+"));
        await().pollDelay(RELOAD_POLL_DELAY, RELOAD_POLL_DELAY_UNIT)
                .atMost(RELOAD_POLL_TIMEOUT, RELOAD_POLL_TIMEOUT_UNIT).until(() -> getRestResponse(httpPort).contains("Ciao, v1"));

        LOGGER.info("[testDMNHotReload] Evaluate DMN");
        given().baseUri("http://localhost:" + httpPort)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\n" +
                        "    \"name\": \"v1\"   " +
                        "}")
                .contentType(ContentType.JSON)
                .when()
                .post("/" + "hello")
                .then()
                .statusCode(200)
                .body("greeting", is("Ciao, v1"));

        // --- Change #2
        LOGGER.info("[testDMNHotReload] Beginning Change #2");
        filter(source, Collections.singletonMap("\"Ciao, \"+", "\"Bonjour, \"+"));
        filter(controlSource, Collections.singletonMap("\"Ciao, \"+", "\"Bonjour, \"+"));
        await().pollDelay(RELOAD_POLL_DELAY, RELOAD_POLL_DELAY_UNIT)
                .atMost(RELOAD_POLL_TIMEOUT, RELOAD_POLL_TIMEOUT_UNIT).until(() -> getRestResponse(httpPort).contains("Bonjour, v1"));

        LOGGER.info("[testDMNHotReload] Evaluate DMN");
        given().baseUri("http://localhost:" + httpPort)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\n" +
                        "    \"name\": \"v1\"   " +
                        "}")
                .contentType(ContentType.JSON)
                .when()
                .post("/" + "hello")
                .then()
                .statusCode(200)
                .body("greeting", is("Bonjour, v1"));

        LOGGER.info("[testDMNHotReload] done.");
    }

    @Test
    public void testDRLHotReload() throws Exception {
        testDir = initProject("projects/classic-inst", "projects/project-intrumentation-reload-drl");
        final String httpPort = run("testDRLHotReload");

        final File controlSource = new File(testDir, "src/main/java/control/RestControl.java");

        // await Quarkus
        await().pollDelay(INIT_POLL_DELAY, INIT_POLL_DELAY_UNIT)
                .atMost(INIT_POLL_TIMEOUT, INIT_POLL_TIMEOUT_UNIT).until(() -> getRestResponse(httpPort).contains("Hello, v1"));

        LOGGER.info("[testDMNHotReload] Evaluate DRL");
        given().baseUri("http://localhost:" + httpPort)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\n" +
                        "    \"strings\": [\"v1\"]  " +
                        "}")
                .contentType(ContentType.JSON)
                .when()
                .post("/" + "q1")
                .then()
                .statusCode(200)
                .body(containsString("Hello, v1"));

        // --- Change #1
        LOGGER.info("[testDMNHotReload] Beginning Change #1");
        File source = new File(testDir, "src/main/resources/acme/rules.drl");
        filter(source, Collections.singletonMap("\"Hello, \"+", "\"Ciao, \"+"));
        filter(controlSource, Collections.singletonMap("\"Hello, \"+", "\"Ciao, \"+"));
        await().pollDelay(RELOAD_POLL_DELAY, RELOAD_POLL_DELAY_UNIT)
                .atMost(RELOAD_POLL_TIMEOUT, RELOAD_POLL_TIMEOUT_UNIT).until(() -> getRestResponse(httpPort).contains("Ciao, v1"));

        LOGGER.info("[testDMNHotReload] Evaluate DRL");
        given().baseUri("http://localhost:" + httpPort)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\n" +
                        "    \"strings\": [\"v1\"]  " +
                        "}")
                .contentType(ContentType.JSON)
                .when()
                .post("/" + "q1")
                .then()
                .statusCode(200)
                .body(containsString("Ciao, v1"));

        // --- Change #2
        LOGGER.info("[testDMNHotReload] Beginning Change #2");
        filter(source, Collections.singletonMap("\"Ciao, \"+", "\"Bonjour, \"+"));
        filter(controlSource, Collections.singletonMap("\"Ciao, \"+", "\"Bonjour, \"+"));
        await().pollDelay(RELOAD_POLL_DELAY, RELOAD_POLL_DELAY_UNIT)
                .atMost(RELOAD_POLL_TIMEOUT, RELOAD_POLL_TIMEOUT_UNIT).until(() -> getRestResponse(httpPort).contains("Bonjour, v1"));

        LOGGER.info("[testDMNHotReload] Evaluate DRL");
        given().baseUri("http://localhost:" + httpPort)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\n" +
                        "    \"strings\": [\"v1\"]  " +
                        "}")
                .contentType(ContentType.JSON)
                .when()
                .post("/" + "q1")
                .then()
                .statusCode(200)
                .body(containsString("Bonjour, v1"));

        LOGGER.info("done.");
    }

    @Test
    public void testStaticResource() throws MavenInvocationException {
        testDir = initProject("projects/simple-dmn", "projects/simple-dmn-static-resource");
        final String httpPort = run("testStaticResource");

        // await Quarkus
        await().pollDelay(INIT_POLL_DELAY, INIT_POLL_DELAY_UNIT)
                .atMost(INIT_POLL_TIMEOUT, INIT_POLL_TIMEOUT_UNIT).until(() -> getRestResponse(httpPort).contains("Hello, v1"));

        // static resource
        given().baseUri("http://localhost:" + httpPort)
                .get("/hello.json")
                .then()
                .statusCode(200)
                .body("definitions", aMapWithSize(greaterThan(0)));
    }
}
