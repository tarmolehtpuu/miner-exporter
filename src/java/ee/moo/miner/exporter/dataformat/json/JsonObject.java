package ee.moo.miner.exporter.dataformat.json;

import java.util.*;

public final class JsonObject extends JsonValue {

    private final Map<String, JsonValue> values = new LinkedHashMap<>();

    public JsonObject() {
    }

    @Override
    public JsonType getType() {
        return JsonType.JSON_OBJECT;
    }

    @Override
    public String toJson() {
        return toJson(0);
    }

    @Override
    public String toJson(int indent) {
        var sb = new StringBuilder();
        var keys = new ArrayList<>(values.keySet());

        String pad1 = "  ".repeat(indent);
        String pad2 = "  ".repeat(indent + 1);

        sb.append('{');

        if (!keys.isEmpty()) {
            sb.append('\n');

            for (int i = 0; i < keys.size(); i++) {
                var key = keys.get(i);
                var val = values.get(key);

                sb.append(pad2);
                sb.append('"');
                sb.append(key);
                sb.append('"');
                sb.append(':');
                sb.append(' ');
                sb.append(val.toJson(indent + 1));


                if (i < keys.size() - 1) {
                    sb.append(',');
                }

                sb.append('\n');
            }

            sb.append(pad1);
        }

        sb.append('}');

        return sb.toString();
    }

    @Override
    public JsonObject asObject() {
        return this;
    }

    public void put(String key, JsonValue value) {
        values.put(key, value);
    }

    public void put(String key, String value) {
        put(key, new JsonString(value));
    }

    public void put(String key, int value) {
        put(key, new JsonNumber(String.valueOf(value)));
    }

    public void put(String key, Set<?> value) {
        var data = new JsonArray();
        for (var item : value) {
            data.add(new JsonString(value.toString()));
        }

        put(key, data);
    }

    public void put(String key, List<?> value) {
        var data = new JsonArray();
        for (var item : value) {
            data.add(new JsonString(value.toString()));
        }

        put(key, data);
    }

    public JsonValue get(String key) {
        return values.get(key);
    }

    public List<JsonValue> getList(String key) {
        return Collections.unmodifiableList(values.get(key).asList());
    }

    public <T> List<T> getList(String key, Class<T> cls) {
        return values.get(key).asList(cls);
    }

    public Set<JsonValue> getSet(String key) {
        return Collections.unmodifiableSet(values.get(key).asSet());
    }

    public <T> Set<T> getSet(String key, Class<T> cls) {
        return values.get(key).asSet(cls);
    }

    public String getString(String key) {
        return values.get(key).asString();
    }

    public String getOptionalString(String key) {
        return values.containsKey(key)
            ? values.get(key).asString()
            : null;
    }

    public <E extends Enum<E>> E getEnum(String key, Class<E> cls) {
        return values.get(key).asEnum(cls);
    }

    public JsonObject getObject(String name) {
        return values.get(name).asObject();
    }

    public boolean getBoolean(String key) {
        return values.get(key).asBoolean();
    }

    public int getInt(String key) {
        return values.get(key).asInt();
    }

    public int getInt(String key, int defaultValue) {
        return values.containsKey(key)
            ? values.get(key).asInt()
            : defaultValue;
    }

    public long getLong(String key) {
        return values.get(key).asLong();
    }

    public float getFloat(String key) {
        return values.get(key).asFloat();
    }

    public double getDouble(String key) {
        return values.get(key).asDouble();
    }

    public Set<String> keySet() {
        return values.keySet();
    }

    public int size() {
        return values.size();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof JsonObject other)) {
            return false;
        }

        return values.equals(other.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        return values.toString();
    }
}