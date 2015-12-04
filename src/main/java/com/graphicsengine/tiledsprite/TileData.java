package com.graphicsengine.tiledsprite;

import com.graphicsengine.dataflow.FloatDimensionData;
import com.nucleus.vecmath.Transform;

public class TileData {

    private int count;
    private FloatDimensionData dimension;
    private Transform transform;
    private String textureref;

    public int getCount() {
        return count;
    }

    public String getTextureref() {
        return textureref;
    }

    public FloatDimensionData getDimension() {
        return dimension;
    }

    public Transform getTransform() {
        return transform;
    }

}
