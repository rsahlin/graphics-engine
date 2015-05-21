package com.graphicsengine.json;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;

import com.graphicsengine.charset.PlayfieldFactory;
import com.graphicsengine.charset.PlayfieldSetup;
import com.graphicsengine.common.StringUtils;
import com.graphicsengine.dataflow.ArrayInput;
import com.nucleus.renderer.BaseRenderer;
import com.nucleus.texturing.TextureSetup;

/**
 * Utilities for CharMap to/from JSON
 * 
 * @author Richard Sahlin
 *
 */
public class JSONPlayfieldParser extends JSONParser {

    private final static String CHARMAP_KEY = "charmap";

    private final static int PLAYFIELD_DATA = 0;
    private final static int WIDTH = 1;
    private final static int HEIGHT = 2;
    private final static int XPOS = 3;
    private final static int YPOS = 4;
    private final static int ZPOS = 5;
    private final static int CHAR_WIDTH = 6;
    private final static int CHAR_HEIGHT = 7;
    private final static int TEXTURE_SOURCE = 8;
    private final static int TEXTURE_FRAMES_X = 9;
    private final static int TEXTURE_FRAMES_Y = 10;

    public JSONPlayfieldParser(BaseRenderer renderer) {
        super(renderer);
    }

    @Override
    public Object parseKey(Object jsonKey, JSONObject json, List<JSONObject> nodes) throws IOException {
        JSONObject JSONcharmap = lookupNodeForKey(jsonKey, CHARMAP_KEY, json, nodes);
        if (JSONcharmap == null) {
            return null;
        }
        PlayfieldSetup charmapData = getCharMapData(JSONcharmap, nodes);
        return PlayfieldFactory.createCharmap(renderer, charmapData);
    }

    /**
     * Provides the mapping between JSON data and the CharMapData class used to create a charmap.
     * 
     * @param charmap
     * @return
     */
    public PlayfieldSetup getCharMapData(JSONObject charmap, List<JSONObject> nodes) throws IOException {
        String[] data = StringUtils.getStringArray((String) charmap.get(DATA_KEY));
        return getCharMapData(data, nodes);
    }

    /**
     * Provides mapping between JSON data (as an array) and the CharMapData class used to create charmap.
     * 
     * @param data
     * @return
     */
    public static PlayfieldSetup getCharMapData(String[] data, List<JSONObject> nodes) throws IOException {
        // Fetch ref to input
        JSONObject jsonInput = JSONUtils.getObjectByKey(nodes, data[PLAYFIELD_DATA]);
        ArrayInput input = JSONArrayInputParser.parseArrayInput(jsonInput, nodes);
        PlayfieldSetup charmap = new PlayfieldSetup(data[WIDTH], data[HEIGHT], data[XPOS], data[YPOS],
                data[ZPOS],
                data[CHAR_WIDTH], data[CHAR_HEIGHT]);
        TextureSetup texSetup = JSONTextureParser.getTextureSetup(data[TEXTURE_SOURCE], nodes);
        charmap.setTextureSource(texSetup, data[TEXTURE_FRAMES_X], data[TEXTURE_FRAMES_Y]);
        charmap.setPlayFieldData(input);
        return charmap;
    }

    @Override
    public Object exportObject(Object obj) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
}
