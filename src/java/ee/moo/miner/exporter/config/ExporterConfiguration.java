package ee.moo.miner.exporter.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.miner.MinerConfig;
import ee.moo.miner.exporter.miner.MinerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;


@Configuration
@Slf4j
public class ExporterConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        return JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .defaultPropertyInclusion(
                JsonInclude.Value.construct(NON_ABSENT, NON_ABSENT)
            )
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .build();
    }

    @Bean
    public MinerFactory minerFactory(ObjectMapper objectMapper) {
        return new MinerFactory(objectMapper);
    }

    @Bean
    public List<Miner> miners(MinerFactory minerFactory) {
        var miners = new ArrayList<Miner>();

        for (var config : MinerConfig.createFromEnvironment()) {
            var miner = minerFactory.create(config);

            log.info(
                "Adding miner: id='{}', type={}, uri='{}'",
                miner.getId(),
                miner.getType(),
                miner.getUri()
            );

            miners.add(miner);
        }

        if (miners.isEmpty()) {
            log.warn("No miners configured");
        }

        return miners;
    }
}
