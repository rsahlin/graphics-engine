package com.graphicsengine.charset;

import com.nucleus.geometry.AttributeUpdater;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshBuilder;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.opengl.GLESWrapper.GLES20;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;

/**
 * Old school charactermap based rendering using a texture and quad mesh, the normal way to use the charmap is to create
 * with specified number of chars to cover a certain area.
 * This class has no real functionality to use the charmap, use subclasses to get the desired functionality.
 * 
 * @author Richard Sahlin
 *
 */
public class Playfield extends Mesh implements AttributeUpdater {

    /**
     * Number of characters in the charmap, this is the max number of visible characters
     */
    private int charCount;
    /**
     * Width of one char, in world units
     */
    private float charWidth;
    /**
     * Height of one char, in world units
     */
    private float charHeight;

    /**
     * Zposition of playfield, by default all chars will have same zpos.
     * It is possible to go into the mesh and change the vertices z position but this is not adviced.
     */
    private float zPos;

    /**
     * Width of playfield in chars
     */
    private int width;
    /**
     * Height of playfield in chars
     */
    private int height;

    /**
     * Contains attribute data for all chars.
     * This data must be mapped into the mesh for changes to take place.
     */
    float[] attributeData;

    /**
     * playfield character data, one value for each char - this is the source map that can be used for collision etc.
     * This MUST be in sync with {@link #attributeData}
     */
    int[] playfieldData;

    /**
     * Creates a new charmap with the specified number of characters
     * The mesh must be created before the charmap is rendered.
     * 
     * @param charCount Number of chars to create attribute storage for.
     */
    public Playfield(String id, int charCount) {
        setId(id);
        this.charCount = charCount;
        attributeData = new float[(charCount * PlayfieldProgram.ATTRIBUTES_PER_CHAR)];
        playfieldData = new int[charCount];
        int offset = 0;
        for (int i = 0; i < charCount; i++) {
            MeshBuilder.prepareTiledUV(attributeData, offset, PlayfieldProgram.ATTRIBUTE_CHARMAP_U_INDEX,
                    PlayfieldProgram.ATTRIBUTE_CHARMAP_V_INDEX, PlayfieldProgram.ATTRIBUTES_PER_VERTEX);
            offset += PlayfieldProgram.ATTRIBUTES_PER_CHAR;
        }
    }

    /**
     * Creates the mesh for this charmap, each char has the specified width and height, z position.
     * Texture UV is set using 1 / framesX and 1/ framesY
     * 
     * @param program
     * @param texture If tiling should be used this must be instance of {@link TiledTexture2D}
     * @param charWidth Width of one char in world units.
     * @param charHeight Height of one char in world units.
     * @param z Z position of chars
     * @return The mesh ready to be rendered
     */
    public void createMesh(PlayfieldProgram program, Texture2D texture, PlayfieldSetup setup) {
        this.charWidth = setup.tileWidth;
        this.charHeight = setup.tileHeight;
        this.zPos = setup.zpos;
        program.buildMesh(this, texture, charCount, charWidth, charHeight, setup.zpos, GLES20.GL_FLOAT);
        setTexture(texture, Texture2D.TEXTURE_0);
        setAttributeUpdater(this);
    }

    /**
     * Positions the characters using width and height number of chars, starting at xpos, ypos
     * This will set the position for each character, the map can be moved by translating
     * the node it is attached to.
     * Use this method to layout the characters on your visible screen as needed.
     * The chars will be laid out sequentially across the x axis (row based)
     * Before rendering the attributes in the mesh must be updated with attribute data from this class.
     * 
     * @param width Width in characters, eg 10 will position 10 chars horizontally beginning at xpos.
     * @param height Height in characters, eg 10 will position 10 chars vertically beginning at ypos.
     * @param xpos Starting xpos for the upper left char.
     * @param ypos Starting ypos for the upper left char.
     */
    public void setupPlayfield(int width, int height, float xpos, float ypos) {
        this.width = width;
        this.height = height;
        int index = 0;
        float currentX = xpos;
        float currentY = ypos;
        for (int y = 0; y < height; y++) {
            currentY = ypos;
            for (int x = 0; x < width; x++) {
                attributeData[index + PlayfieldProgram.ATTRIBUTE_CHARMAP_X_INDEX] = currentX;
                attributeData[index + PlayfieldProgram.ATTRIBUTE_CHARMAP_Y_INDEX] = currentY;
                index += PlayfieldProgram.ATTRIBUTES_PER_VERTEX;
                attributeData[index + PlayfieldProgram.ATTRIBUTE_CHARMAP_X_INDEX] = currentX;
                attributeData[index + PlayfieldProgram.ATTRIBUTE_CHARMAP_Y_INDEX] = currentY;
                index += PlayfieldProgram.ATTRIBUTES_PER_VERTEX;
                attributeData[index + PlayfieldProgram.ATTRIBUTE_CHARMAP_X_INDEX] = currentX;
                attributeData[index + PlayfieldProgram.ATTRIBUTE_CHARMAP_Y_INDEX] = currentY;
                index += PlayfieldProgram.ATTRIBUTES_PER_VERTEX;
                attributeData[index + PlayfieldProgram.ATTRIBUTE_CHARMAP_X_INDEX] = currentX;
                attributeData[index + PlayfieldProgram.ATTRIBUTE_CHARMAP_Y_INDEX] = currentY;
                index += PlayfieldProgram.ATTRIBUTES_PER_VERTEX;
                currentX += charWidth;
            }
            currentX = xpos;
            ypos += charHeight;
        }
    }

    /**
     * Copies char frame index data from the source into this charmap, source data should only include char number.
     * Note that there will be conversion from int[] to float values as the char data is copied.
     * Do NOT use this method for a large number of data.
     * 
     * @param charRow Row based char data, each value is the frame index for a char.
     * @param startOffset Offset into charRows where data is read.
     * @param startChar Put data at this char index, 0 for first char, 10 for the thenth etc.
     * @param count Number of chars to copy
     * @throws ArrayIndexOutOfBoundsException If source or destination does not contain enough data.
     */
    public void setPlayfieldData(int[] charRow, int startOffset, int startChar, int count) {
        for (int i = 0; i < count; i++) {
            setChar(startChar++, charRow[startOffset++]);
        }
    }

    /**
     * Copies char frame index data from the source into this charmap, source data should only include char number.
     * Note that there will be conversion from byte[] to float values as the char data is copied.
     * Do NOT use this method for a large number of data.
     * 
     * @param charRow Row based char data, each value is the frame index for a char.
     * @param startOffset Offset into charRows where data is read.
     * @param startChar Put data at this char index, 0 for first char, 10 for the thenth etc.
     * @param count Number of chars to copy
     * @throws ArrayIndexOutOfBoundsException If source or destination does not contain enough data.
     */
    public void setPlayfieldData(byte[] charRow, int startOffset, int startChar, int count) {
        for (int i = 0; i < count; i++) {
            setChar(startChar++, charRow[startOffset++]);
        }
    }

    /**
     * Copies the string into the charmap, same as calling {@link #setPlayfieldData(byte[], int, int, int)}
     * 
     * @param row
     * @param startChar
     */
    public void setPlayfieldData(String row, int startChar) {
        setPlayfieldData(row.getBytes(), 0, startChar, row.length());
    }

    /**
     * Fills a rectangular area with a specific character value.
     * This method is not performance optimized, if a large area shall be filled with high performance then consider
     * using a custom function that write into {@link #attributeData}
     * 
     * @param x Xpos of start position
     * @param y Ypos of start position
     * @param width With of area to fill
     * @param height Height of area to fill
     * @param fill Fill value
     */
    public void fill(int x, int y, int width, int height, int fill) {
        if (x > this.width || y > this.height) {
            // Completely outside
            return;
        }
        int startChar = y * width + y;
        if (x + width > this.width) {
            width = this.width - x;
        }
        if (y + height > this.height) {
            height = this.height - y;
        }
        while (height-- > 0) {
            for (int i = 0; i < width; i++) {
                setChar(startChar++, fill);
            }
        }
    }

    /**
     * Internal method to set a char at a playfield (charmap) position.
     * This will set the data in both the attribute array and the playfield data.
     * This method shall not be used for a large number of chars or when performance is important.
     * 
     * @param pos The playfield position, from 0 to width * height.
     * @param value The value to set.
     */
    private void setChar(int pos, int value) {
        playfieldData[pos] = value;
        int destIndex = pos * PlayfieldProgram.ATTRIBUTES_PER_CHAR
                + PlayfieldProgram.ATTRIBUTE_CHARMAP_FRAME_INDEX;
        attributeData[destIndex] = value;
        destIndex += PlayfieldProgram.ATTRIBUTES_PER_VERTEX;
        attributeData[destIndex] = value;
        destIndex += PlayfieldProgram.ATTRIBUTES_PER_VERTEX;
        attributeData[destIndex] = value;
        destIndex += PlayfieldProgram.ATTRIBUTES_PER_VERTEX;
        attributeData[destIndex] = value;
        destIndex += PlayfieldProgram.ATTRIBUTES_PER_VERTEX;
    }

    @Override
    public void setAttributeData() {
        VertexBuffer positions = getVerticeBuffer(BufferIndex.ATTRIBUTES);
        positions.setArray(getAttributeData(), 0, 0, charCount * PlayfieldProgram.ATTRIBUTES_PER_CHAR);
    }

    @Override
    public float[] getAttributeData() {
        return attributeData;
    }

    /**
     * Returns the playfield map, this contains one int for each char position.
     * Can be used for collision
     * 
     * @return
     */
    public int[] getPlayfield() {
        return playfieldData;
    }

    @Override
    public void destroy() {
        attributeData = null;
    }

    /**
     * Return the width, in chars, of the playfield.
     * 
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returs the height, in chars, of the playfield.
     * 
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the width of each character in the playfield.
     * 
     * @return
     */
    public float getTileWidth() {
        return charWidth;
    }

    /**
     * Returns the height of one character in the playfield.
     * 
     * @return
     */
    public float getTileHeight() {
        return charHeight;
    }

    /**
     * Returns the base zpos for the playfield.
     * This will be the same across all chars unless the z position of vertices has been changed in the mesh.
     * 
     * @return The base z position of the chars, as specified when calling
     * {@link #createMesh(PlayfieldProgram, Texture2D, PlayfieldSetup)}
     */
    public float getZPos() {
        return zPos;
    }

    /**
     * Returns the character x and y position as set in this playfield. This does not take any node transform into
     * account.
     * 
     * @param charNumber charNumber to get position for, must be > 0 < charCount
     * @param result x and y position of char stored here, note this does not include node transforms.
     * @param offset Offset into result where values are stored.
     */
    public void getPosition(int charNumber, float[] result, int offset) {
        offset += charNumber * PlayfieldProgram.ATTRIBUTES_PER_CHAR;
        result[offset++] = attributeData[PlayfieldProgram.ATTRIBUTE_CHARMAP_X_INDEX];
        result[offset++] = attributeData[PlayfieldProgram.ATTRIBUTE_CHARMAP_Y_INDEX];
    }

}
