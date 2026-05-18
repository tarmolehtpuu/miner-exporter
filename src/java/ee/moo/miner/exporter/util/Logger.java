package ee.moo.miner.exporter.util;

import ee.moo.tiny.json.Json;
import ee.moo.tiny.json.JsonObject;
import ee.moo.tiny.json.JsonWriteMode;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    private final String name;
    private final Level level;
    private final Format format;

    public Logger(Class<?> cls) {
        var i = cls.getName().lastIndexOf('.');

        this.level = Level.valueOf(System.getenv().getOrDefault("LOG_LEVEL", "INFO"));
        this.format = Format.valueOf(System.getenv().getOrDefault("LOG_FORMAT", "TEXT"));

        if (i == -1) {
            this.name = cls.getName();
        } else {
            this.name = cls.getName().substring(i + 1);
        }
    }

    public boolean isEnabled(Level level) {
        return this.level.isEnabled(level);
    }

    public void debug(String message) {
        log(Level.DEBUG, message);
    }

    public void debug(String message, Object... args) {
        log(Level.DEBUG, message, args);
    }

    public void debug(String message, Throwable cause, Object... args) {
        log(Level.DEBUG, message, cause, args);
    }

    public void info(String message) {
        log(Level.INFO, message);
    }

    public void info(String message, Object... args) {
        log(Level.INFO, message, args);
    }

    public void info(String message, Throwable cause, Object... args) {
        log(Level.INFO, message, cause, args);
    }

    public void warn(String message) {
        log(Level.INFO, message);
    }

    public void warn(String message, Object... args) {
        log(Level.WARN, message, args);
    }

    public void warn(String message, Throwable cause, Object... args) {
        log(Level.WARN, message, cause, args);
    }

    public void error(String message) {
        log(Level.ERROR, message);
    }

    public void error(String message, Object... args) {
        log(Level.ERROR, message, args);
    }

    public void error(String message, Throwable cause, Object... args) {
        log(Level.ERROR, message, cause, args);
    }

    public void log(Level level, String message) {
        log(level, message, null, (Object) null);
    }

    public void log(Level level, String message, Object... args) {
        log(level, message, null, args);
    }

    public void log(Level level, String message, Throwable cause, Object... args) {
        if (!this.level.isEnabled(level)) {
            System.out.println("disabled");
            return;
        }

        var line = new Line();
        line.setTimestamp(ZonedDateTime.now());
        line.setLevel(level);
        line.setMessage(String.format(message, args));
        line.setThread(Thread.currentThread().getName());
        line.setFormat(format);
        line.setName(name);
        line.setCause(cause);

        line.toString().lines().forEach(System.out::println);
    }

    private static class Line {
        private ZonedDateTime timestamp;
        private Level level;
        private Format format;
        private String name;
        private String message;
        private String thread;
        private Throwable cause;

        public void setTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
        }

        public void setLevel(Level level) {
            this.level = level;
        }

        public void setFormat(Format format) {
            this.format = format;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setThread(String thread) {
            this.thread = thread;
        }

        public void setCause(Throwable cause) {
            this.cause = cause;
        }

        public String toString() {
            return format == Format.JSONL ? toJson() : toText();
        }

        public String toText() {
            var sb = new StringBuilder();

            sb.append(timestamp.format(TIME));
            sb.append(' ');
            sb.append('[');
            sb.append(thread);
            sb.append(']');
            sb.append(' ');
            sb.append(StringUtil.rpad(level.toString(), ' ', 5));
            sb.append(' ');
            sb.append(name);
            sb.append(' ');
            sb.append('-');
            sb.append(' ');
            sb.append(message);
            sb.append('\n');

            if (cause != null) {
                var sw = new StringWriter();
                var pw = new PrintWriter(sw);

                cause.printStackTrace(pw);

                sb.append(sw);
            }

            return sb.toString();
        }

        public String toJson() {
            var json = new JsonObject();
            json.put("@timestamp", timestamp.format(TIME));
            json.put("level", level.toString());
            json.put("message", message);
            json.put("thread", thread);

            if (cause != null) {
                var sw = new StringWriter();
                var pw = new PrintWriter(sw);

                cause.printStackTrace(pw);

                json.put("stacktrace", sw.toString().replace("\n", "\\n"));
            }

            return Json.write(json, JsonWriteMode.NORMAL);
        }
    }

    public enum Level {
        DEBUG {
            @Override
            public boolean isEnabled(Level level) {
                return level == DEBUG || level == INFO || level == WARN || level == ERROR;
            }
        },
        INFO {
            @Override
            public boolean isEnabled(Level level) {
                return level == INFO || level == WARN || level == ERROR;
            }
        },
        WARN {
            @Override
            public boolean isEnabled(Level level) {
                return level == WARN || level == ERROR;
            }
        },
        ERROR {
            @Override
            public boolean isEnabled(Level level) {
                return true;
            }
        };

        public abstract boolean isEnabled(Level level);
    }

    public enum Format {
        TEXT,
        JSONL
    }
}
