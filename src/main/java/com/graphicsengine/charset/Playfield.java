package com.graphicsengine.charset;

import com.nucleus.geometry.AttributeUpdater;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshBuilder;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.opengl.GLES20Wrapper.GLES20;
import com.nucleus.texturing.Texture2D;

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
     * playfield character data, one value for each char - this is the source map.
     */
    int[] playfieldData;

    /**
     * Creates a new charmap with the specified number of characters
     * The mesh must be created before the charmap is rendered.
     * 
     * @param charCount Number of chars to create attribute storage for.
     */
    public Playfield(int charCount) {
        this.charCount = charCount;
        attributeData = new float[(charCount * PlayfieldProgram.ATTRIBUTES_PER_CHAR)];
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
     * @param texture
     * @param charWidth Width of one char in world units.
     * @param charHeight Height of one char in world units.
     * @param z Z position of chars
     * @param framesX Number of frames on the x axis
     * @param framesY Number of frames on the y axis
     * @return The mesh ready to be rendered
     */
    public void createMesh(PlayfieldProgram program, Texture2D texture, float charWidth, float charHeight, float z,
            int framesX, int framesY) {
        program.buildCharsetMesh(this, charCount, charWidth, charHeight, z, GLES20.GL_FLOAT, 1f / framesX,
                1f / framesY);
        setTexture(texture, Texture2D.TEXTURE_0);
        setAttributeUpdater(this);
        this.charWidth = charWidth;
        this.charHeight = charHeight;
    }

    /**
     * Positions the characters using width and height number of chars, starting at xpos, ypos
     * This will set the position for each character (it's vertices), the map can be moved by translating
     * the node it is attached to.
     * Use this emthod to layout the characters on your visible screen as needed.
     * The chars will be laid out sequentially across the x axis (row based)
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
     * @param charRows Row based char data, each value is the frame index for a char.
     * @param startOffset Offset into charRows where data is read.
     * @param startChar Put data at this char index, 0 for first char, 10 for the thenth etc.
     * @throws ArrayIndexOutOfBoundsException If source or destination does not contain enough data.
     */
    public void setPlayfieldData(int[] charRows, int startOffset, int startChar, int count) {

        int destIndex = startChar * PlayfieldProgram.ATTRIBUTES_PER_CHAR
                + PlayfieldProgram.ATTRIBUTE_CHARMAP_FRAME_INDEX;
        float c;
        for (int i = 0; i < count; i++) {
            c = charRows[startOffset++];
            attributeData[destIndex] = c;
            destIndex += PlayfieldProgram.ATTRIBUTES_PER_VERTEX;
            attributeData[destIndex] = c;
            destIndex += PlayfieldProgram.ATTRIBUTES_PER_VERTEX;
            attributeData[destIndex] = c;
            destIndex += PlayfieldProgram.ATTRIBUTES_PER_VERTEX;
            attributeData[destIndex] = c;
            destIndex += PlayfieldProgram.ATTRIBUTES_PER_VERTEX;
        }

    }

    @Override
    public void setAttributeData() {
        VertexBuffer positions = getVerticeBuffer(1);
        positions.setArray(getAttributeData(), 0, 0, charCount * PlayfieldProgram.ATTRIBUTES_PER_CHAR);
    }

    @Override
    public float[] getAttributeData() {
        return attributeData;
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

}
