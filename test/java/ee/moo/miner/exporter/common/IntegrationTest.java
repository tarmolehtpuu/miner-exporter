package ee.moo.miner.exporter.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.HttpHeaders;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles(profiles = {"test"})
@ExtendWith(SpringExtension.class)
public class IntegrationTest {

    protected static WireMockServer wiremock = new WireMockServer(8082);

    @BeforeEach
    public void beforeEach() {
        wiremock.resetAll();
    }

    @BeforeAll
    public static void beforeAll() {
        wiremock.start();
    }

    @AfterAll
    public static void afterAll() {
        wiremock.stop();
    }

    protected LoggedRequest getWiremockRequest(RequestPatternBuilder pattern) {
        var requests = wiremock.findAll(pattern);
        if (requests.size() != 1) {
            throw new IllegalStateException("Expected exactly one wiremock request to match pattern");
        }

        return requests.getFirst();
    }

    protected HttpHeaders getWiremockRequestHeaders(RequestPatternBuilder pattern) {
        return getWiremockRequest(pattern).getHeaders();
    }

    protected String getWiremockRequestBodyString(RequestPatternBuilder pattern) {
        return getWiremockRequest(pattern).getBodyAsBase64();
    }

    protected JsonNode getWiremockRequestBodyJson(RequestPatternBuilder pattern) {
        try {
            return new ObjectMapper().readTree(getWiremockRequestBodyString(pattern));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
