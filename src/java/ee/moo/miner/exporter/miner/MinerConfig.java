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

import ee.moo.miner.exporter.client.CGMinerTcpClient;
import ee.moo.tiny.common.log.Logger;
import ee.moo.tiny.common.util.StringUtil;
import org.eclipse.jetty.client.*;
import org.eclipse.jetty.http.HttpHeader;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MinerConfig {

    private static final Logger log = new Logger(MinerConfig.class);

    private final String id;

    private final MinerType type;

    private final URI uri;

    private AuthMode mode = AuthMode.NONE;

    private String username;

    private String password;

    private Duration connectTimeout = Duration.ofMillis(1000);

    private Duration readTimeout = Duration.ofMillis(4000);

    private int readBufferSize = 8192;

    public MinerConfig(Map<String, String> env) throws URISyntaxException {
        this.id = env.get("MINER_ID");
        this.type = MinerType.valueOf(env.get("MINER_TYPE"));
        this.uri = new URI(env.get("MINER_URI"));

        if (env.containsKey("MINER_AUTH")) {
            this.mode = AuthMode.valueOf(env.get("MINER_AUTH"));
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

        this.validate();
    }

    public void validate() {
        if (StringUtil.isEmpty(id)) {
            throw new MinerException("Miner ID is required");
        }

        if (StringUtil.isEmpty(uri.toString())) {
            throw new MinerException("Miner URL is required (miner=%s)", id);
        }

        if (mode == AuthMode.BASIC || mode == AuthMode.DIGEST) {
            if (StringUtil.isEmpty(username)) {
                throw new MinerException("Username is required for MINER_AUTH mode: %s (miner=%s)", mode, id);
            }
            if (StringUtil.isEmpty(password)) {
                throw new MinerException("Password is required for MINER_AUTH mode: %s (miner=%s)", mode, id);
            }
        }

        if (mode == AuthMode.NONE) {
            if (!StringUtil.isEmpty(username)) {
                log.warn("Username specified but AUTH_MODE is set to NONE, ignoring...");
            }
            if (!StringUtil.isEmpty(password)) {
                log.warn("Password specified but AUTH_MODE is set to NONE, ignoring...");
            }
        }

        if (readBufferSize < 1024) {
            log.warn("Too low read buffer size: %d (miner=%s). Defaulting to 1024");
            readBufferSize = 1024;
        }
    }

    public CGMinerTcpClient createTcpClient() {
        var base = uri.toString();
        if (!base.startsWith("tcp://")) {
            throw new MinerException("Unable to create TCP client for URI=%s (miner=%s)", uri, id);
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
            throw new MinerException("Unable to create HTTP client for URI=%s (miner=%s)", uri, getId());
        }

        try {
            var client = new HttpClient();

            client.setFollowRedirects(true);
            client.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
            client.setConnectTimeout(connectTimeout.toMillis());


            client.getRequestListeners().addQueuedListener(new Request.QueuedListener() {
                @Override
                public void onQueued(Request request) {
                    if (request.getTimeout() == 0) {
                        request.timeout(readTimeout.toMillis(), TimeUnit.MILLISECONDS);
                    }

                    request.headers(httpFields -> {
                        if (!httpFields.contains(HttpHeader.USER_AGENT)) {
                            httpFields.add(HttpHeader.USER_AGENT, "miner-exporter/0.1.8");
                        }
                    });
                }
            });

            Authentication auth = null;
            if (mode == AuthMode.BASIC) {
                auth = new BasicAuthentication(uri, Authentication.ANY_REALM, username, password);
            }
            if (mode == AuthMode.DIGEST) {
                auth = new DigestAuthentication(uri, Authentication.ANY_REALM, username, password);
            }
            if (auth != null) {
                client.getAuthenticationStore().addAuthentication(auth);
            }

            client.getAuthenticationStore().addAuthentication(
                new DigestAuthentication(uri, Authentication.ANY_REALM, username, password)
            );
            client.start();

            return client;
        } catch (Exception e) {
            throw new MinerException(e.getMessage(), e);
        }
    }

    public String getId() {
        return id;
    }

    public MinerType getType() {
        return type;
    }

    public URI getUri() {
        return uri;
    }

    public enum AuthMode {
        NONE,
        BASIC,
        DIGEST
    }
}
