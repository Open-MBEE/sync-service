package org.openmbee.syncservice.core.utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JSONUtils {

    public JSONArray parseStringToJsonArray(String restResponse) {
        JSONArray jsonArr = null;

        jsonArr = new JSONArray(restResponse);
        if (jsonArr.length() == 0) {
            return null;
        }
        return jsonArr;
    }

    public List<String> convertJsonArrayToStringList(JSONArray jsonArray) {

        if (jsonArray == null)
            return null;

        List<String> strList = new ArrayList<String>();

        for (int inx = 0; inx < jsonArray.length(); inx++) {
            strList.add(String.valueOf(jsonArray.get(inx)));
        }
        return strList;
    }

    public String getStringFromArrayOfJSONObjects(JSONArray array, String key) {
        if(array == null) {
            return null;
        }
        for (int j = 0; j < array.length(); j++) {
            JSONObject arrayObj = array.getJSONObject(j);
            if(arrayObj.has(key)) {
                return arrayObj.getString(key);
            }
        }
        return null;
    }

    public Integer getIntFromArrayOfJSONObjects(JSONArray array, String key) {
        if(array == null) {
            return null;
        }
        for (int j = 0; j < array.length(); j++) {
            JSONObject arrayObj = array.getJSONObject(j);
            if(arrayObj.has(key)) {
                return arrayObj.getInt(key);
            }
        }
        return null;
    }

    public <V> List<V> flattenObjectArray(JSONArray array, String key) {
        if(array == null) {
            return null;
        }
        List<V> out = new ArrayList<>();
        for (int j = 0; j < array.length(); j++) {
            JSONObject arrayObj = array.getJSONObject(j);
            if(arrayObj.has(key)) {
                out.add((V)arrayObj.get(key));
            }
        }
        return out;
    }
}
