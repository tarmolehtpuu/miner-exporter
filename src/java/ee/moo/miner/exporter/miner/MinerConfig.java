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

import ee.moo.miner.exporter.cgminer.CGMinerTcpClient;
import ee.moo.miner.exporter.util.StringUtil;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MinerConfig {

    private static final Logger logger = Logger.getLogger(MinerConfig.class.getName());

    private final String id;

    private final MinerType type;

    private final URI uri;

    private String username;

    private String password;

    private Duration connectTimeout = Duration.ofMillis(2000);

    private Duration readTimeout = Duration.ofMillis(2000);

    private int readBufferSize = 8192;

    public MinerConfig(Map<String, String> env) throws URISyntaxException {
        this.id = env.get("MINER_ID");
        this.type = MinerType.valueOf(env.get("MINER_TYPE"));
        this.uri = new URI(env.get("MINER_URI"));

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

        if (readBufferSize < 1024) {
            logger.log(
                Level.WARNING,
                "Too low read buffer size: %d (miner=%s). Defaulting to 1024",
                new Object[]{readBufferSize, id}
            );
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
            throw new MinerException("Unable to create http client for URI=%s (miner=%s)", uri, id);
        }

        try {
            var builder = HttpClient.newBuilder();
            builder.version(HttpClient.Version.HTTP_1_1);
            builder.connectTimeout(connectTimeout);
            builder.executor(Executors.newVirtualThreadPerTaskExecutor());
            builder.followRedirects(HttpClient.Redirect.NORMAL);

            if (!StringUtil.isEmpty(username) && !StringUtil.isEmpty(password)) {
                builder.authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password.toCharArray());
                    }
                });
            }

            return builder.build();

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public Duration getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
    }

    public int getReadBufferSize() {
        return readBufferSize;
    }

    public void setReadBufferSize(int readBufferSize) {
        this.readBufferSize = readBufferSize;
    }
}
