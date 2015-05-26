package com.graphicsengine.json;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;

import com.graphicsengine.charset.TiledSetup;
import com.graphicsengine.common.StringUtils;
import com.graphicsengine.sprite.SpriteControllerSetup;
import com.nucleus.renderer.NucleusRenderer;

public abstract class JSONParser {

    /**
     * Store the data for an object in this key.
     */
    protected final static String DATA_KEY = "data";
    protected final static String LOGIC_DATA_KEY = "logic-data";
    protected final static String TILE_DATA_KEY = "tile-data";

    protected NucleusRenderer renderer;

    public JSONParser(NucleusRenderer renderer) {
        this.renderer = renderer;
    }

    /**
     * 
     * Check the jsonKey for a type that is understood by the parser and create the object corresponding.
     * 
     * @param jsonKey
     * @param json
     * @param nodes
     * @return The object to create for jsonKey or null if not handled by this parser
     * @throws IOException
     */
    public abstract Object parseKey(Object jsonKey, JSONObject json, List<JSONObject> nodes) throws IOException;

    /**
     * Exports objects and nodes to JSON
     * 
     * @param obj The object to export.
     * @return The exported object
     * @throws IOException
     */
    public abstract Object exportObject(Object obj) throws IOException;

    /**
     * Parse the JSON object, parsing all keys and returning an object for the data if the data is understood.
     * Otherwise null is returned.
     * 
     * @param json
     * @param nodes
     * @return
     * @throws IOException If there is an error reading JSONdata
     */
    public Object parse(JSONObject json, List<JSONObject> nodes) throws IOException {
        for (Object o : json.keySet()) {
            Object result = parseKey(o, json, nodes);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    /**
     * Check if the object is String and is equal.
     * 
     * @param jsonKey The JSON key
     * @param key String key to check for, normally the node object identifiers, eg charmap, texture2d etc
     * @return True if jsonKey is String and equals key, false otherwise
     */
    protected boolean isKey(Object jsonKey, String key) {
        if (!(jsonKey instanceof String)) {
            return false;
        }
        String type = (String) jsonKey;
        if (type.equals(key)) {
            return true;
        }
        return false;
    }

    /**
     * Utility method, checks if jsonKey and key is same, if they are then the value is returned from JSONObject using
     * the key.
     * 
     * @param jsonKey jsonKey from object iterator
     * @param key Check if jsonKey is same as this
     * @param jsonObject Return value from this object if key and jsonKey is same.
     * @return The value for key from jsonObject, if jsonKey and key are the same. Otherwise null.
     */
    protected String getValueAsString(Object jsonKey, String key, JSONObject jsonObject) {
        if (isKey(jsonKey, key)) {
            return (String) jsonObject.get(key);
        }
        return null;
    }

    /**
     * Checks if jsonKey and key is the same, then returns the node (JSONObject) for the key.
     * 
     * @param jsonKey json key
     * @param key Key to compare with jsonKey
     * @param jsonObject Return value from this object if key and jsonKey is same
     * @return Node (jsonobject) with same name as value for key.
     */
    public JSONObject getValueAsJSON(Object jsonKey, String key, JSONObject jsonObject) {
        if (isKey(jsonKey, key)) {
            return (JSONObject) jsonObject.get(key);
        }
        return null;
    }

    /**
     * Checks if jsonKey and key is the same, then searches for the JSONObject of matching name from
     * the nodes list.
     * Use this method when parsing JSON nodes.
     * 
     * @param jsonKey json key
     * @param key Key to compare with jsonKey
     * @param jsonObject JsonObject containing value for key, this value is used to find matching node.
     * @param nodes List of nodes
     * @return Node (jsonobject) with same name as value for key.
     */
    public JSONObject lookupNodeForKey(Object jsonKey, String key, JSONObject jsonObject, List<JSONObject> nodes) {
        String name = getValueAsString(jsonKey, key, jsonObject);
        if (name == null) {
            return null;
        }
        return JSONUtils.getObjectByKey(nodes, name);
    }

    /**
     * Provides the mapping between JSON data and the tiled charmap data using the TiledSetup class.
     * 
     * @param charmap
     * @param nodes
     * @param tiledSetup The object where the imported data is stored.
     * @return
     */
    public void getSetup(JSONObject charmap, List<JSONObject> nodes, TiledSetup tiledSetup)
            throws IOException {
        String[] data = StringUtils.getStringArray((String) charmap.get(TILE_DATA_KEY));
        getSetup(data, nodes, tiledSetup);
    }

    /**
     * Provides mapping between JSON data (as an array) and the TiledSetup class used to create tiled charmap.
     * 
     * @param data
     * @param nodes
     * @param tiledSetup The object where the imported data is stored.
     * @return
     */
    public void getSetup(String[] data, List<JSONObject> nodes, TiledSetup tiledSetup)
            throws IOException {
        tiledSetup.importData(data, 0);
        JSONTextureParser.createTextureSetup(tiledSetup, nodes);
    }

    public void getSetup(JSONObject spriteController, List<JSONObject> nodes, SpriteControllerSetup controllerSetup)
            throws IOException {
        String[] data = StringUtils.getStringArray((String) spriteController.get(LOGIC_DATA_KEY));
        getSetup(data, nodes, controllerSetup);
    }

    public void getSetup(String[] data, List<JSONObject> nodes, SpriteControllerSetup controllerSetup)
            throws IOException {
        controllerSetup.importData(data, 0);
    }

}
