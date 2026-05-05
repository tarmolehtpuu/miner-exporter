package ee.moo.miner.exporter.miner.antminer;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import ee.moo.miner.exporter.client.ClientException;
import ee.moo.miner.exporter.metrics.Metrics;
import ee.moo.miner.exporter.miner.*;
import ee.moo.miner.exporter.miner.antminer.model.AntMinerPool;
import ee.moo.miner.exporter.miner.antminer.model.AntMinerStats;
import ee.moo.miner.exporter.miner.antminer.model.AntMinerSummary;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.client.Authentication;
import org.eclipse.jetty.client.BasicAuthentication;
import org.eclipse.jetty.client.DigestAuthentication;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.http.HttpMethod;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@RequiredArgsConstructor
public class Antminer implements Miner {

    private final MinerConfig config;

    private final ObjectMapper objectMapper;

    private HttpClient client;

    @Override
    public MinerConfig getConfig() {
        return config;
    }

    @Override
    public MinerType getType() {
        return MinerType.ANTMINER;
    }

    @Override
    public Metrics getMetrics() {
        var summary = getSummary();
        System.out.println(summary);

        var pools = getPools();
        for (var pool : pools) {
            System.out.println(pool);
        }

        return new AntminerMetrics();
    }

    private AntMinerSummary getSummary() {
        var client = getClient();

        try {
            var response = client.newRequest(String.format("%s/cgi-bin/miner_summary.cgi", config.getUri().toString()))
                .timeout(config.getReadTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .method(HttpMethod.GET)
                .send();

            if (response.getStatus() != 200) {
                throw new ClientException("Unexpected http status code: %s (miner=%s, cmd=summary)", response.getStatus(), config.getId());
            }

            var json = objectMapper.readTree(response.getContent());
            validateHeader(json);
            validateSummary(json);

            var summary = json.get("SUMMARY").get(0);

            return AntMinerSummary.builder()
                .rate(summary.get("GHS 5s").asLong() * 1_000_000_000)
                .build();

        } catch (JsonMappingException e) {
            throw new MinerException(e.getMessage(), e);
        } catch (ExecutionException | InterruptedException | TimeoutException | IOException e) {
            throw new MinerException(e.getMessage(), e);
        }
    }

    private List<AntMinerPool> getPools() {
        var client = getClient();
        var result = new ArrayList<AntMinerPool>();

        try {
            var response = client.newRequest(String.format("%s/cgi-bin/miner_pools.cgi", config.getUri().toString()))
                .timeout(config.getReadTimeout().toMillis(), TimeUnit.MILLISECONDS)
                .method(HttpMethod.GET)
                .send();

            if (response.getStatus() != 200) {
                throw new ClientException("Unexpected http status code: %s (miner=%s, cmd=pools)", response.getStatus(), config.getId());
            }

            System.out.println(response.getContentAsString());

            var json = objectMapper.readTree(response.getContent());
            validateHeader(json);
            validatePools(json);

            for (var p : json.get("POOLS")) {
                result.add(AntMinerPool.builder()
                    .id(p.get("POOL").asInt())
                    .uri(new URI(p.get("URL").asText()))
                    .user(p.get("User").asText())
                    .status(p.get("Status").asText())
                    .priority(p.get("Priority").asInt())
                    .build()
                );
            }

            return result;

        } catch (JsonMappingException e) {
            throw new MinerException(e.getMessage(), e);
        } catch (ExecutionException | InterruptedException | TimeoutException | IOException | URISyntaxException e) {
            throw new MinerException(e.getMessage(), e);
        }
    }

    private List<AntMinerStats> getStats() {
        throw new UnsupportedOperationException("Unimplemented");
    }

    private void validateHeader(JsonNode json) {
    }

    private void validateSummary(JsonNode json) {
        if (json.get("SUMMARY").getNodeType() != JsonNodeType.ARRAY) {
            throw new MinerException("Expecting response to contain SUMMARY array (cmd=%s)", "summary");
        }

        if (json.get("SUMMARY").size() != 1) {
            throw new MinerException("Expecting SUMMARY array to contain exactly one element (cmd=%s)", "summary");
        }

        if (json.get("SUMMARY").get(0).getNodeType() != JsonNodeType.OBJECT) {
            throw new MinerException("Expecting an object in SUMMARY array (cmd=%s)", "summary");
        }
    }

    private void validatePools(JsonNode json) {
    }

    private void validateStats(JsonNode json) {

    }

    private HttpClient getClient() {
        if (client == null) {
            try {
                client = new HttpClient();

                client.setConnectTimeout(config.getConnectTimeout().toMillis());
                client.setResponseBufferSize(config.getReadBufferSize());
                client.start();

                var base = config.getUri().toString();
                if (!base.endsWith("/")) {
                    base = String.format("%s/", base);
                }

                if (config.getAuth() == MinerConfig.AuthMode.BASIC) {
                    client
                        .getAuthenticationStore()
                        .addAuthentication(new BasicAuthentication(
                            new URI(base),
                            Authentication.ANY_REALM,
                            config.getUsername(),
                            config.getPassword()
                        ));
                }

                if (config.getAuth() == MinerConfig.AuthMode.DIGEST) {
                    client
                        .getAuthenticationStore()
                        .addAuthentication(new DigestAuthentication(
                            new URI(base),
                            Authentication.ANY_REALM,
                            config.getUsername(),
                            config.getPassword()
                        ));
                }

            } catch (Exception e) {
                throw new MinerException(e.getMessage(), e);
            }
        }

        return client;
    }
}
