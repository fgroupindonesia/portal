/*
 *  This is a Supplemental File from the Main Project used
 *  in Java Programming Core Fundamental I
 *  with FGroupIndonesia team.
 */
package helper;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 *
 * @author ASUS
 */
public class JSONChecker {

    private JsonObject jsonObject;

    public boolean isValid(String resp) {

        jsonObject = new JsonParser().parse(resp).getAsJsonObject();
        //System.out.println("val " + jsonObject.get("status").getAsString());

        return jsonObject.get("status").getAsString().equals("valid");

    }

    public String getValueAsString(String keyName) {
        String val = null;

        if (jsonObject.has(keyName)) {

            if (jsonObject.get(keyName).isJsonArray()) {
                val = jsonObject.get(keyName).getAsJsonArray().toString();
            } else {
                val = jsonObject.get(keyName).getAsJsonObject().toString();
            }

        }

        return val;
    }

    public boolean getValueAsBoolean(String keyName) {
        return jsonObject.get(keyName).getAsBoolean();
    }

    public JsonObject getJSON() {
        return jsonObject;
    }

}
