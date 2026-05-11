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
package ee.moo.miner.exporter.client.cgminer;

import ee.moo.tiny.json.Json;
import ee.moo.tiny.json.JsonObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;

public class CGMinerTcpClient {

    private final String host;
    private final int port;

    private Duration connectTimeout = Duration.ofSeconds(2);

    private Duration readTimeout = Duration.ofSeconds(2);

    private int readBufferSize = 8192;

    public CGMinerTcpClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String execute(String command) throws IOException {
        var socket = new Socket();
        try (socket) {
            socket.connect(new InetSocketAddress(host, port), connectTimeout.toMillisPart());
            socket.setSoTimeout(readTimeout.toMillisPart());

            var out = new PrintWriter(socket.getOutputStream());
            var json = new JsonObject();
            json.put("command", command);

            out.write(Json.write(json));
            out.flush();

            var in = new InputStreamReader(socket.getInputStream());
            var sb = new StringBuilder();

            char[] buf = new char[readBufferSize];

            int max = readBufferSize;
            int len;
            while (true) {
                len = in.read(buf, 0, max);
                if (len < 1) {
                    break;
                }

                sb.append(buf, 0, len);
                if (buf[len - 1] == '\0') {
                    break;
                }
            }

            return sb.toString()
                .replace("}{", "},{")
                .replace("\0", "")
                .trim();
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
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
