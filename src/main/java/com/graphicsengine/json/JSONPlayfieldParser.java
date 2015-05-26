package com.graphicsengine.json;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;

import com.graphicsengine.charset.PlayfieldFactory;
import com.graphicsengine.charset.PlayfieldSetup;
import com.graphicsengine.common.StringUtils;
import com.graphicsengine.dataflow.ArrayInput;
import com.nucleus.renderer.BaseRenderer;

/**
 * Utilities for CharMap to/from JSON
 * 
 * @author Richard Sahlin
 *
 */
public class JSONPlayfieldParser extends JSONParser {

    private final static String CHARMAP_KEY = "charmap";

    public JSONPlayfieldParser(BaseRenderer renderer) {
        super(renderer);
    }

    @Override
    public Object parseKey(Object jsonKey, JSONObject json, List<JSONObject> nodes) throws IOException {
        JSONObject JSONcharmap = lookupNodeForKey(jsonKey, CHARMAP_KEY, json, nodes);
        if (JSONcharmap == null) {
            return null;
        }
        PlayfieldSetup playfieldSetup = new PlayfieldSetup();
        getSetup(JSONcharmap, nodes, playfieldSetup);
        return PlayfieldFactory.create(renderer, playfieldSetup);
    }

    /**
     * Provides the mapping between JSON data and the CharMapData class used to create a charmap.
     * 
     * @param charmap
     * @return
     */
    public void getSetup(JSONObject charmap, List<JSONObject> nodes, PlayfieldSetup playfieldSetup) throws IOException {
        String[] data = StringUtils.getStringArray((String) charmap.get(DATA_KEY));
        getSetup(data, nodes, playfieldSetup);
    }

    /**
     * Provides mapping between JSON data (as an array) and the CharMapData class used to create charmap.
     * 
     * @param data
     * @return
     */
    public void getSetup(String[] data, List<JSONObject> nodes, PlayfieldSetup playfieldSetup) throws IOException {
        super.getSetup(data, nodes, playfieldSetup);
        JSONObject jsonInput = JSONUtils.getObjectByKey(nodes, playfieldSetup.getPlayfieldSource());
        if (jsonInput != null) {
            ArrayInput input = JSONArrayInputParser.parseArrayInput(jsonInput, nodes);

            playfieldSetup.setPlayFieldData(input);
        }
    }

    @Override
    public Object exportObject(Object obj) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
}
