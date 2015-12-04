package com.graphicsengine.dataflow;

import com.nucleus.vecmath.Axis;

/**
 * Data for a dimension, this can be 1,2 or 3 dimensions
 * Use the axis values to index
 * 
 * @author Richard Sahlin
 *
 */
public class IntDimensionData {

    private int[] data;

    public int[] getDimension() {
        return data;
    }

    /**
     * Returns the width of the dimension
     * 
     * @return
     * @throws NullPointerException If data has not been set
     */
    public int getWidth() {
        return data[Axis.WIDTH.index];
    }

    /**
     * Returns the height of the dimension
     * 
     * @return
     * @throws NullPointerException If data has not been set
     */
    public int getHeight() {
        return data[Axis.HEIGHT.index];
    }

}
