package com.graphicsengine.dataflow;

import com.nucleus.common.StringUtils;
import com.nucleus.types.DataType;

/**
 * Dataflow for an input array source.
 * This is used to provide data for scene components
 * 
 * @author Richard Sahlin
 *
 */
public class ArrayInput {

    private final static String NOT_IMPLEMENTED_ERROR = "Not implemented support for: ";

    /**
     * Number of components, 2 for x and y
     */
    int components;
    /**
     * Width of one line of data
     * This can be used if the destination array has not the same width to calculate modulo.
     */
    int lineWidth;
    /**
     * Number of lines of data.
     */
    int height;
    /**
     * FLOAT, INT or SHORT arrays
     */
    DataType type;
    /**
     * Array containing the data, possible arrays are float[], int[] and short[]
     */
    Object data;

    /**
     * Creates a new ArrayInput with the specified properties and data.
     * 
     * @param components
     * @param lineWidth
     * @param height
     * @param type
     * @param data
     */
    public ArrayInput(int components, int lineWidth, int height, DataType type, Object data) {
        this.components = components;
        this.lineWidth = lineWidth;
        this.height = height;
        this.type = type;
        this.data = data;
    }

    /**
     * Creates a new ArrayInput with the specified properties and data from Strings.
     * 
     * @param components
     * @param lineWidth
     * @param height
     * @param type
     * @param data
     */
    public ArrayInput(String components, String lineWidth, String height, String type, String data) {
        this.components = Integer.parseInt(components);
        this.lineWidth = Integer.parseInt(lineWidth);
        this.height = Integer.parseInt(height);
        this.type = DataType.valueOf(type);
        switch (this.type) {
        case FLOAT:
            this.data = StringUtils.getFloatArray(data);
            break;
        case INT:
            this.data = StringUtils.getIntArray(data);
            break;
        case SHORT:
            this.data = StringUtils.getShortArray(data);
            break;
        default:
            throw new IllegalArgumentException(NOT_IMPLEMENTED_ERROR + type);
        }

    }

    /**
     * Copies the data in this array to the source int array.
     * 
     * @param dest
     * @param components
     * @param lineWidth
     * @param height
     */
    public void copyArray(int[] dest, int components, int lineWidth, int height) {

        switch (type) {
        case INT:
            copyArray((int[]) data, dest, components, lineWidth, height);
            break;
        default:
            throw new IllegalArgumentException("Not implemented support for " + type);
        }
    }

    /**
     * Copies the data in this array to the source float array.
     * 
     * @param dest
     * @param components
     * @param lineWidth
     * @param height
     */
    public void copyArray(float[] dest, int components, int lineWidth, int height) {

        switch (type) {
        case INT:
            copyArray((int[]) data, dest, components, lineWidth, height);
            break;
        default:
            throw new IllegalArgumentException("Not implemented support for " + type);
        }
    }

    protected void copyArray(int[] source, int[] dest, int components, int lineWidth, int height) {
        if (this.height < height) {
            height = this.height;
        }
        int w = lineWidth;
        if (this.lineWidth < lineWidth) {
            w = this.lineWidth;
        }
        int sourceIndex = 0;
        int destIndex = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < w; x++) {
                dest[destIndex + x] = source[sourceIndex + x];
            }
            destIndex += lineWidth;
            sourceIndex += this.lineWidth;
        }
    }

    protected void copyArray(int[] source, float[] dest, int components, int lineWidth, int height) {
        if (this.height < height) {
            height = this.height;
        }
        int w = lineWidth;
        if (this.lineWidth < lineWidth) {
            w = this.lineWidth;
        }
        int sourceIndex = 0;
        int destIndex = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < w; x++) {
                dest[destIndex + x] = source[sourceIndex + x];
            }
            destIndex += lineWidth;
            sourceIndex += this.lineWidth;
        }
    }
}
