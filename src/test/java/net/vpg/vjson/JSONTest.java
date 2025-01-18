package net.vpg.vjson;

import net.vpg.vjson.value.JSONArray;
import net.vpg.vjson.value.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

class JSONTest {
    static URL url;
    static JSONObject obj;

    @BeforeAll
    static void setUp() {
        url = JSONTest.class.getResource("example.json");
        obj = new JSONObject()
            .put("\"STRING\"", "Unicode: \uABCD, \r\n\f\b\\\"")
            .put("NUMBERS", new JSONObject()
                .put("INT", 123)
                .put("NEGATIVE", -123)
                .put("LONG", 1234567890)
                .put("DOUBLE1", 1.010)
                .put("DOUBLE2", 1.010e-5))
            .put("BOOLEAN", true)
            .put("ARRAY", new JSONArray().add(null))
        ;
    }

    @Test
    void checkCorrectParse() throws IOException {
        Assertions.assertEquals(obj.toString(), JSONObject.parse(url).toString());
    }

    @Test
    void checkCorrectPrettyPrint() {
        Assertions.assertEquals("""
            {
              "\\"STRING\\"": "Unicode: ꯍ, \\r\\n\\f\\b\\\\\\"",
              "NUMBERS": {
                "INT": 123,
                "NEGATIVE": -123,
                "LONG": 1234567890,
                "DOUBLE1": 1.01,
                "DOUBLE2": 1.01E-5
              },
              "BOOLEAN": true,
              "ARRAY": [
                null
              ]
            }""", obj.toPrettyString());
    }
}