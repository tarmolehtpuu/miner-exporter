package ee.moo.miner.exporter.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerConfig;
import ee.moo.miner.exporter.miner.MinerFactory;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.WWWAuthenticationProtocolHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;


@Configuration
@Slf4j
public class MinerConfiguration {

    @Bean
    public HttpClient httpClient() throws Exception {
        var http = new HttpClient();
        http.start();
        http.getProtocolHandlers().remove(WWWAuthenticationProtocolHandler.NAME);
        return http;
    }

    @Bean
    public MinerFactory minerFactory(HttpClient httpClient, ObjectMapper objectMapper) {
        return new MinerFactory(httpClient, objectMapper);
    }

    @Bean
    public List<Miner> miners(MinerFactory minerFactory) {
        var miners = new ArrayList<Miner>();

        for (var config : MinerConfig.createFromEnvironment()) {
            var miner = minerFactory.create(config);

            log.info(
                "Adding miner: id='{}', type={}, host='{}', port={}",
                miner.getId(),
                miner.getType(),
                miner.getHost(),
                miner.getPort()
            );

            miners.add(miner);
        }

        if (miners.isEmpty()) {
            log.warn("No miners configured");
        }

        return miners;
    }
}
