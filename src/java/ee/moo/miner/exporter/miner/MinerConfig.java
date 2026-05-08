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
package ee.moo.miner.exporter.miner;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ee.moo.miner.exporter.cgminer.CGMinerTcpClient;
import ee.moo.miner.exporter.util.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.client.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;
import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS;

@Data
@Slf4j
public class MinerConfig {

    private String id;

    private MinerType type;

    private URI uri;

    private AuthMode auth = AuthMode.NONE;

    private String username;

    private String password;

    private Duration connectTimeout = Duration.ofMillis(2000);

    private Duration readTimeout = Duration.ofMillis(2000);

    private int readBufferSize = 8192;

    public MinerConfig(Map<String, String> env) throws URISyntaxException {
        this.id = env.get("MINER_ID");
        this.type = MinerType.valueOf(env.get("MINER_TYPE"));
        this.uri = new URI(env.get("MINER_URI"));

        if (env.containsKey("MINER_AUTH")) {
            this.auth = AuthMode.valueOf(env.get("MINER_AUTH"));
        }

        if (env.containsKey("MINER_USERNAME")) {
            this.username = env.get("MINER_USERNAME");
        }

        if (env.containsKey("MINER_PASSWORD")) {
            this.password = env.get("MINER_PASSWORD");
        }

        if (env.containsKey("MINER_CONNECT_TIMEOUT")) {
            this.connectTimeout = Duration.parse(env.get("MINER_CONNECT_TIMEOUT"));
        }

        if (env.containsKey("MINER_READ_TIMEOUT")) {
            this.readTimeout = Duration.parse(env.get("MINER_READ_TIMEOUT"));
        }

        if (env.containsKey("MINER_READ_BUFFER_SIZE")) {
            this.readBufferSize = Integer.parseInt(env.get("MINER_READ_BUFFER_SIZE"));
        }

        // FIXME
        this.validate();
    }

    public void validate() {
        if (StringUtil.isEmpty(id)) {
            throw new MinerException("Miner ID is required");
        }

        if (StringUtil.isEmpty(uri.toString())) {
            throw new MinerException("Miner URL is required (miner=%s)", id);
        }

        if (auth == AuthMode.BASIC || auth == AuthMode.DIGEST) {
            if (!uri.toString().startsWith("http://") && !uri.toString().startsWith("https://")) {
                throw new MinerException("AuthMode.%s is only supported for http|https URL-s (miner=%s)", auth, id);
            }

            if (StringUtil.isEmpty(username)) {
                throw new MinerException("Miner username is required for AuthMode.%s (miner=%s)", auth, id);
            }

            if (StringUtil.isEmpty(password)) {
                throw new MinerException("Password is required for AuthMode.%s (miner=%s)", auth, id);
            }
        }

        if (readBufferSize < 1024) {
            log.warn("Too low read buffer size: {} (miner={}). Defaulting to 1024", readBufferSize, id);
        }
    }

    public CGMinerTcpClient createTcpClient() {
        var base = uri.toString();
        if (!base.startsWith("tcp://")) {
            throw new MinerException("Unable to create TCP client for URI=%s (miner=%s)", uri, id);
        }

        if (auth != AuthMode.NONE) {
            throw new MinerException("Authentication mode (auth=%s) not supported for TCP client (miner=%s)", auth, id);
        }

        var client = new CGMinerTcpClient(uri.getHost(), uri.getPort());

        client.setConnectTimeout(connectTimeout);
        client.setReadTimeout(readTimeout);
        client.setReadBufferSize(readBufferSize);

        return client;
    }

    public HttpClient createHttpClient() {
        var base = uri.toString();
        if (!base.startsWith("http://") && !base.startsWith("https://")) {
            throw new MinerException("Unable to create http client for URI=%s (miner=%s)", uri, id);
        }

        if (!base.endsWith("/")) {
            base = String.format("%s/", base);
        }

        try {
            var client = new HttpClient();

            client.setConnectTimeout(connectTimeout.toMillis());
            client.getRequestListeners().addListener(new Request.Listener() {
                @Override
                public void onQueued(Request request) {
                    request.timeout(readTimeout.toMillis(), TimeUnit.MILLISECONDS);
                }
            });
            client.setResponseBufferSize(readBufferSize);
            client.start();

            if (auth == MinerConfig.AuthMode.BASIC) {
                client
                    .getAuthenticationStore()
                    .addAuthentication(new BasicAuthentication(
                        new URI(base),
                        Authentication.ANY_REALM,
                        getUsername(),
                        getPassword()
                    ));
            }

            if (auth == MinerConfig.AuthMode.DIGEST) {
                client
                    .getAuthenticationStore()
                    .addAuthentication(new DigestAuthentication(
                        new URI(base),
                        Authentication.ANY_REALM,
                        username,
                        password
                    ));
            }

            return client;

        } catch (Exception e) {
            throw new MinerException(e.getMessage(), e);
        }
    }

    public ObjectMapper createObjectMapper() {
        return JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .defaultPropertyInclusion(JsonInclude.Value.construct(NON_ABSENT, NON_ABSENT))
            .disable(FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(WRITE_DATES_AS_TIMESTAMPS)
            .build();
    }

    public enum AuthMode {
        NONE,
        BASIC,
        DIGEST
    }
}
