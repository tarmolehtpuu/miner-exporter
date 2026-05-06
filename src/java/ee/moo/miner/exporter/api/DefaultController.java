package ee.moo.miner.exporter.api;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.util.Callback;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
public class DefaultController extends Handler.Abstract {

    private final List<Controller> controllers;

    @Override
    public boolean handle(Request request, Response response, Callback callback) throws Exception {
        for (var controller : controllers) {
            if (controller.matches(request)) {
                controller.handle(request, response, callback);
                return true;
            }
        }

        return false;
    }
}

