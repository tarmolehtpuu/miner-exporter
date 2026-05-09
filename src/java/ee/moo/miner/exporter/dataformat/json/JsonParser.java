package ee.moo.miner.exporter.dataformat.json;


public final class JsonParser {

    private final String input;

    private int pos;

    public JsonParser(String input) {
        if (input == null) {
            throw new JsonException("Cannot parse null input");
        }
        this.input = input;
    }

    public JsonValue parse() {
        skipWhitespace();
        var value = parseValue();
        skipWhitespace();
        if (pos < input.length()) {
            throw new JsonException("Unexpected trailing data at position %d: '%s'", pos, input.charAt(pos));
        }
        return value;
    }

    @SuppressWarnings("EnhancedSwitchMigration")
    public JsonValue parseValue() {
        skipWhitespace();
        char c = peek();

        switch (c) {
            case '"':
                return parseString();
            case '{':
                return parseObject();
            case '[':
                return parseArray();
            case 't':
                return parseLiteral("true", JsonBoolean.TRUE);
            case 'f':
                return parseLiteral("false", JsonBoolean.FALSE);
            case 'n':
                return parseLiteral("null", JsonNull.INSTANCE);
            default:
                if (c == '-' || Character.isDigit(c)) {
                    return parseNumber();
                }
                throw new JsonException("Unexpected character at position %d: '%s'", pos, c);
        }
    }

    private JsonObject parseObject() {
        var obj = new JsonObject();

        expect('{');
        skipWhitespace();

        if (peek() == '}') {
            next();
            return obj;
        }

        while (true) {
            skipWhitespace();
            var key = parseString();
            skipWhitespace();

            expect(':');
            skipWhitespace();
            obj.put(key.asString(), parseValue());
            skipWhitespace();

            char c = next();
            if (c == '}') {
                break;
            }
            if (c != ',') {
                throw new JsonException("Expected ',' or '}', got: %s at position %d", c, pos - 1);
            }
        }

        return obj;
    }

    private JsonArray parseArray() {
        var array = new JsonArray();

        expect('[');
        skipWhitespace();

        if (peek() == ']') {
            next();
            return array;
        }

        while (true) {
            skipWhitespace();
            array.add(parseValue());
            skipWhitespace();

            char c = next();
            if (c == ']') {
                break;
            }
            if (c != ',') {
                throw new JsonException("Expected ',' or ']', got: %s at position %d", c, pos - 1);
            }
        }

        return array;
    }

    private JsonValue parseLiteral(String expected, JsonValue value) {
        expect(expected);
        return value;
    }

    private JsonString parseString() {
        expect('"');
        var sb = new StringBuilder();

        while (true) {
            char c = next();

            if (c == '"') {
                break;
            }

            if (c == '\\') {
                c = next();
                switch (c) {
                    case '"':
                        sb.append('"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        if (pos + 4 > input.length()) {
                            throw new JsonException("Invalid Unicode escape at position: %d", pos);
                        }

                        var hex = input.substring(pos, pos + 4);
                        try {
                            sb.append((char) Integer.parseInt(hex, 16));
                        } catch (NumberFormatException e) {
                            throw new JsonException("Invalid Unicode escape sequence: \\u%s", hex);
                        }
                        pos += 4;
                        break;
                    default:
                        throw new JsonException("Invalid escape character at position %d: \\", pos - 1, c);
                }
            } else {
                sb.append(c);
            }
        }

        return new JsonString(sb.toString());
    }

    private JsonNumber parseNumber() {
        int start = pos;
        if (peek() == '-') {
            next();
        }

        while (Character.isDigit(peek())) {
            next();
        }

        if (peek() == '.') {
            next();
            while (Character.isDigit(peek())) {
                next();
            }
        }

        if (peek() == 'e' || peek() == 'E') {
            next();
            if (peek() == '+' || peek() == '-') {
                next();
            }

            while (Character.isDigit(peek())) {
                next();
            }
        }

        if (start == pos) {
            throw new JsonException("Expected number at position: %d", pos);
        }

        return new JsonNumber(input.substring(start, pos));
    }

    private void expect(char e) {
        char c = next();
        if (c != e) {
            throw new JsonException("Expected '%s', got '%s' at position %d", e, c, pos);
        }
    }

    private void expect(String e) {
        for (char c : e.toCharArray()) {
            expect(c);
        }
    }

    private char peek() {
        return pos < input.length() ? input.charAt(pos) : '\0';
    }

    private char next() {
        if (pos >= input.length()) {
            throw new JsonException("Unexpected end of input");
        }
        return input.charAt(pos++);
    }


    private void skipWhitespace() {
        while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
            pos++;
        }
    }
}