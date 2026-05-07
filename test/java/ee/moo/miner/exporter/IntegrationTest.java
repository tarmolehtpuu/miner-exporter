package ee.moo.miner.exporter;

import com.github.tomakehurst.wiremock.WireMockServer;
import ee.moo.miner.exporter.fake.FakeCGMiner;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.Request;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class IntegrationTest {

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

    @BeforeEach
    public void beforeEach() throws Exception {
        wiremock.resetAll();
        cgminer.resetAll();

        http = new HttpClient();
        http.setConnectTimeout(2000);
        http.setResponseBufferSize(8192);
        http.getRequestListeners().addListener(new Request.Listener() {
            @Override
            public void onQueued(Request request) {
                request.timeout(2000, TimeUnit.MILLISECONDS);
            }
        });
        http.start();
    }

    @AfterEach
    public void afterEach() throws Exception {
        http.stop();
        http = null;
    }

    @BeforeAll
    public static void beforeAll() throws Exception {
        wiremock = new WireMockServer(WIREMOCK_PORT);
        wiremock.start();

        var env = Map.of(
            "MINER_0_ID", "miner01",
            "MINER_0_TYPE", "BITAXE",
            "MINER_0_URI", wiremockUri(),
            "MINER_1_ID", "miner02",
            "MINER_1_TYPE", "AVALON",
            "MINER_1_URI", cgminerUri(),
            "MINER_2_ID", "miner03",
            "MINER_2_TYPE", "ANTMINER",
            "MINER_2_URI", wiremockUri()
        );

        application = new Application(env, APPLICATION_PORT);
        application.start();

        cgminer = new FakeCGMiner(CGMINER_PORT);
        cgminer.start();
    }

    @AfterAll
    public static void afterAll() throws Exception {
        wiremock.stop();
        application.stop();
        cgminer.stop();
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

    public static String wiremockUri() {
        return String.format("http://%s:%d", WIREMOCK_HOST, WIREMOCK_PORT);
    }

    public static String wiremockUri(String path) {
        return String.format("http://%s:%d%s", WIREMOCK_HOST, WIREMOCK_PORT, path);
    }

    public static String applicationUri(String path) {
        return String.format("http://%s:%d%s", APPLICATION_HOST, APPLICATION_PORT, path);
    }

    public static String cgminerUri() {
        return String.format("tcp://%s:%d", CGMINER_HOST, CGMINER_PORT);
    }
}
