package net.vplaygames.vjson;

import net.vplaygames.vjson.value.JSONArray;
import net.vplaygames.vjson.value.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

class JSONTest {
    static URL url;
    static File file;
    static String string;
    static JSONObject obj;

    @BeforeAll
    static void setUp() throws IOException {
        url = JSONTest.class.getResource("example.json");
        file = new File(url.getFile());
        string = String.join("\n", Files.readAllLines(file.toPath()));
        obj = new JSONObject()
            .put("\"STRING\"", "Unicode: \uABCD, \r\n\f\b\\\"")
            .put("INT", 123)
            .put("LONG", 1234567890)
            .put("DOUBLE", 1.001)
            .put("BOOLEAN", true)
            .put("ARRAY", new JSONArray().add(null))
        ;
    }

    @Test
    void parseFromURL() throws IOException {
        Assertions.assertEquals(JSONObject.parse(url), obj);
    }

    @Test
    void parseFromFile() throws IOException {
        Assertions.assertEquals(JSONObject.parse(file), obj);
    }

    @Test
    void parseFromString() {
        Assertions.assertEquals(JSONObject.parse(string), obj);
    }

    boolean validate(JSONObject jo) {
        return jo.equals(obj);
    }
}