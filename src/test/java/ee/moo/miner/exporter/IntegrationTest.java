/*
   miner-exporter - Prometheus exporter for cryptocurrency miners
   Copyright 2026 Tarmo Lehtpuu

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package ee.moo.miner.exporter;

import com.github.tomakehurst.wiremock.WireMockServer;
import ee.moo.miner.exporter.fake.FakeCGMiner;
import ee.moo.tiny.common.util.SystemUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class IntegrationTest {

    static {
        SystemUtil.silenceStderr(() -> LoggerFactory.getLogger(""));
    }

    public static final String APPLICATION_HOST = "127.0.0.1";
    public static final int APPLICATION_PORT = 8081;

    public static final String WIREMOCK_HOST = "127.0.0.1";
    public static final int WIREMOCK_PORT = 8082;

    public static final String CGMINER_HOST = "127.0.0.1";
    public static final int CGMINER_PORT = 8083;

    protected static Application application;

    protected static WireMockServer wiremock;

    protected static FakeCGMiner cgminer;

    protected static HttpClient http;

    @BeforeAll
    public static void beforeAll() throws Exception {
        wiremock = new WireMockServer(WIREMOCK_PORT);
        wiremock.start();

        cgminer = new FakeCGMiner(CGMINER_PORT);
        cgminer.start();
    }

    @AfterAll
    public static void afterAll() throws Exception {
        wiremock.stop();
        cgminer.stop();
    }

    public static void startApplication(String host, int port) throws Exception {
        application = new Application(Map.of(), host, port);
        application.start();
    }

    public static void startApplication(Map<String, String> env, String host, int port) throws Exception {
        application = new Application(env, host, port);
        application.start();
    }

    public static String resource(String path) throws IOException {
        try (var is = IntegrationTest.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new FileNotFoundException(String.format("Resource not found: %s", path));
            }

            try (var reader = new BufferedReader(new InputStreamReader(is, UTF_8))) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        }
    }

    public static URI wiremockUri() throws URISyntaxException {
        return new URI(String.format("http://%s:%d", WIREMOCK_HOST, WIREMOCK_PORT));
    }

    public static URI wiremockUri(String path) throws URISyntaxException {
        return new URI(String.format("http://%s:%d%s", WIREMOCK_HOST, WIREMOCK_PORT, path));
    }

    public static URI applicationUri(String path) throws URISyntaxException {
        return new URI(String.format("http://%s:%d%s", APPLICATION_HOST, APPLICATION_PORT, path));
    }

    public static URI cgminerUri() throws URISyntaxException {
        return new URI(String.format("tcp://%s:%d", CGMINER_HOST, CGMINER_PORT));
    }

    @BeforeEach
    public void beforeEach() throws Exception {
        wiremock.resetAll();
        cgminer.resetAll();

        http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofMillis(2000))
            .build();
    }

    @AfterEach
    public void afterEach() throws Exception {
        http.close();
        http = null;

        if (application != null) {
            application.stop();
        }
    }
}
