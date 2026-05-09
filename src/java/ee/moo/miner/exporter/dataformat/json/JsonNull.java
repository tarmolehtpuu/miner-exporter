package ee.moo.miner.exporter.dataformat.json;

import java.util.Objects;

public final class JsonNull extends JsonValue {

    public static final JsonNull INSTANCE = new JsonNull();

    private JsonNull() {
    }

    @Override
    public JsonType getType() {
        return JsonType.JSON_NULL;
    }

    @Override
    public String toJson() {
        return "null";
    }

    @Override
    public String toJson(int indent) {
        return toJson();
    }

    @Override
    public Object asNull() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return obj instanceof JsonNull;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(null);
    }

    @Override
    public String toString() {
        return "null";
    }
}