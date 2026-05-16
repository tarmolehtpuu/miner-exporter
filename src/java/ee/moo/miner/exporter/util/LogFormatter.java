package ee.moo.miner.exporter.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class LogFormatter extends Formatter {

    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    @Override
    public String format(LogRecord record) {
        var sb = new StringBuilder(String.format(
                "[%s][%s][%s][%s]: %s%n",
                formatTime(record.getInstant()),
                Thread.currentThread().getName(),
                formatLevel(record.getLevel().getLocalizedName()),
                formatClass(record.getSourceClassName()),
                formatMessage(record)
        ));

        if (record.getThrown() != null) {
            try {
                var sw = new StringWriter();
                var pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                sb.append(sw);
            } catch (Exception e) {
                //noinspection CallToPrintStackTrace
                e.printStackTrace();
            }
        }

        return sb.toString();
    }

    private String formatTime(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).format(timeFormatter);
    }

    private String formatLevel(String level) {
        if (level.length() > 4) {
            level = level.substring(0, 4);
        }
        if (level.length() < 4) {
            level = StringUtil.rpad(level, ' ', 4);
        }

        return level;
    }

    private String formatClass(String name) {
        return name.substring(name.lastIndexOf('.') + 1);
    }

    @Override
    public String formatMessage(LogRecord record) {
        if (record.getParameters() == null || record.getParameters().length == 0) {
            return record.getMessage();
        }

        return String.format(record.getMessage(), record.getParameters());
    }
}
