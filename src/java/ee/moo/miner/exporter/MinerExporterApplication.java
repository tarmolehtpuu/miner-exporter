package ee.moo.miner.exporter;

import ee.moo.miner.exporter.handler.DefaultHandler;
import org.eclipse.jetty.server.*;

public class MinerExporterApplication {

    static void main() throws Exception {
        var server = new Server();

        var connector = new ServerConnector(server);
        connector.setPort(8080);

        server.addConnector(connector);
        server.setDefaultHandler(new DefaultHandler());
        server.start();
        server.join();
    }
}
