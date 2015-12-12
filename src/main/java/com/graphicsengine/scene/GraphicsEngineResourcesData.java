package com.graphicsengine.scene;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.charset.PlayfieldData;
import com.graphicsengine.charset.TiledCharsetData;
import com.graphicsengine.tiledsprite.TiledSpriteControllerData;
import com.nucleus.scene.ResourcesData;

/**
 * Definition of all resources (for a scene)
 * 
 * @author Richard Sahlin
 *
 */
public class GraphicsEngineResourcesData extends ResourcesData {

    @SerializedName("tiledSpriteController")
    private TiledSpriteControllerData[] tiledSpriteController;
    @SerializedName("tiledCharset")
    private TiledCharsetData[] tiledCharset;
    @SerializedName("playfield")
    private PlayfieldData[] playfield;

    public TiledSpriteControllerData[] getTiledSpriteControllerData() {
        return tiledSpriteController;
    }

    /**
     * Returns the defined playfields
     * 
     * @return
     */
    public PlayfieldData[] getPlayfieldData() {
        return playfield;
    }

    /**
     * Returns the defined tiled charset objects
     * 
     * @return
     */
    public TiledCharsetData[] getTiledCharsetData() {
        return tiledCharset;
    }

    /**
     * Returns the (first) tiledcharset with matching id, or null if not found.
     * 
     * @param id
     * @return
     */
    public TiledCharsetData getTiledCharset(String id) {
        for (TiledCharsetData t : tiledCharset) {
            if (id.equals(t.getId())) {
                return t;
            }
        }
        return null;
    }

    /**
     * Returns the (first) playfield data with matching id, or null if not found.
     * 
     * @param id
     * @return
     */
    public PlayfieldData getPlayfieldData(String id) {
        for (PlayfieldData p : playfield) {
            if (id.equals(p.getId())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Returns the (first) tiledspritecontroller with matching id, or null if not found.
     * 
     * @param id
     * @return
     */
    public TiledSpriteControllerData getTiledSpriteController(String id) {
        for (TiledSpriteControllerData t : tiledSpriteController) {
            if (id.equals(t.getId())) {
                return t;
            }
        }
        return null;
    }

}
