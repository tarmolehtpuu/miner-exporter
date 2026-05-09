package ee.moo.miner.exporter.dataformat.json;

public final class JsonBoolean extends JsonValue {

    public static final JsonBoolean TRUE = new JsonBoolean(true);
    public static final JsonBoolean FALSE = new JsonBoolean(false);

    private final boolean value;

    private JsonBoolean(boolean value) {
        this.value = value;
    }

    public static JsonBoolean of(boolean value) {
        return value ? TRUE : FALSE;
    }

    public boolean value() {
        return value;
    }

    @Override
    public JsonType getType() {
        return JsonType.JSON_BOOLEAN;
    }

    @Override
    public String toJson() {
        return value ? "true" : "false";
    }

    @Override
    public String toJson(int indent) {
        return toJson();
    }

    @Override
    public boolean asBoolean() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof JsonBoolean other)) {
            return false;
        }

        return this.value == other.value;
    }

    @Override
    public int hashCode() {
        return Boolean.hashCode(value);
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }
}
