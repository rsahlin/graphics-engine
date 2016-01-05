package com.graphicsengine.dataflow;

import com.nucleus.scene.NodeData;
import com.nucleus.types.DataType;

public class ArrayInputData extends NodeData {

    private int components;
    private IntDimensionData size;
    private int[] data;
    private DataType dataType;

    public int getComponents() {
        return components;
    }

    public IntDimensionData getSize() {
        return size;
    }

    public int[] getData() {
        return data;
    }

    public DataType getDataType() {
        return dataType;
    }

    /**
     * Copies the data in this array to the source int array.
     * 
     * @param dest
     * @param lineWidth
     * @param height
     * @param xpos
     * @param ypos
     */
    public void copyArray(int[] dest, int lineWidth, int height, int xpos, int ypos) {
        switch (dataType) {
        case INT:
            copyArray(data, dest, lineWidth, height, xpos, ypos);
            break;
        default:
            throw new IllegalArgumentException("Not implemented support for " + dataType);
        }
    }

    /**
     * Copy data from source to dest, using lineWidth for destination and this.lineWidth for source, this means
     * copying data FROM this class TO another.
     * 
     * @param source Source array, will be updated with this.lineWidth
     * @param dest Destination array, will be updated by lineWidth
     * @param lineWidth Width of one line in destination
     * @param height Number of lines to copy
     * @param xpos
     * @param ypos
     */
    protected void copyArray(int[] source, int[] dest, int lineWidth, int height, int xpos, int ypos) {
        height = Math.min(height, size.getHeight());
        int w = Math.min(lineWidth, size.getWidth());
        int sourceIndex = 0;
        int destIndex = calcStartIndex(xpos, ypos);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < w; x++) {
                dest[destIndex + x] = source[sourceIndex + x];
            }
            destIndex += lineWidth;
            sourceIndex += size.getWidth();
        }
    }

    /**
     * Calculate the start index using x and y offset, making sure that the startposition is not outside destination.
     * 
     * @return Destination start index
     */
    private int calcStartIndex(int xoffset, int yoffset) {
        return size.getWidth() * Math.min(yoffset, size.getHeight())
                + Math.min(xoffset, size.getWidth());
    }

}
