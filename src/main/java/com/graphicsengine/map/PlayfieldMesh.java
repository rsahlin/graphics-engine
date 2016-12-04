package com.graphicsengine.map;

import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.geometry.Material;
import com.nucleus.geometry.Mesh;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.vecmath.Axis;
import com.nucleus.vecmath.Rectangle;

/**
 * Old school charactermap based rendering using a texture and quad mesh, the normal way to use the charmap is to create
 * with specified number of chars to cover a certain area.
 * This class has no real functionality besides being able to be rendered - to use the charmap, see
 * {@link PlayfieldNode}
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldMesh extends SpriteMesh {

    /**
     * playfield character data, one value for each char - this is the source map that can be used for collision etc.
     * This MUST be in sync with {@link #attributeData}
     */
    transient int[] charmap;
    /**
     * The width and height of the charmap
     */
    final transient int[] size = new int[2];

    /**
     * Creates a new instance of an empty playfield mesh.
     */
    protected PlayfieldMesh() {
        super();
    }

    /**
     * Creates a copy of the playfield - NOTE this will ONLY create the character and attribute storage.
     * {@link #createMesh(PlayfieldProgram, Texture2D, float[], float[])} and
     * {@link #setupCharmap(int, int, float, float)} MUST be called in order to setup all values.
     * 
     * @param source The source, id, textureRef and character count is taken from here.
     */
    protected PlayfieldMesh(Mesh source) {
        super(source);
        set(source);
    }

    /**
     * Sets the values from the source playfield mesh.
     * Note, this will NOT create the Mesh, it will just set the values so that the mesh can be created.
     * 
     * @param source
     */
    public void set(PlayfieldMesh source) {
        setId(source.getId());
        this.textureRef = source.textureRef;
    }

    /**
     * Initializes the data in this class for the specified number of chars
     * This will not create the Mesh
     * Internal method
     * 
     * @param charCount
     */
    private void init(int charCount) {
        charmap = new int[charCount];
    }


    /**
     * Creates the mesh for this charmap, each char has the specified width and height, z position.
     * Texture UV is set using 1 / framesX and 1/ framesY
     * 
     * @param program
     * @param texture If tiling should be used this must be instance of {@link TiledTexture2D}
     * @param material
     * @param mapSize Number of chars to support in the mesh
     * @param rectangle The rectangle defining a char, all chars will have same size.
     */
    public void createMesh(PlayfieldProgram program, Texture2D texture, Material material, int[] mapSize,
            Rectangle rectangle) {
        int count = mapSize[0] * mapSize[1];
        super.createMesh(program, texture, material, count, rectangle);
        init(count);
    }

    /**
     * Creates and positions the characters using width and height number of chars, starting at xpos, ypos
     * This will set the position for each character, the map can be moved by translating
     * the node it is attached to.
     * Use this method to layout the characters on your visible screen as needed.
     * The chars will be laid out sequentially across the x axis (row based)
     * Before rendering the attributes in the mesh must be updated with attribute data from this class.
     * 
     * @param mapSize width and height of map, in characters
     * @param charSize width and height of each char
     * @param offset Start position of the upper left char, ie the upper left char will have this position.
     * @throws IllegalArgumentException If the size of the map does not mach number of chars in this class
     */
    public void setupCharmap(int[] mapSize, float[] charSize, float[] offset) {
        if (mapSize[Axis.WIDTH.index] * mapSize[Axis.HEIGHT.index] != charmap.length) {
            throw new IllegalArgumentException("Size of map does not match number of chars in mesh");
        }
        size[Axis.WIDTH.index] = mapSize[Axis.WIDTH.index];
        size[Axis.HEIGHT.index] = mapSize[Axis.HEIGHT.index];
        int index = 0;
        float currentX = offset[0];
        float currentY = offset[1];
        for (int y = 0; y < mapSize[1]; y++) {
            currentY = offset[1];
            for (int x = 0; x < mapSize[0]; x++) {
                attributeData[index + mapper.TRANSLATE_INDEX] = currentX;
                attributeData[index + mapper.TRANSLATE_INDEX + 1] = currentY;
                index += mapper.ATTRIBUTES_PER_VERTEX;
                attributeData[index + mapper.TRANSLATE_INDEX] = currentX;
                attributeData[index + mapper.TRANSLATE_INDEX + 1] = currentY;
                index += mapper.ATTRIBUTES_PER_VERTEX;
                attributeData[index + mapper.TRANSLATE_INDEX] = currentX;
                attributeData[index + mapper.TRANSLATE_INDEX + 1] = currentY;
                index += mapper.ATTRIBUTES_PER_VERTEX;
                attributeData[index + mapper.TRANSLATE_INDEX] = currentX;
                attributeData[index + mapper.TRANSLATE_INDEX + 1] = currentY;
                index += mapper.ATTRIBUTES_PER_VERTEX;
                currentX += charSize[Axis.WIDTH.index];
            }
            currentX = offset[0];
            // TODO handle Y axis going other direction?
            offset[1] -= charSize[Axis.HEIGHT.index];
        }
    }

    /**
     * Copies char frame index data from the source into this charmap, source data should only include char number.
     * Note that there will be conversion from int[] to float values as the char data is copied.
     * Do NOT use this method for a large number of data when performance is critical.
     * 
     * @param mapper The attribute property mapper
     * @param source Source map data
     * @param sourceOffset Offset into source where data is read
     * @param destOffset Offset where data is written in this class
     * @param count Number of chars to copy
     * @throws ArrayIndexOutOfBoundsException If source or destination does not contain enough data.
     */
    public void copyCharmap(PropertyMapper mapper, int[] source, int sourceOffset, int destOffset, int count) {
        for (int i = 0; i < count; i++) {
            setChar(mapper, destOffset++, source[sourceOffset++]);
        }
    }

    /**
     * Copies the data from the source map into this class
     * The copy will be done on a row by row basis, adjusting to different size of source and destination.
     * 
     * @param mapper The attribute property mapper
     * @param source Map data will be copied from this
     */
    public void copyCharmap(PropertyMapper mapper, Map source) {
        if (source == null || source.getMapSize() == null) {
            return;
        }
        int[] sourceSize = source.getMapSize();

        int height = Math.min(size[Axis.HEIGHT.index], sourceSize[Axis.HEIGHT.index]);
        int width = Math.min(size[Axis.WIDTH.index], sourceSize[Axis.WIDTH.index]);
        for (int y = 0; y < height; y++) {
            copyCharmap(mapper, source.getMap(), y * sourceSize[Axis.WIDTH.index], y * size[Axis.WIDTH.index], width);
        }

    }

    /**
     * Fills a rectangular area with a specific character value.
     * This method is not performance optimized, if a large area shall be filled with high performance then consider
     * using a custom function that write into {@link #attributeData}
     * 
     * @param mapper The attribute property mapper
     * @param x Map start x of fill
     * @param y Map start y of fill
     * @param width With of area to fill
     * @param height Height of area to fill
     * @param fill Fill value
     */
    public void fill(PropertyMapper mapper, int x, int y, int width, int height, int fill, int[] mapSize) {
        if (x > mapSize[Axis.WIDTH.index] || y > mapSize[Axis.HEIGHT.index]) {
            // Completely outside
            return;
        }
        int startChar = y * width + y;
        if (x + width > mapSize[Axis.WIDTH.index]) {
            width = mapSize[Axis.WIDTH.index] - x;
        }
        if (y + height > mapSize[Axis.HEIGHT.index]) {
            height = mapSize[Axis.HEIGHT.index] - y;
        }
        while (height-- > 0) {
            for (int i = 0; i < width; i++) {
                setChar(mapper, startChar++, fill);
            }
        }
    }

    /**
     * Internal method to set a char at a playfield (charmap) position.
     * This will set the data in both the attribute array and the playfield data.
     * This method shall not be used for a large number of chars or when performance is important.
     * 
     * @param mapper Property mapper for attribute indexes
     * @param pos The playfield position, from 0 to width * height.
     * @param value The value to set.
     */
    private void setChar(PropertyMapper mapper, int pos, int value) {
        charmap[pos] = value;
        int destIndex = pos * mapper.ATTRIBUTES_PER_VERTEX * ShaderProgram.VERTICES_PER_SPRITE
                + mapper.FRAME_INDEX;
        attributeData[destIndex] = value;
        destIndex += mapper.ATTRIBUTES_PER_VERTEX;
        attributeData[destIndex] = value;
        destIndex += mapper.ATTRIBUTES_PER_VERTEX;
        attributeData[destIndex] = value;
        destIndex += mapper.ATTRIBUTES_PER_VERTEX;
        attributeData[destIndex] = value;
        destIndex += mapper.ATTRIBUTES_PER_VERTEX;
        getVerticeBuffer(BufferIndex.ATTRIBUTES).setDirty(true);
    }

    /**
     * Returns the playfield map, this contains one int for each char position.
     * Can be used for collision
     * 
     * @return
     */
    public int[] getPlayfield() {
        return charmap;
    }

    @Override
    public void destroy() {
        attributeData = null;
    }

}
