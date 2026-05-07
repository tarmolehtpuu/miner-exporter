package ee.moo.miner.exporter.client;

import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final String host;
    private final int port;

    @Setter
    private Duration connectTimeout = Duration.ofSeconds(2);

    @Setter
    private Duration readTimeout = Duration.ofSeconds(2);

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
