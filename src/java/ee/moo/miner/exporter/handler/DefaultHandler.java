package ee.moo.miner.exporter.handler;


import ee.moo.miner.exporter.metrics.MetricsController;
import ee.moo.miner.exporter.miner.MinerNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
public class DefaultHandler extends Handler.Abstract {

    private final MetricsController controller;

    public DefaultHandler(MetricsController controller) {
        this.controller = controller;
    }

    @Override
    public boolean handle(Request request, Response response, Callback callback) throws Exception {
        var path = request.getHttpURI().getPath();

        if (path.equals("/healthz")) {
            response.setStatus(200);
            response.getHeaders().put("Content-Type", "text/plain");
            response.write(true, ByteBuffer.wrap("Healthy!".getBytes(UTF_8)), callback);

            return true;
        }

        if (path.equals("/readyz")) {
            response.setStatus(200);
            response.getHeaders().put("Content-Type", "text/plain");
            response.write(true, ByteBuffer.wrap("Ready!".getBytes(UTF_8)), callback);

            return true;
        }

        if (path.startsWith("/metrics/")) {
            var count = path.chars()
                .filter(c -> c == '/')
                .count();

            if (count != 2) {
                log.warn("Ignoring invalid /metrics path: {}", path);
                return false;
            }

            String id = path.substring(path.lastIndexOf("/") + 1);
            String metrics = "";

            try {
                metrics = controller.getMetrics(
                    path.substring(path.lastIndexOf("/") + 1)
                );

            } catch (MinerNotFoundException e) {
                response.setStatus(404);
                response.getHeaders().put("Content-Type", "text/plain");
                response.write(true, ByteBuffer.wrap(e.getMessage().getBytes(UTF_8)), callback);

                return true;
            }

            if (metrics.isEmpty()) {
                log.warn("Empty metrics returned for miner (id={})", id);
            }

            response.setStatus(200);
            response.getHeaders().put("Content-Type", "text/plain; version=0.0.4; charset=utf-8");
            response.write(true, ByteBuffer.wrap(metrics.getBytes(UTF_8)), callback);

            return true;
        }

        return false;
    }
}

