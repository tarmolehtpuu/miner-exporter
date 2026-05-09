package ee.moo.miner.exporter.dataformat.json;


import ee.moo.miner.exporter.util.StringUtil;

public final class JsonString extends JsonValue {

    private final String value;

    public JsonString(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    @Override
    public JsonType getType() {
        return JsonType.JSON_STRING;
    }

    @Override
    public String toJson() {
        return String.format("\"%s\"", value);
    }

    @Override
    public String toJson(int indent) {
        return toJson();
    }

    @Override
    public String asString() {
        return value;
    }

    @Override
    public double asDouble() {
        return Double.parseDouble(value);
    }

    @Override
    public int asInt() {
        return Integer.parseInt(value);
    }

    @Override
    public <E extends Enum<E>> E asEnum(Class<E> cls) {
        for (E constant : cls.getEnumConstants()) {
            if (StringUtil.equals(constant.name(), value)) {
                return constant;
            }
        }

        throw new JsonException("Invalid enum value: '%s' for type '%s'", value, cls.getName());
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof JsonString other)) {
            return false;
        }

        return StringUtil.equals(this.value, other.value);
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