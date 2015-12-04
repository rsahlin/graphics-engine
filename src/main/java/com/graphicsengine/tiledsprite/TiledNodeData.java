package com.graphicsengine.tiledsprite;

import com.nucleus.scene.NodeData;

/**
 * The base instance of a tiled object.
 * 
 * @author Richard Sahlin
 *
 */
public class TiledNodeData extends NodeData {

    private TileData tiledata;

    public TileData getTileData() {
        return tiledata;
    }

}
