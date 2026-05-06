package ee.moo.miner.exporter.api;

import ee.moo.miner.exporter.util.StringUtil;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import java.nio.ByteBuffer;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ReadyzController implements Controller {

    @Override
    public boolean matches(Request request) {
        if (!StringUtil.equals("GET", request.getMethod())) {
            return false;
        }

        return StringUtil.equals("/readyz", request.getHttpURI().getPath());
    }

    @Override
    public void handle(Request request, Response response, Callback callback) {
        response.setStatus(200);
        response.getHeaders().put("Content-Type", "text/plain");
        response.write(true, ByteBuffer.wrap("Ready!".getBytes(UTF_8)), callback);
    }
}
