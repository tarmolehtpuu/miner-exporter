package ee.moo.miner.exporter.dataformat.json;


public final class JsonNumber extends JsonValue {

    private final String value;

    public JsonNumber(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public JsonType getType() {
        return JsonType.JSON_NUMBER;
    }

    @Override
    public String toJson() {
        return value;
    }

    @Override
    public String toJson(int indent) {
        return toJson();
    }

    @Override
    public int asInt() {
        return Integer.parseInt(value);
    }

    @Override
    public long asLong() {
        return Long.parseLong(value);
    }

    @Override
    public float asFloat() {
        return Float.parseFloat(value);
    }

    @Override
    public double asDouble() {
        return Double.parseDouble(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof JsonNumber other)) {
            return false;
        }

        return value.equals(other.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}