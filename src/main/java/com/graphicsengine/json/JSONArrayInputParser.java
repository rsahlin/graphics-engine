package com.graphicsengine.json;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;

import com.graphicsengine.common.StringUtils;
import com.graphicsengine.dataflow.ArrayInput;

/**
 * Parses JSON data into ArrayInput
 * 
 * @author Richard Sahlin
 *
 */
public class JSONArrayInputParser {

    private final static String ARRAY_INPUT_KEY = "arrayinput";
    private final static String SIZE_KEY = "size";
    private final static String DATA_KEY = "data";

    private final static int COMPONENTS = 0;
    private final static int LINEWIDTH = 1;
    private final static int HEIGHT = 2;
    private final static int TYPE = 3;

    /**
     * Utility method to parse the array input
     * 
     * @param json
     * @param nodes
     * @return
     * @throws IOException
     */
    public static ArrayInput parseArrayInput(JSONObject json, List<JSONObject> nodes) throws IOException {
        JSONObject jsonArray = (JSONObject) json.get(ARRAY_INPUT_KEY);
        if (jsonArray == null) {
            return null;
        }
        String[] size = StringUtils.getStringArray((String) jsonArray.get(SIZE_KEY));
        String data = (String) jsonArray.get(DATA_KEY);
        ArrayInput arrayInput = new ArrayInput(size[COMPONENTS], size[LINEWIDTH], size[HEIGHT], size[TYPE], data);
        return arrayInput;
    }
}
