package ee.moo.miner.exporter.client.cgminer;

import com.fasterxml.jackson.databind.ObjectMapper;
import ee.moo.miner.exporter.client.cgminer.model.*;
import ee.moo.miner.exporter.client.cgminer.request.CGMinerCommand;
import ee.moo.miner.exporter.client.cgminer.response.*;
import ee.moo.miner.exporter.client.ClientException;
import lombok.Setter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.time.Duration;
import java.util.List;

public class CGMinerClient {

    private final String host;
    private final int port;

    @Setter
    private ObjectMapper objectMapper;

    private int connectTimeout = 2000;

    private int readTimeout = 2000;

    @Setter
    private int readBufferMaxSize = 32000;

    public CGMinerClient(String host, int port) {
        this.host = host;
        this.port = port;
        this.objectMapper = new ObjectMapper();
    }

    public void setConnectTimeout(Duration duration) {
        this.connectTimeout = duration.toMillisPart();
    }

    public void setReadTimeout(Duration duration) {
        this.readTimeout = duration.toMillisPart();
    }

    public String execute(String command) throws IOException {
        var socket = new Socket();
        try (socket) {
            socket.connect(new InetSocketAddress(host, port), connectTimeout);
            socket.setSoTimeout(readTimeout);

            var out = new PrintWriter(socket.getOutputStream());
            out.write(objectMapper.writeValueAsString(new CGMinerCommand(command)));
            out.flush();

            return read(socket);
        }
    }

    public CGMinerVersion getVersion() {
        try {
            var response = objectMapper.readValue(execute("version"), CGMinerVersionResponse.class);
            if (response.isError()) {
                throw new ClientException("CGMiner Error (cmd=version): %s", response.getError());
            }
            return response.getVersion();

        } catch (IOException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public CGMinerSummary getSummary() {
        try {
            var response = objectMapper.readValue(execute("summary"), CGMinerSummaryResponse.class);
            if (response.isError()) {
                throw new ClientException("CGMiner Error (cmd=summary): %s", response.getError());
            }
            return response.getSummary();

        } catch (IOException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public CGMinerConfig getConfig() {
        try {
            var response = objectMapper.readValue(execute("config"), CGMinerConfigResponse.class);
            if (response.isError()) {
                throw new ClientException("CGMiner Error (cmd=config): %s", response.getError());
            }
            return response.getConfig();

        } catch (IOException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public List<CGMinerPool> getPools() {
        try {
            var response = objectMapper.readValue(execute("pools"), CGMinerPoolsResponse.class);
            if (response.isError()) {
                throw new ClientException("CGMiner Error (cmd=pools): %s", response.getError());
            }
            return response.getPools();

        } catch (IOException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    public List<CGMinerDevice> getDevices() {
        try {
            var response = objectMapper.readValue(execute("devs"), CGMinerDevsResponse.class);
            if (response.isError()) {
                throw new ClientException("CGMiner Error (cmd=devs): %s", response.getError());
            }
            return response.getDevices();

        } catch (IOException e) {
            throw new ClientException(e.getMessage(), e);
        }
    }

    private String read(Socket socket) throws IOException {
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
