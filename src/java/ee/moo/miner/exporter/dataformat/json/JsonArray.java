package ee.moo.miner.exporter.dataformat.json;

import java.util.*;

public final class JsonArray extends JsonValue {

    private final List<JsonValue> values = new ArrayList<>();

    public JsonArray() {
    }

    public JsonArray(List<JsonValue> values) {
        if (values != null) {
            this.values.addAll(values);
        }
    }

    @Override
    public JsonType getType() {
        return JsonType.JSON_ARRAY;
    }

    @Override
    public String toJson() {
        return toJson(0);
    }

    @Override
    public String toJson(int indent) {
        var sb = new StringBuilder();
        var pad1 = "  ".repeat(indent);
        var pad2 = "  ".repeat(indent + 1);

        sb.append('[');

        if (!values.isEmpty()) {
            sb.append('\n');

            for (int i = 0; i < values.size(); i++) {
                sb.append(pad2);
                sb.append(values.get(i).toJson(indent + 1));

                if (i < values.size() - 1) {
                    sb.append(',');
                }

                sb.append('\n');
            }

            sb.append(pad1);
        }

        sb.append(']');

        return sb.toString();
    }

    public void add(JsonValue v) {
        values.add(v);
    }

    @Override
    public List<JsonValue> asList() {
        return Collections.unmodifiableList(values);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> asList(Class<T> cls) {
        var result = new ArrayList<T>(values.size());

        for (JsonValue value : values) {
            if (cls == String.class) {
                result.add((T) value.asString());

            } else if (cls == Integer.class || cls == int.class) {
                result.add((T) Integer.valueOf(value.asInt()));

            } else if (cls == Long.class || cls == long.class) {
                result.add((T) Long.valueOf(value.asLong()));

            } else if (cls == Float.class || cls == float.class) {
                result.add((T) Float.valueOf(value.asFloat()));

            } else if (cls == Double.class || cls == double.class) {
                result.add((T) Double.valueOf(value.asDouble()));

            } else if (cls == Boolean.class || cls == boolean.class) {
                result.add((T) Boolean.valueOf(value.asBoolean()));

            } else if (JsonValue.class.isAssignableFrom(cls)) {
                if (!cls.isInstance(value)) {
                    throw new JsonException("Expected %s but got %s", cls.getName(), value.getClass().getName());
                }
                result.add((T) value);

            } else {
                throw new JsonException("Unsupported list element type: %s", cls.getName());

            }
        }

        return result;
    }

    @Override
    public Set<JsonValue> asSet() {
        return new HashSet<>(asList());
    }

    @Override
    public <T> Set<T> asSet(Class<T> cls) {
        return new HashSet<>(asList(cls));
    }
}
