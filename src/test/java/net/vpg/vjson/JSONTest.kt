package net.vpg.vjson

import net.vpg.vjson.parser.JSONParser.toJSON
import net.vpg.vjson.value.JSONArray
import net.vpg.vjson.value.JSONObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal object JSONTest {
    val url = JSONTest::class.java.getResource("example.json")!!
    val obj = JSONObject()
            .put("\"STRING\"", "Unicode: \uABCD, \r\n\u000c\b\\\"")
            .put(
                    "NUMBERS", JSONObject()
                    .put("INT", 123)
                    .put("NEGATIVE", -123)
                    .put("LONG", 1234567890)
                    .put("DOUBLE1", 1.010)
                    .put("DOUBLE2", 1.010e-5)
            )
            .put("BOOLEAN", true)
            .put("ARRAY", JSONArray().add(null))

    fun checkCorrectParse() {
        Assertions.assertEquals(obj.toString(), url.toJSON().toString())
    }

    @Test
    fun checkCorrectPrettyPrint() {
        Assertions.assertEquals(
                """
            {
              "\"STRING\"": "Unicode: ꯍ, \r\n\f\b\\\"",
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
            }
            """.trimIndent(), obj.toPrettyString()
        )
    }
}