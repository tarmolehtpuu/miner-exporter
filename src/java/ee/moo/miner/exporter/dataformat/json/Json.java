package ee.moo.miner.exporter.dataformat.json;

import java.io.*;
import java.nio.charset.StandardCharsets;

public final class Json {

    public static String write(JsonValue value) {
        return String.format("%s\n", value.toJson());
    }

    public static JsonValue read(String json) {
        if (json == null) {
            throw new JsonException("Cannot parse null JSON string");
        }
        return new JsonParser(json).parse();
    }


    public static JsonObject readObject(String json) {
        return read(json).asObject();
    }

    public static JsonValue read(byte[] bytes) {
        if (bytes == null) {
            throw new JsonException("Can not parse null JSON bytes");
        }

        return new JsonParser(new String(bytes, StandardCharsets.UTF_8)).parse();
    }

    public static JsonObject readObject(byte[] bytes) {
        return read(bytes).asObject();
    }

    public static JsonValue read(File file) {
        if (file == null) {
            throw new JsonException("Cannot read from null file");
        }

        try (var reader = new FileReader(file, StandardCharsets.UTF_8)) {
            return read(reader);
        } catch (IOException e) {
            throw new JsonException("Failed to read file: %s", e, file.getPath());
        }
    }

    public static JsonValue read(Reader reader) {
        if (reader == null) {
            throw new JsonException("Cannot read from null reader");
        }

        try {
            var buf = new BufferedReader(reader);
            var sb = new StringBuilder();

            String line;
            while ((line = buf.readLine()) != null) {
                sb.append(line);
                sb.append('\n');
            }

            return read(sb.toString());

        } catch (IOException e) {
            throw new JsonException("Failed to read JSON", e);
        }
    }
}
