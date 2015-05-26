package com.graphicsengine.json;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;

import com.graphicsengine.charset.TiledSetup;
import com.graphicsengine.common.StringUtils;
import com.nucleus.renderer.BaseRenderer;
import com.nucleus.texturing.TextureParameter;
import com.nucleus.texturing.TextureSetup;

public class JSONTextureParser extends JSONParser {

    private final static String TEXTURE_KEY = "texture2d";
    /**
     * Key for the texture parameters, MIN/MAG filter and texture wrap s/t
     */
    private final static String TEXTURE_PARAMETER_KEY = "texparameter";

    private final static int SOURCENAME = 0;
    private final static int RESOLUTION = 1;
    private final static int LEVELS = 2;

    public JSONTextureParser(BaseRenderer renderer) {
        super(renderer);
    }

    @Override
    public Object parseKey(Object jsonKey, JSONObject json, List<JSONObject> nodes) throws IOException {
        String name = getValueAsString(jsonKey, TEXTURE_KEY, json);
        if (name == null) {
            return null;
        }
        JSONObject JSONTexture = JSONUtils.getObjectByKey(nodes, name);
        System.out.println(JSONTexture);
        return JSONTexture;

    }

    /**
     * Returns the texture setup from the JSONObject, the object MUST be a texture object
     * 
     * @param jsonTexture
     * @return
     */
    public static TextureSetup createTextureSetup(JSONObject jsonTexture) {
        TextureSetup texSetup = createTextureSetup(StringUtils.getStringArray((String) jsonTexture.get(DATA_KEY)), 0);
        TextureParameter params = getTextureParameters(jsonTexture);
        if (params != null) {
            texSetup.setTextureParameter(params);
        }
        return texSetup;
    }

    /**
     * Fetches the texture parameter object, if set, and returns TextureParameter - or null if texture parameters not
     * set.
     * 
     * @param jsonTexture
     * @return Texture parameter object or null if not set.
     */
    public static TextureParameter getTextureParameters(JSONObject jsonTexture) {
        String texParams = (String) jsonTexture.get(TEXTURE_PARAMETER_KEY);
        if (texParams == null) {
            return null;
        }
        String[] data = StringUtils.getStringArray(texParams);
        TextureParameter params = new TextureParameter();
        params.importData(data, 0);
        return params;
    }

    public static TextureSetup createTextureSetup(String[] data, int offset) {
        TextureSetup texdata = new TextureSetup();
        texdata.importData(data, offset);

        return texdata;
    }

    /**
     * Creates the texture setup and sets it in the tiled setup class.
     * 
     * @param tiled
     * @param nodes
     * @return The TiledSetup containing the texture setup
     */
    public static TiledSetup createTextureSetup(TiledSetup tiled, List<JSONObject> nodes) {

        JSONObject tex = JSONUtils.getObjectByKey(nodes, tiled.getTextureRef());
        TextureSetup texSetup = JSONTextureParser.createTextureSetup(tex);
        tiled.setTexture(texSetup);
        return tiled;

    }

    @Override
    public Object exportObject(Object obj) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

}
