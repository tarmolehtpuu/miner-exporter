package ee.moo.miner.exporter.dataformat.json;

import java.util.List;
import java.util.Set;

public abstract sealed class JsonValue permits
    JsonObject,
    JsonArray,
    JsonString,
    JsonNumber,
    JsonBoolean,
    JsonNull {

    public abstract JsonType getType();

    public abstract String toJson();

    public abstract String toJson(int indent);

    public boolean isObject() {
        return getType().isObject();
    }

    public boolean isArray() {
        return getType().isArray();
    }

    public boolean isString() {
        return getType().isString();
    }

    public boolean isNumber() {
        return getType().isNumber();
    }

    public boolean isBoolean() {
        return getType().isBoolean();
    }

    public boolean isNull() {
        return getType().isNull();
    }

    public JsonObject asObject() {
        throw new JsonException("Expecting %s, got: %s", JsonType.JSON_OBJECT, getType());
    }

    public List<JsonValue> asList() {
        throw new JsonException("Expecting %s, got: %s", JsonType.JSON_ARRAY, getType());
    }

    public <T> List<T> asList(Class<T> cls) {
        throw new JsonException("Expecting %s, got: %s", JsonType.JSON_ARRAY, getType());
    }

    public Set<JsonValue> asSet() {
        throw new JsonException("Expecting %s, got: %s", JsonType.JSON_ARRAY, getType());
    }

    public <T> Set<T> asSet(Class<T> cls) {
        throw new JsonException("Expecting %s, got: %s", JsonType.JSON_ARRAY, getType());
    }

    public int asInt() {
        throw new JsonException("Expecting %s, got: %s", JsonType.JSON_NUMBER, getType());
    }

    public long asLong() {
        throw new JsonException("Expecting %s, got: %s", JsonType.JSON_NUMBER, getType());
    }

    public float asFloat() {
        throw new JsonException("Expecting %s, got: %s", JsonType.JSON_NUMBER, getType());
    }

    public double asDouble() {
        throw new JsonException("Expecting %s, got: %s", JsonType.JSON_NUMBER, getType());
    }

    public Object asNull() {
        throw new JsonException("Expecting %s, got: %s", JsonType.JSON_NULL, getType());
    }

    public boolean asBoolean() {
        throw new JsonException("Expecting %s, got: %s", JsonType.JSON_BOOLEAN, getType());
    }

    public String asString() {
        throw new JsonException("Expecting %s, got: %s", JsonType.JSON_STRING, getType());
    }

    public <E extends Enum<E>> E asEnum(Class<E> cls) {
        throw new JsonException("Expecting %s, got: %s", JsonType.JSON_STRING, getType());
    }
}