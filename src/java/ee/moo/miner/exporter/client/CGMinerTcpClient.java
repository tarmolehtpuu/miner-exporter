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
package ee.moo.miner.exporter.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
public class CGMinerTcpClient {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Getter
    private final String host;

    @Getter
    private final int port;

    @Getter
    @Setter
    private Duration connectTimeout = Duration.ofSeconds(2);

    @Getter
    @Setter
    private Duration readTimeout = Duration.ofSeconds(2);

    @Getter
    @Setter
    private int readBufferSize = 8192;

    public String execute(String command) throws IOException {
        var socket = new Socket();
        try (socket) {
            socket.connect(new InetSocketAddress(host, port), connectTimeout.toMillisPart());
            socket.setSoTimeout(readTimeout.toMillisPart());

            var out = new PrintWriter(socket.getOutputStream());
            out.write(objectMapper.writeValueAsString(Map.of("command", command)));
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
                .replace("\0", "");
        }
    }
}
