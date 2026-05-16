package ee.moo.miner.exporter.util;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class Log {

    public static void configure() {
        LogManager.getLogManager().reset();

        var level = Level.parse(System.getenv().getOrDefault("LOG_LEVEL", "INFO"));
        var log = Logger.getLogger("");

        log.addHandler(new LogHandler(level));
        log.setLevel(level);
    }
}
