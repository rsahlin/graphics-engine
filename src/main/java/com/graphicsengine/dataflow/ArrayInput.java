package com.graphicsengine.dataflow;

import com.nucleus.common.StringUtils;
import com.nucleus.io.DataExporter;
import com.nucleus.io.DataImporter;
import com.nucleus.io.DataSetup;
import com.nucleus.types.DataType;
import com.nucleus.vecmath.Axis;

/**
 * Dataflow for an input array source. The input data is aligned into rows, this makes it useful for loading
 * data into row based data such as a tile/char map.
 * This is used to provide data for scene components
 * 
 * @author Richard Sahlin
 *
 */
public class ArrayInput extends DataSetup implements DataImporter, DataExporter {

    private final static String NOT_IMPLEMENTED_ERROR = "Not implemented support for: ";

    public enum ArrayInputMapping implements DataIndexer {
        COMPONENTS(0, DataType.INT),
        LINEWIDTH(1, DataType.INT),
        HEIGHT(2, DataType.INT),
        TYPE(3, DataType.STRING),
        XOFFSET(4, DataType.INT),
        YOFFSET(5, DataType.INT),
        DATA(6, DataType.INT);

        private final int index;
        private final DataType type;

        private ArrayInputMapping(int index, DataType type) {
            this.index = index;
            this.type = type;
        }

        @Override
        public int getIndex() {
            return index;
        }

        @Override
        public DataType getType() {
            return type;
        }
    }

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
     * FLOAT, INT, SHORT or STRING arrays
     */
    DataType type;
    /**
     * Array containing the data, possible arrays are float[], int[], short[] and String
     */
    Object data;

    /**
     * Y offset into destination
     */
    int xOffset = 0;
    /**
     * X offset into destination
     */
    int yOffset = 0;

    /**
     * Default constructor
     */
    public ArrayInput() {
        super();
    }

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
     * Creates a new array input from the data source.
     * 
     * @param source
     */
    public ArrayInput(ArrayInputData source) {
        components = source.getComponents();
        lineWidth = source.getDimension().getDimension()[Axis.WIDTH.index];
        height = source.getDimension().getDimension()[Axis.HEIGHT.index];
        type = source.getDataType();
    }

    /**
     * Sets the offset where the data is stored in the destination
     * 
     * @param x X offset into destination
     * @param y Y offst into destination
     */
    public void setOffset(int x, int y) {
        this.xOffset = x;
        this.yOffset = y;
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
        setData(data);
    }

    /**
     * Sets the data in this object using a String source, the values must be delimited by ','
     * 
     * @param data String with values in the format according to the type in this class and delimtered by ','
     */
    public void setData(String data) {
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
        case STRING:
            this.data = data;
            break;
        default:
            throw new IllegalArgumentException(NOT_IMPLEMENTED_ERROR + type);
        }

    }

    /**
     * Copies the data in this array to the source int array.
     * 
     * @param dest
     * @param lineWidth
     * @param height
     */
    public void copyArray(int[] dest, int lineWidth, int height) {

        switch (type) {
        case INT:
            copyArray((int[]) data, dest, lineWidth, height);
            break;
        case STRING:
            copyArray((String) data, dest, lineWidth, height);
            break;
        default:
            throw new IllegalArgumentException("Not implemented support for " + type);
        }
    }

    /**
     * Copies the data in this array to the source float array.
     * 
     * @param dest
     * @param lineWidth
     * @param height
     */
    public void copyArray(float[] dest, int lineWidth, int height) {

        switch (type) {
        case INT:
            copyArray((int[]) data, dest, lineWidth, height);
            break;
        default:
            throw new IllegalArgumentException("Not implemented support for " + type);
        }
    }

    /**
     * Copy data from source to dest, using lineWidth for destination and this.lineWidth for source, this means
     * copying data FROM this class TO another.
     * 
     * @param source Source array, will be updated with this.lineWidth
     * @param dest Destination array, will be updated by lineWidth
     * @param lineWidth Width of one line in destination
     * @param height
     */
    protected void copyArray(int[] source, int[] dest, int lineWidth, int height) {
        height = Math.min(height, this.height);
        int w = Math.min(lineWidth, this.lineWidth);
        int sourceIndex = 0;
        int destIndex = calcStartIndex();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < w; x++) {
                dest[destIndex + x] = source[sourceIndex + x];
            }
            destIndex += lineWidth;
            sourceIndex += this.lineWidth;
        }
    }

    /**
     * Copy data from source to dest, using lineWidth for destination and this.lineWidth for source, this means
     * copying data FROM this class TO another.
     * 
     * @param source Source array, will be updated with this.lineWidth
     * @param dest Destination array, will be updated by lineWidth
     * @param lineWidth Width of one line in destination
     * @param height
     */
    protected void copyArray(String source, int[] dest, int lineWidth, int height) {
        height = Math.min(height, this.height);
        int w = Math.min(lineWidth, this.lineWidth);
        int sourceIndex = 0;
        int destIndex = calcStartIndex();
        byte[] byteSource = source.getBytes();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < w; x++) {
                dest[destIndex + x] = byteSource[sourceIndex + x];
            }
            destIndex += lineWidth;
            sourceIndex += this.lineWidth;
        }
    }

    /**
     * Copy data from source to dest, using lineWidth for destination and this.lineWidth for source, this means
     * copying data FROM this class TO another.
     * 
     * @param source Source array, will be updated with this.lineWidth
     * @param dest Destination array, will be updated by lineWidth
     * @param lineWidth Width of one line in destination
     * @param height
     */
    protected void copyArray(int[] source, float[] dest, int lineWidth, int height) {
        height = Math.min(height, this.height);
        int w = Math.min(lineWidth, this.lineWidth);
        int sourceIndex = 0;
        int destIndex = calcStartIndex();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < w; x++) {
                dest[destIndex + x] = source[sourceIndex + x];
            }
            destIndex += lineWidth;
            sourceIndex += this.lineWidth;
        }
    }

    /**
     * Calculate the start index using x and y offset, making sure that the startposition is not outside destination.
     * 
     * @return Destination start index
     */
    private int calcStartIndex() {
        return this.lineWidth * Math.min(yOffset, this.height) + Math.min(xOffset, this.lineWidth);
    }

    @Override
    public String exportDataAsString() {
        return StringUtils.getString(exportDataAsStringArray());
    }

    @Override
    public int importData(String[] data, int offset) {
        components = getInt(data, offset, ArrayInputMapping.COMPONENTS);
        lineWidth = getInt(data, offset, ArrayInputMapping.LINEWIDTH);
        height = getInt(data, offset, ArrayInputMapping.HEIGHT);
        type = DataType.valueOf(getString(data, offset, ArrayInputMapping.TYPE));
        xOffset = getInt(data, offset, ArrayInputMapping.XOFFSET);
        yOffset = getInt(data, offset, ArrayInputMapping.YOFFSET);
        setData(getString(data, offset, ArrayInputMapping.DATA));
        return ArrayInputMapping.values().length;
    }

    @Override
    public String[] exportDataAsStringArray() {
        String[] strArray = new String[ArrayInputMapping.values().length];
        setData(strArray, ArrayInputMapping.COMPONENTS, components);
        setData(strArray, ArrayInputMapping.LINEWIDTH, lineWidth);
        setData(strArray, ArrayInputMapping.HEIGHT, height);
        setData(strArray, ArrayInputMapping.TYPE, type.name());
        setData(strArray, ArrayInputMapping.XOFFSET, xOffset);
        setData(strArray, ArrayInputMapping.YOFFSET, yOffset);
        switch (type) {
        case INT:
            setData(strArray, ArrayInputMapping.DATA, (int[]) data);
        }
        return strArray;
    }
}
