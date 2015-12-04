package com.graphicsengine.charset;

import com.graphicsengine.dataflow.IntDimensionData;
import com.graphicsengine.tiledsprite.TiledNodeData;
import com.nucleus.vecmath.Axis;

/**
 * The tiled charset data, this is used to create a playfield.
 * 
 * @author Richard Sahlin
 *
 */
public class TiledCharsetData extends TiledNodeData {

    private String source;
    private IntDimensionData mapDimension;
    private int[] mapData;

    /**
     * Returns the source data for the charmap.
     * 
     * @return
     */
    public String getSource() {
        return source;
    }

    public IntDimensionData getMapDimension() {
        return mapDimension;
    }

    public int[] getMapData() {
        return mapData;
    }

    /**
     * Allocates the array to hold the map data, uses the map dimension.
     * 
     */
    public void createMapData() {
        mapData = new int[mapDimension.getDimension()[Axis.WIDTH.index]
                * mapDimension.getDimension()[Axis.HEIGHT.index]];
    }
}
