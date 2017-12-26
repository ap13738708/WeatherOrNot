package com.example.weather2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by P on 12/15/2017.
 */

public class JSONInput {
    String json;
    ArrayList<JSONObject> list ;
    public int getArrayListSize() {
        return list.size();
    }
    public JSONInput(String json) {
        this.json = json;
    }
    public void setJsonArray(String key1){
        String converter = null;
        list = new ArrayList<JSONObject>();
        try {
            JSONObject jsonObject = new JSONObject(this.json);
            converter = jsonObject.getString(key1);
            JSONArray jsonArray = new JSONArray(converter);
            for (int i=0; i<jsonArray.length();i++){
                JSONObject jsonPart = jsonArray.getJSONObject(i);
                list.add(jsonPart);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getJsonFromArrayList(String key1,String key2, int j){
        String result = null;
        String converter = null;
        JSONObject jsonObject = list.get(j);
        try {
            converter = jsonObject.getString(key1);
            JSONArray jsonArray = new JSONArray(converter);
            for (int i=0; i<jsonArray.length();i++) {
                JSONObject jsonPart = jsonArray.getJSONObject(i);
                result = jsonPart.getString(key2);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Not Found";
    }
    public String getJsonFromArrayListFromObject(String key1,String key2, int j){
        String result = null;
        String converter = null;
        JSONObject jsonObject = list.get(j);
        try {
            converter = jsonObject.getString(key1);
            JSONObject jsonObject2 = new JSONObject(converter);
            result = jsonObject2.getString(key2);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "Not Found";
    }
    public String getJsonObjectFromJsonObject(String key1, String key2){
        String result = null;
        String converter = null;
        try {
            JSONObject fullJsonObject = new JSONObject(this.json);
            converter = fullJsonObject.getString(key1);
            JSONObject targetJsonObject = new JSONObject(converter);
            result = targetJsonObject.getString(key2);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getJsonObject(String key) {
        String result = null;
        try {
            JSONObject jsonObject = new JSONObject(this.json);
            result = jsonObject.getString(key);
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getJsonObjectFromJsonArray(String key1, String key2){
        String result = null;
        String converter = null;
        try {
            JSONObject jsonObject = new JSONObject(this.json);
            converter = jsonObject.getString(key1);
            JSONArray jsonArray = new JSONArray(converter);
            for (int i=0; i<jsonArray.length(); i++){
                JSONObject jsonPart = jsonArray.getJSONObject(i);
                result = jsonPart.getString(key2);
            }
            return  result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getJsonObjectFromJsonArray(String key1, String key2, int j){
        String result = null;
        String converter = null;
        try {
            JSONObject jsonObject = new JSONObject(this.json);
            converter = jsonObject.getString(key1);
            JSONArray jsonArray = new JSONArray(converter);
            for (int i=0; i<jsonArray.length(); i++){
                JSONObject jsonPart = jsonArray.getJSONObject(i);
                result = jsonPart.getString(key2);
                if(i == j) {
                    break;
                }
            }
            return  result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String kelvinToCelcius(String kelvin){
        String result = "";
        double inputKelvin = Double.parseDouble(kelvin);
        inputKelvin = inputKelvin - 273;
        inputKelvin = Math.round(inputKelvin*10)/10;
        return result + inputKelvin;
    }
}
