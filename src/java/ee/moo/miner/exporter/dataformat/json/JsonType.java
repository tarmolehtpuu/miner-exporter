package ee.moo.miner.exporter.dataformat.json;


public enum JsonType {
    JSON_ARRAY,
    JSON_BOOLEAN,
    JSON_NULL,
    JSON_NUMBER,
    JSON_OBJECT,
    JSON_STRING;

    public boolean isArray() {
        return this == JSON_ARRAY;
    }

    public boolean isBoolean() {
        return this == JSON_BOOLEAN;
    }

    public boolean isNull() {
        return this == JSON_NULL;
    }

    public boolean isNumber() {
        return this == JSON_NULL;
    }

    public boolean isObject() {
        return this == JSON_OBJECT;
    }

    public boolean isString() {
        return this == JSON_STRING;
    }
}