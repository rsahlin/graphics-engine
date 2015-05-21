package com.graphicsengine.json;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;

import com.graphicsengine.charset.TiledSetup;
import com.graphicsengine.common.StringUtils;
import com.graphicsengine.dataflow.ArrayInput;
import com.nucleus.renderer.BaseRenderer;
import com.nucleus.texturing.TextureSetup;

/**
 * Utilities for TiledSpritesheet to/from JSON
 * 
 * @author Richard Sahlin
 *
 */
public class JSONTiledSpriteParser extends JSONParser {

    private final static String TILEDSPRITESHEET_KEY = "tiledspritesheet";

    private final static int TILEDSPRITE_DATA = 0;
    private final static int SPRITECOUNT = 1;
    private final static int SPRITE_WIDTH = 2;
    private final static int SPRITE_HEIGHT = 3;
    private final static int TEXTURE_SOURCE = 4;
    private final static int TEXTURE_FRAMES_X = 5;
    private final static int TEXTURE_FRAMES_Y = 6;

    public JSONTiledSpriteParser(BaseRenderer renderer) {
        super(renderer);
    }

    @Override
    public Object parseKey(Object jsonKey, JSONObject json, List<JSONObject> nodes) throws IOException {
        JSONObject JSONcharmap = lookupNodeForKey(jsonKey, TILEDSPRITESHEET_KEY, json, nodes);
        if (JSONcharmap == null) {
            return null;
        }
        TiledSetup tiledData = getCharMapData(JSONcharmap, nodes);
        return null;
    }

    /**
     * Provides the mapping between JSON data and the CharMapData class used to create a charmap.
     * 
     * @param charmap
     * @return
     */
    public TiledSetup getCharMapData(JSONObject charmap, List<JSONObject> nodes) throws IOException {
        String[] data = StringUtils.getStringArray((String) charmap.get(DATA_KEY));
        return getCharMapData(data, nodes);
    }

    /**
     * Provides mapping between JSON data (as an array) and the TiledSetup class used to create charmap.
     * 
     * @param data
     * @return
     */
    public static TiledSetup getCharMapData(String[] data, List<JSONObject> nodes) throws IOException {
        // Fetch ref to input
        JSONObject jsonInput = JSONUtils.getObjectByKey(nodes, data[TILEDSPRITE_DATA]);
        if (jsonInput != null) {
            ArrayInput input = JSONArrayInputParser.parseArrayInput(jsonInput, nodes);
        }
        TiledSetup tiled = new TiledSetup(data[SPRITECOUNT], data[SPRITE_WIDTH], data[SPRITE_HEIGHT]);
        TextureSetup texSetup = JSONTextureParser.getTextureSetup(data[TEXTURE_SOURCE], nodes);
        tiled.setTextureSource(texSetup, data[TEXTURE_FRAMES_X], data[TEXTURE_FRAMES_Y]);
        return tiled;
    }

    @Override
    public Object exportObject(Object obj) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
}
