package ee.moo.miner.exporter.controller;

import ee.moo.miner.exporter.IntegrationTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ReadyzTest extends IntegrationTest {

    @Test
    public void testReadyz() throws Exception {
        startApplication(APPLICATION_HOST, APPLICATION_PORT);

        var response = http
            .newRequest(applicationUri("/readyz"))
            .send();

        assertEquals(200, response.getStatus());
        assertEquals("text/plain", response.getHeaders().get("Content-Type"));
        assertEquals("Ready!", response.getContentAsString());
    }
}
