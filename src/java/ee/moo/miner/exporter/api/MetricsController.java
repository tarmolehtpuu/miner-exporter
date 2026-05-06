package ee.moo.miner.exporter.api;

import ee.moo.miner.exporter.miner.Miner;
import ee.moo.miner.exporter.util.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import java.nio.ByteBuffer;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@RequiredArgsConstructor
@Slf4j
public class MetricsController implements Controller {

    private final List<Miner> miners;

    @Override
    public boolean matches(Request request) {
        if (!StringUtil.equals("GET", request.getMethod())) {
            return false;
        }

        var path = request.getHttpURI().getPath();
        if (!path.startsWith("/metrics/")) {
            return false;
        }

        var count = path.chars()
            .filter(c -> c == '/')
            .count();

        if (count != 2) {
            log.warn("Ignoring invalid metrics path: {}", path);
            return false;
        }

        var miner = miners.stream()
            .filter(m -> StringUtil.equals(m.getId(), getId(path)))
            .findFirst()
            .orElse(null);

        if (miner == null) {
            log.warn("Miner not found (id={})", getId(path));
            return false;
        }

        return true;
    }

    @Override
    public void handle(Request request, Response response, Callback callback) {
        var miner = miners.stream()
            .filter(m -> StringUtil.equals(m.getId(), getId(request.getHttpURI().getPath())))
            .findFirst()
            .orElseThrow();

        var bytes = miner
            .getMetrics()
            .export()
            .getBytes(UTF_8);

        response.setStatus(200);
        response.getHeaders().put("Content-Type", "text/plain; version=0.0.4; charset=utf-8");
        response.write(true, ByteBuffer.wrap(bytes), callback);
    }

    private String getId(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }
}
