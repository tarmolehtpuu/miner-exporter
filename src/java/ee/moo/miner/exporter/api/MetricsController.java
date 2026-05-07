package ee.moo.miner.exporter.api;

import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
@Slf4j
public class MetricsController implements Controller {

    private final Miner miner;

    @Override
    public boolean matches(Request request) {
        if (!StringUtil.equals("GET", request.getMethod())) {
            return false;
        }

        return StringUtil.equals("/metrics", request.getHttpURI().getPath());
    }

    @Override
    public void handle(Request request, Response response, Callback callback) {
        var bytes = miner
            .getMetrics()
            .export()
            .getBytes(UTF_8);

        response.setStatus(200);
        response.getHeaders().put("Content-Type", "text/plain; version=0.0.4; charset=utf-8");
        response.write(true, ByteBuffer.wrap(bytes), callback);
    }
}
