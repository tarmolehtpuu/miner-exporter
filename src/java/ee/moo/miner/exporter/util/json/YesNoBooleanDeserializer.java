package ee.moo.miner.exporter.util.json;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import ee.moo.miner.exporter.util.StringUtil;

import java.io.IOException;

public class YesNoBooleanDeserializer extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
        String value = p.getValueAsString();

        if (StringUtil.equalsIgnoreCase("Y", value)) {
            return Boolean.TRUE;
        }

        if (StringUtil.equalsIgnoreCase("N", value)) {
            return Boolean.FALSE;
        }

        throw new IOException(String.format("Unexcpected value for boolean (expected Y/N): %s", value));
    }
}
