package com.graphicsengine.map;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.dataflow.ArrayInputData;
import com.nucleus.io.BaseReference;
import com.nucleus.types.DataType;
import com.nucleus.vecmath.Axis;

/**
 * The map for a playfield, this class holds the char data
 * This class can be serialized using GSON
 * The Map itself does not contain the charmap - that and other data is contained in the {@linkplain Playfield}
 * 
 * @author Richard Sahlin
 *
 */
public class Map extends BaseReference {

    public static final String MODE = "mode";
    public static final String FORMAT = "format";
    public static final String COLOR = "color";

    /**
     * Char or vertex based info
     *
     */
    public enum Mode {
        CHAR(), VERTEX();
    }

    /**
     * Color info for map, can be either per vertex or per char.
     *
     */
    public class MapColor {
        @SerializedName(MODE)
        private Mode mode;
        /**
         * VEC3 or VEC4
         */
        @SerializedName(FORMAT)
        private DataType format;
        @SerializedName(COLOR)
        private float[] color;
        
        /**
         * Creates a new color for map
         * 
         * @param width
         * @param height
         * @param mode
         * @param format VEC3 or VEC4
         * @throws IllegalArgumentException if format is not VEC3 or VEC4, or mode or format is null.
         */
        public MapColor(int width, int height, Mode mode, DataType format) {
            if (mode == null || format == null || (format != DataType.VEC3 && format != DataType.VEC4)) {
                throw new IllegalArgumentException("Invalid mode or format: " + mode + ", " + format);
            }
            this.format = format;
            this.mode = mode;
            int sizePerChar = getSizePerChar();
            color = new float[sizePerChar * width * height];
        }

        /**
         * Returns the colormode, this defines if the color information is per char or per vertex.
         * 
         * @return
         */
        public Mode getMode() {
            return mode;
        }

        /**
         * Returns the datatype for each color value, this defines how many values each color is stored with.
         * Either VEC3 or VEC4
         * 
         * @return
         */
        public DataType getFormat() {
            return format;
        }

        /**
         * Fills the colormap with the specified fillcolor, if format is VEC3 then 3 values are copied.
         * If format is VEC4 then 4 values are copied.
         * 
         * @param fillColor
         */
        public void fill(float[] fillColor) {
            switch (format) {
            case VEC3:
                fillVEC3(fillColor);
                break;
            case VEC4:
                fillVEC4(fillColor);
                break;
            default:
                throw new IllegalArgumentException("Invalid format: " + format);
            }
        }

        private void fillVEC3(float[] fillColor) {
            int index = 0;
            int size = color.length;
            while (index < size) {
                color[index++] = fillColor[0];
                color[index++] = fillColor[1];
                color[index++] = fillColor[2];
            }
        }

        private void fillVEC4(float[] fillColor) {
            int index = 0;
            int size = color.length;
            while (index < size) {
                color[index++] = fillColor[0];
                color[index++] = fillColor[1];
                color[index++] = fillColor[2];
                color[index++] = fillColor[3];
            }
        }

        /**
         * Size in floats for each char, result depends on {@link #getMode()} and {@link #getFormat()}
         * 
         * @return
         */
        public int getSizePerChar() {
            int size = format.getSize() / 4;
            switch (mode) {
            case CHAR:
                return size;
            case VERTEX:
                return size * 4;
            default:
                throw new IllegalArgumentException("Invalid mode:" + mode);
            }

        }

        /**
         * Returns number of floats to next vertex, either 0 or depending on format.
         * 
         * @return Number of floats to step between ambient material values in a char.
         */
        public int getVertexStride() {
            if (mode == Mode.CHAR) {
                return 0;
            }
            return format.getSize() / 4;
        }

        /**
         * Returns a reference to color values.
         * 
         * @return
         */
        public float[] getColor() {
            return color;
        }

    }

    public static final String MAPSIZE = "mapSize";
    public static final String MAPDATA = "mapData";
    public static final String FLAGS = "flags";
    public static final String AMBIENT = "ambient";
    public static final String ARRAYINPUT = "arrayInput";

    public static final int FLIP_X = 4;
    public static final int FLIP_Y = 2;

    /**
     * The size of the map, usually 2 values.
     */
    @SerializedName(MAPSIZE)
    private int[] mapSize;
    /**
     * The map data
     */
    @SerializedName(MAPDATA)
    private int[] mapData;

    @SerializedName(FLAGS)
    private int[] flags;

    @SerializedName(ARRAYINPUT)
    private ArrayInputData arrayInput;

    @SerializedName(AMBIENT)
    private MapColor ambient;

    /**
     * Creates a new empty playfield, with the specified width and height.
     * Storage for ambient material is created
     * 
     * @param width
     * @param height
     * @param ambientMode Storage mode for ambient material
     * @param ambientFormat Datatype for ambient material VEC3 or VEC4
     * @throws IllegalArgumentException If ambient is null or ambientFormat is not VEC3 or VEC4
     */
    Map(int width, int height, Mode ambientMode, DataType ambientFormat) {
        createArrays(width, height, ambientMode, ambientFormat);
    }

    private void createArrays(int width, int height, Mode ambientMode, DataType ambientFormat) {
        mapSize = new int[] { width, height };
        mapData = new int[width * height];
        flags = new int[width * height];
        ambient = new MapColor(width, height, ambientMode, ambientFormat);
    }

    /**
     * Creates a new playfield from the controller source
     * This playfield will contain the mapdata from the source, the id will be the mapReference from the source.
     * This is used when exporting
     * 
     * @param source
     */
    Map(PlayfieldNode source) {
        mapSize = new int[2];
        createMap(source);
    }

    /**
     * Creates the mapdata in this class from the source, this will copy map size and the data in the map.
     * 
     * @param source
     */
    void createMap(PlayfieldNode source) {
        setSize(source.getMapSize());
        if (source.getMapData() != null) {
            mapData = new int[mapSize[Axis.WIDTH.index] * mapSize[Axis.HEIGHT.index]];
            System.arraycopy(source.getMapData(), 0, mapData, 0, mapData.length);
        }
    }

    /**
     * Creats ambient lightmap for the map, map must be initialized with size
     * 
     * @param mode
     * @param format
     * @throws IllegalArgumentException If map does not have size
     */
    public void createAmbient(Mode mode, DataType format) {
        if (mapSize == null || mapSize[0] <= 0 || mapSize[1] <= 0) {
            throw new IllegalArgumentException("Map does not have valid size");
        }
        ambient = new MapColor(mapSize[0], mapSize[1], mode, format);
    }

    /**
     * Returns the ambient material color for each char or per vertex, or null if not set.
     * 
     * @return Ambient material properties, or null if not set.
     */
    public MapColor getAmbient() {
        return ambient;
    }

    /**
     * Returns the mapsize, width and height
     */
    public int[] getMapSize() {
        return mapSize;
    }

    /**
     * Copies the size from the source array
     * 
     * @param size Array with at least 2 values, width and height
     */
    private void setSize(int[] size) {
        mapSize[Axis.WIDTH.index] = size[Axis.WIDTH.index];
        mapSize[Axis.HEIGHT.index] = size[Axis.HEIGHT.index];
    }

    /**
     * Returns the map data, this is a reference to the map array - any changes will be reflected in this class
     * 
     * @return Array with map data, or null if not set
     */
    public int[] getMap() {
        return mapData;
    }

    /**
     * Returns the flags, this is a reference to the flags array - any changes will be reflected here.
     * 
     * @return
     */
    public int[] getFlags() {
        return flags;
    }

    /**
     * Returns the array input if set
     * 
     * @return Array input data or null
     */
    public ArrayInputData getArrayInput() {
        return arrayInput;
    }

    /**
     * Fills all of the map with the specified value
     * 
     * @param value
     */
    public void fill(int value) {
        for (int i = 0; i < mapData.length; i++) {
            mapData[i] = value;
        }
    }

    /**
     * Sets the state of the flip X flag
     * 
     * @param index
     * @param flip
     */
    public void setFlipX(int index, boolean flip) {
        flags[index] = flags[index] | ((flip) ? FLIP_X : 0);
    }

    /**
     * Sets the state of the flip Y flag
     * 
     * @param index
     * @param flip
     */
    public void setFlipY(int index, boolean flip) {
        flags[index] = flags[index] | ((flip) ? FLIP_Y : 0);
    }

    /**
     * Returns the state of the flip X flag
     * 
     * @param index
     * @return
     */
    public boolean getFlipX(int index) {
        return ((flags[index] & FLIP_X) == FLIP_X) ? true : false;
    }

    /**
     * Returns the state of the flip Y flag
     * 
     * @param index
     * @return
     */
    public boolean getFlipY(int index) {
        return ((flags[index] & FLIP_Y) == FLIP_Y) ? true : false;
    }

    /**
     * Returns true if the flag(s) is set at the specified index
     * 
     * @param index
     * @param flag
     * @return
     */
    public boolean isFlag(int index, int flag) {
        return ((flags[index] & flag) == flag) ? true : false;
    }

}
