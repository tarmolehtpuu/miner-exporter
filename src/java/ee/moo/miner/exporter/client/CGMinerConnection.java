package ee.moo.miner.exporter.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.client.command.CGMinerCommand;
import ee.moo.miner.exporter.client.model.CGMinerVersion;
import ee.moo.miner.exporter.client.response.CGMinerVersionResponse;
import ee.moo.miner.exporter.miner.MinerException;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;

public class CGMinerConnection {

    private final Socket socket;

    private final String host;
    private final int port;

    @Setter
    private ObjectMapper objectMapper = new ObjectMapper();

    private int connectTimeout = 2000;

    private int readTimeout = 2000;

    @Setter
    private int readBufferMaxSize = 32000;

    public CGMinerConnection(String host, int port) {
        socket = new Socket();
        this.host = host;
        this.port = port;
        objectMapper = new ObjectMapper();
    }

    public void setConnectTimeout(Duration duration) {
        this.connectTimeout = duration.toMillisPart();
    }

    public void setReadTimeout(Duration duration) {
        this.readTimeout = duration.toMillisPart();
    }

    public void connect() throws IOException {
        socket.connect(new InetSocketAddress(host, port), connectTimeout);
        socket.setSoTimeout(readTimeout);
    }

    public void close() throws IOException {
        socket.close();
    }

    public String execute(String command) throws IOException {
        var out = new PrintWriter(socket.getOutputStream());

        out.write(objectMapper.writeValueAsString(new CGMinerCommand(command)));
        out.flush();

        return recv();
    }

    public CGMinerVersion getVersion() {
        try {
            var response = objectMapper.readValue(execute("version"), CGMinerVersionResponse.class);
            if (response.isError()) {
                throw new MinerException("CGMiner Error: %s", response.getError());
            }

            return response.toVersion();

        } catch (IOException e) {
            throw new MinerException(e.getMessage(), e);
        }
    }

    private String recv() throws IOException {
        var in = new InputStreamReader(socket.getInputStream());
        var sb = new StringBuilder();

        char[] buf = new char[readBufferMaxSize];

        int max = readBufferMaxSize;
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
