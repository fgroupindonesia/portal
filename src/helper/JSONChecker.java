/*
 *  This is a Portal Access for Client & Admin Usage
 *  (c) FGroupIndonesia, 2020.
 */
package helper;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 *
 * @author ASUS
 */
public class JSONChecker {

    private JsonObject jsonObject;
    private JsonArray jsonArray;

    public JSONChecker() {

    }

    public JSONChecker(String resp) {
        try {
            jsonObject = new JsonParser().parse(resp).getAsJsonObject();
            System.out.println("This is jsonobject");
        } catch (Exception ex) {
            jsonArray = new JsonParser().parse(resp).getAsJsonArray();
            System.out.println("This is jsonarray");
        }
        //System.out.println("val " + jsonObject.get("status").getAsString());
    }

    public boolean isValid(String resp) {

        jsonObject = new JsonParser().parse(resp).getAsJsonObject();
        //System.out.println("val " + jsonObject.get("status").getAsString());

        return jsonObject.get("status").getAsString().equals("valid");

    }

    public JsonObject getArrayValue(int i) {
        //jsonArray = jsonObject.getAsJsonArray();
        if (jsonArray != null) {
            return jsonArray.get(i).getAsJsonObject();
        }

        return null;
    }

    public String getValueAsString(String keyName) {
        String val = null;

        try {

            if (jsonObject.has(keyName)) {

                if (jsonObject.get(keyName).isJsonArray()) {
                    val = jsonObject.get(keyName).getAsJsonArray().toString();
                } else if (jsonObject.get(keyName).isJsonObject()) {
                    val = jsonObject.get(keyName).getAsJsonObject().toString();
                } else {
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

            if (jsonObject.has(keyName)) {
                stat = jsonObject.get(keyName).getAsBoolean();
            }

        } catch (Exception ex) {

        }

        return stat;
    }

    public JsonObject getJSON() {
        return jsonObject;
    }

    public boolean isObject() {
        if(jsonObject!=null){
        return jsonObject.isJsonObject();
        } 
        
        return false;
    }

    public boolean isArray() {
        boolean stat = false;
        
        if(jsonArray!=null){
            stat = true;
        }
        
        return stat;

    }

}
