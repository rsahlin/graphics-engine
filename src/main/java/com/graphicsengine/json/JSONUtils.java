package com.graphicsengine.json;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.graphicsengine.common.StringUtils;

/**
 * Utilities for JSON, using org.json
 * 
 * @author Richard Sahlin
 *
 */
public class JSONUtils {

    /**
     * Utility method to return a list containing the array elements for the specified key.
     * 
     * @param fileName
     * @param arrayKey The key to return the json array as a List
     * @return
     */
    public static List<JSONObject> readJSONArrayByKey(InputStream is, String arrayKey) {

        try {
            ArrayList<JSONObject> jsonList = new ArrayList<JSONObject>();

            JSONObject jsonObject = (JSONObject) new JSONParser().parse(new InputStreamReader(is));

            JSONArray elements = (JSONArray) jsonObject.get(arrayKey);

            Iterator it = elements.iterator();
            while (it.hasNext()) {

                jsonList.add((JSONObject) it.next());
                /*
                 * JSONObject object = (JSONObject) it.next();
                 * Iterator objectIterator = object.entrySet().iterator();
                 * while (objectIterator.hasNext()) {
                 * Map.Entry pairs = (Map.Entry) objectIterator.next();
                 * String key = (String) pairs.getKey();
                 * jsonMap.put(key, pairs.getValue());
                 * objectIterator.remove();
                 * }
                 * it.remove();
                 */
            }
            return jsonList;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the first matching Object that is stored with the key.
     * 
     * @param array
     * @param key
     * @return The value for key, or null if not in array.
     */
    public static Object getFromArray(JSONArray array, String key) {

        for (Object o : array) {
            if (o instanceof JSONObject) {
                JSONObject json = (JSONObject) o;
                Object value = json.get(key);
                if (value != null) {
                    return value;
                }
            }
        }
        return null;
    }

    /**
     * Searches through the list of JSONObjects for an array with matching key.
     * 
     * @param list
     * @param name
     * @return Array stored with keys, or null if none found
     */
    public static JSONArray getArrayByKey(List<JSONObject> list, String key) {

        Object result = getByKey(list, key);
        if ((result != null) && (result instanceof JSONArray)) {
            return (JSONArray) result;
        }
        return null;
    }

    private static Object getByKey(List<JSONObject> list, String key) {
        Object result = null;
        for (JSONObject o : list) {
            result = o.get(key);
            if (result != null) {
                return result;
            }
        }
        return null;

    }

    /**
     * Returns the first object that is instanceof JSONObject and has key
     * 
     * @param list List of JSON
     * @param key The key to lookup object (value) for
     * @return The object for key, or null if not found
     */
    public static JSONObject getObjectByKey(List<JSONObject> list, String key) {

        Object result = getByKey(list, key);
        if ((result != null) && (result instanceof JSONObject)) {
            return (JSONObject) result;
        }
        return null;
    }

    /**
     * Searches for an value with the specified name and returns as int array, returns null if no array was found with
     * the specified name.
     * 
     * @param JSON
     * @param key
     * @return The int array, or null if none with matching name found
     */
    public static int[] getIntArray(List<Object> JSON, String key) {

        for (Object o : JSON) {
            if (o instanceof JSONArray) {
                return getIntArray((JSONArray) o);
            }
        }
        return null;
    }

    /**
     * Returns the JSONArray as an int array.
     * 
     * @param array
     * @return
     * @throws JSONException
     */
    public static int[] getIntArray(JSONArray array) {

        int[] result = new int[array.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = ((Long) array.get(i)).intValue();
        }
        return result;
    }

    public static String[] getStringArray(JSONObject obj, String key) {
        return StringUtils.getStringArray((String) obj.get(key));
    }
}
