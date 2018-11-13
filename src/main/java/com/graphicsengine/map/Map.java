package com.graphicsengine.map;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.nucleus.SimpleLogger;
import com.nucleus.common.BufferUtils;
import com.nucleus.io.BaseReference;
import com.nucleus.types.DataType;
import com.nucleus.vecmath.Axis;

/**
 * The map for a playfield, this class holds the char data
 * The Map itself does not contain the charmap - that and other data is contained in the {@linkplain Playfield}
 * 
 * @author Richard Sahlin
 *
 */
public class Map extends BaseReference implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 8961728842536352805L;
    public static final String MODE = "mode";
    public static final String FORMAT = "format";
    public static final String COLOR = "color";

    private final static byte VERSION_1 = 1;

    /**
     * Char or vertex based info
     *
     */
    public enum Mode {
        CHAR(1),
        VERTEX(2);

        public final int value;

        private Mode(int value) {
            this.value = value;
        }

        public static Mode valueOf(int value) {
            for (Mode mode : values()) {
                if (mode.value == value) {
                    return mode;
                }
            }
            return null;
        }
    }

    /**
     * Color info for map, can be either per vertex or per char.
     *
     */
    public class MapColor implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = -7662510905955399450L;

        private int length;
        private Mode mode;
        /**
         * VEC3 or VEC4
         */
        private DataType format;
        private FloatBuffer color;

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
            length = width * height * sizePerChar;
            createBuffer();
        }

        private void createBuffer() {
            color = BufferUtils.createFloatBuffer(length);
            SimpleLogger.d(getClass(), "Created emissive buffer with " + length + " floats");
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
            color.position(0);
            int index = 0;
            while (index < length) {
                color.put(fillColor);
                index += 3;
            }
        }

        private void fillVEC4(float[] fillColor) {
            color.position(0);
            int index = 0;
            while (index < length) {
                color.put(fillColor);
                index += 4;
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
         * @return Number of floats to step between emissive material values in a char.
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
        public FloatBuffer getColor() {
            return color;
        }

        private void writeObject(ObjectOutputStream out) throws IOException {
            SimpleLogger.d(getClass(), "writeObject()");
            out.writeByte(VERSION_1);
            out.writeInt(length);
            out.writeByte(mode != null ? mode.value : -1);
            out.writeByte(format != null ? format.getType() : -1);
            float[] data = new float[length];
            color.position(0);
            color.get(data);
            out.writeObject(data);
        }

        private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
            SimpleLogger.d(getClass(), "readObject()");
            byte version = in.readByte();
            length = in.readInt();
            mode = Mode.valueOf(in.readByte());
            format = DataType.valueOf(in.readByte());
            createBuffer();
            float[] data = (float[]) in.readObject();
            color.position(0);
            color.put(data);
        }

    }

    public static final String MAPSIZE = "mapSize";

    public static final int FLIP_X = 4;
    public static final int FLIP_Y = 2;

    /**
     * The size of the map, usually 2 values.
     */
    private int[] mapSize;
    /**
     * The map data
     */
    private IntBuffer mapBuffer;

    private ByteBuffer flags;

    private MapColor emissive;

    /**
     * Creates a new empty playfield, with the specified width and height.
     * Storage for emissive material is created
     * 
     * @param width
     * @param height
     * @param emissiveMode Storage mode for emissive material
     * @param emissiveFormat Datatype for emissive material VEC3 or VEC4
     * @throws IllegalArgumentException If emissive is null or emissiveFormat is not VEC3 or VEC4
     */
    Map(int width, int height, Mode emissiveMode, DataType emissiveFormat) {
        mapSize = new int[] { width, height };
        createBuffers(width, height, emissiveMode, emissiveFormat);
    }

    private void createBuffers(int width, int height, Mode emissiveMode, DataType emissiveFormat) {
        createBuffers(width, height);
        emissive = new MapColor(width, height, emissiveMode, emissiveFormat);
    }

    /**
     * Creates map and flag buffers
     * 
     * @param width
     * @param height
     */
    private void createBuffers(int width, int height) {
        mapBuffer = BufferUtils.createIntBuffer(width * height);
        flags = BufferUtils.createByteBuffer(width * height);
    }

    /**
     * Creats emissive lightmap for the map, map must be initialized with size
     * 
     * @param mode
     * @param format
     * @throws IllegalArgumentException If map does not have size
     */
    public void createEmissive(Mode mode, DataType format) {
        if (mapSize == null || mapSize[0] <= 0 || mapSize[1] <= 0) {
            throw new IllegalArgumentException("Map does not have valid size");
        }
        emissive = new MapColor(mapSize[0], mapSize[1], mode, format);
    }

    /**
     * Returns the emissive material color for each char or per vertex, or null if not set.
     * 
     * @return Emissive material properties, or null if not set.
     */
    public MapColor getEmissive() {
        return emissive;
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
    public IntBuffer getMap() {
        return mapBuffer;
    }

    /**
     * Returns the flags, this is a reference to the flags array - any changes will be reflected here.
     * 
     * @return
     */
    public ByteBuffer getFlags() {
        return flags;
    }

    /**
     * Fills all of the map with the specified value.
     * NOTE - This is a very slow operation
     * 
     * @param value
     */
    public void fill(int value) {
        int length = mapSize[0] * mapSize[1];
        for (int i = 0; i < length; i++) {
            mapBuffer.put(i, value);
        }
    }

    /**
     * Sets the state of the flip X flag
     * 
     * @param index
     * @param flip
     */
    public void setFlipX(int index, boolean flip) {
        byte flag = (byte) (flags.get(index) | ((flip) ? FLIP_X : 0));
        flags.put(index, flag);
    }

    /**
     * Sets the state of the flip Y flag
     * 
     * @param index
     * @param flip
     */
    public void setFlipY(int index, boolean flip) {
        byte flag = (byte) (flags.get(index) | ((flip) ? FLIP_Y : 0));
        flags.put(index, flag);
    }

    /**
     * Returns the state of the flip X flag
     * 
     * @param index
     * @return
     */
    public boolean getFlipX(int index) {
        return ((flags.get(index) & FLIP_X) == FLIP_X) ? true : false;
    }

    /**
     * Returns the state of the flip Y flag
     * 
     * @param index
     * @return
     */
    public boolean getFlipY(int index) {
        return ((flags.get(index) & FLIP_Y) == FLIP_Y) ? true : false;
    }

    /**
     * Returns true if the flag(s) is set at the specified index
     * 
     * @param index
     * @param flag
     * @return
     */
    public boolean isFlag(int index, int flag) {
        return ((flags.get(index) & flag) == flag) ? true : false;
    }

    /**
     * Returns the length of the map, width * height
     * 
     * @return
     */
    public int getLength() {
        return mapSize != null ? mapSize[0] * mapSize[1] : 0;
    }

    /**
     * Returns the height of the map
     * 
     * @return
     */
    public int getHeight() {
        return mapSize != null ? mapSize[1] : 0;
    }

    /**
     * Returns the width of the map
     * 
     * @return
     */
    public int getWidth() {
        return mapSize != null ? mapSize[0] : 0;
    }

    /**
     * Copies data from the source to the mapbuffer, this will copy map char data from the source into this map.
     * 
     * @param src
     * @param offset
     * @param length
     */
    public void setMap(int[] src, int offset, int length) {
        mapBuffer.put(src, offset, length);
    }

    /**
     * Prints debug message for the map position
     * 
     * @param x
     * @param y
     */
    public void logMapPosition(int x, int y) {
        int index = y * mapSize[0] + x;
        if (index >= getLength()) {
            SimpleLogger.d(getClass(), "Outside map for pos: " + x + ", " + y);
        }
        String emissiveStr = "none";
        if (emissive != null) {
            switch (emissive.getMode()) {
                case CHAR:
                    emissiveStr = Float.toString(emissive.getColor().get(index));
                    break;
                case VERTEX:
                    emissiveStr = "per vertex";
            }
        }
        SimpleLogger.d(getClass(),
                "Position: " + x + ", " + y + " char:" + mapBuffer.get(index) + ", flags:" + flags.get(index)
                        + ", emissive" + emissiveStr);
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        SimpleLogger.d(getClass(), "writeObject()");
        out.writeByte(VERSION_1);
        out.writeInt(mapSize[0]);
        out.writeInt(mapSize[1]);
        int[] mapData = new int[mapSize[0] * mapSize[1]];
        mapBuffer.position(0);
        mapBuffer.get(mapData);
        out.writeObject(mapData);
        byte[] flagData = new byte[mapSize[0] * mapSize[1]];
        flags.position(0);
        flags.get(flagData);
        out.writeObject(flagData);
        out.writeObject(emissive);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        SimpleLogger.d(getClass(), "readObject()");
        byte version = in.readByte();
        mapSize = new int[] { in.readInt(), in.readInt() };
        createBuffers(mapSize[0], mapSize[1]);
        int[] mapData = (int[]) in.readObject();
        mapBuffer.position(0);
        mapBuffer.put(mapData);
        byte[] flagData = (byte[]) in.readObject();
        flags.position(0);
        flags.put(flagData);
        emissive = (MapColor) in.readObject();
    }

}
