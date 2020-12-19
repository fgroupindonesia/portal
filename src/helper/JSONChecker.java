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

    public JSONChecker() {

    }

    public JSONChecker(String resp) {
        jsonObject = new JsonParser().parse(resp).getAsJsonObject();
        //System.out.println("val " + jsonObject.get("status").getAsString());
    }

    public boolean isValid(String resp) {

        jsonObject = new JsonParser().parse(resp).getAsJsonObject();
        //System.out.println("val " + jsonObject.get("status").getAsString());

        return jsonObject.get("status").getAsString().equals("valid");

    }

    public String getValueAsString(String keyName) {
        String val = null;

        try {

            if (jsonObject.has(keyName)) {

                if (jsonObject.get(keyName).isJsonArray()) {
                    val = jsonObject.get(keyName).getAsJsonArray().toString();
                } else if(jsonObject.get(keyName).isJsonObject()) {
                    val = jsonObject.get(keyName).getAsJsonObject().toString();
                }else {
                    val = jsonObject.get(keyName).getAsString();
                }

            }

        } catch (Exception ex) {
            System.out.println("error while obtaining string value");
        }

        return val;
    }

    public boolean getValueAsBoolean(String keyName) {

        boolean stat = false;

        try {

            if(jsonObject.has(keyName)){
                stat = jsonObject.get(keyName).getAsBoolean();
            }
            
        } catch (Exception ex) {

        }

        return stat;
    }

    public JsonObject getJSON() {
        return jsonObject;
    }

}
