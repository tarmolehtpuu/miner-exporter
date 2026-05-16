package ee.moo.miner.exporter.util;

import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class LogHandler extends StreamHandler  {

    public LogHandler(Level level) {
        setOutputStream(System.out);
        setFormatter(new LogFormatter());
        setLevel(level);
    }

    @Override
    public void publish(LogRecord record) {
        super.publish(record);
        super.flush();
    }
}
