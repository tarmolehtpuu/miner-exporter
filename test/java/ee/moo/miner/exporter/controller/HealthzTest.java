package ee.moo.miner.exporter.controller;

import ee.moo.miner.exporter.IntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HealthzTest extends IntegrationTest {

    @Test
    public void testHealthz() throws ExecutionException, InterruptedException, TimeoutException {
        var response = http.newRequest(applicationUri("/healthz")).send();

        assertEquals(200, response.getStatus());
        assertEquals("text/plain", response.getHeaders().get("Content-Type"));
        assertEquals("Healthy!", response.getContentAsString());
    }
}
