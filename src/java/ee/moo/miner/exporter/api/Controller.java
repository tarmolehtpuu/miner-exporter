package ee.moo.miner.exporter.api;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;


public interface Controller {

    boolean matches(Request request);

    void handle(Request request, Response response, Callback callback);
}
