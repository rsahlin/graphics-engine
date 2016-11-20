package com.graphicsengine.map;

import com.google.gson.annotations.SerializedName;
import com.nucleus.texturing.TiledTexture2D;

/**
 * Data needed for a {@linkplain PlayfieldNode}
 * This contains the map data and texture to be used with map.
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public class Playfield {
    @SerializedName("map")
    private Map map;
    @SerializedName("charMap")
    private TiledTexture2D charMap;
}
