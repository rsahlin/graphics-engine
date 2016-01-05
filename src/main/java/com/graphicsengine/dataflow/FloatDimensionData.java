package com.graphicsengine.dataflow;

import com.google.gson.annotations.SerializedName;

/**
 * Data for a dimension, this can be 1,2 or 3 dimensions, use this when importing or exporting.
 * Use the axis values to index
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public class FloatDimensionData {

    @SerializedName("data")
    private float[] data;

    /**
     * Creates a new float dimension based on the source
     * 
     * @param dimension
     */
    public FloatDimensionData(float[] dimension) {
        set(dimension);
    }

    /**
     * Sets the dimension values from the source dimension, this object will have the same size as the source
     * 
     * @param dimension
     */
    public void set(float[] dimension) {
        data = new float[dimension.length];
        System.arraycopy(dimension, 0, dimension, 0, dimension.length);
    }

    public float[] getDimension() {
        return data;
    }

}
