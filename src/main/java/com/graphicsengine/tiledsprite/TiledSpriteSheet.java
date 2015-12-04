package com.graphicsengine.tiledsprite;

import com.nucleus.geometry.AttributeUpdater;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.vecmath.Axis;

/**
 * A number of sprites that will be rendered using the same Mesh, ie all sprites in this class are rendered using one
 * draw call.
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSpriteSheet extends Mesh implements AttributeUpdater {

    /**
     * Contains attribute data for all sprites.
     * This data must be mapped into the mesh for changes to take place.
     */
    float[] attributeData;

    int count;
    /**
     * Width and height of each sprite.
     */
    private float[] size = new float[2];
    /**
     * Anchor points for each sprite.
     */
    private float[] anchor = new float[3];

    /**
     * Creates a new sprite sheet using one mesh, the mesh must be created before being used.
     * 
     * @param spriteCount
     */
    public TiledSpriteSheet(int spriteCount) {
        setup(spriteCount);
    }

    private void setup(int spriteCount) {
        count = spriteCount;
        attributeData = new float[count * TiledSpriteProgram.ATTRIBUTES_PER_SPRITE];
    }

    /**
     * Creates the Mesh to be rendered, after this call the all the sprites in this controller can be rendered
     * by fetching the mesh and rendering it.
     * Note that this class will be set as AttributeUpdater in the mesh in order for the sprites to be displayed
     * properly.
     * 
     * @param program
     * @param texture The texture to use for sprites, must be {@link TiledTexture2D} otherwise tiling will not work.
     * @param width Width for each sprite
     * @param height Height for each sprite
     * @param z Zpos
     * @return
     */
    public void createMesh(TiledSpriteProgram program, Texture2D texture, float width, float height, float z) {
        size[0] = width;
        size[1] = height;
        anchor[2] = z;
        anchor[0] = -width / 2;
        anchor[1] = -height / 2;
        program.buildMesh(this, texture, count, size, anchor);
        setTexture(texture, Texture2D.TEXTURE_0);
        setAttributeUpdater(this);
    }

    public void createMesh(TiledSpriteProgram program, Texture2D texture, float[] dimension, float[] anchor) {
        program.buildMesh(this, texture, count, dimension, anchor);

        size[0] = dimension[Axis.X.index];
        size[1] = dimension[Axis.Y.index];
        anchor[0] = anchor[Axis.X.index];
        anchor[1] = anchor[Axis.Y.index];
        anchor[2] = anchor[Axis.Z.index];
        setTexture(texture, Texture2D.TEXTURE_0);
        setAttributeUpdater(this);

    }

    /**
     * Returns the number of sprites for this tiled spritesheet, this is the max number of sprites (quads) that
     * can be displayed.
     * 
     * @return Number of sprites in the spritesheet.
     */
    public int getCount() {
        return count;
    }

    @Override
    public void setAttributeData() {
        // TODO Index the numbers for vertice/attribute data instead of using 0/1
        VertexBuffer positions = getVerticeBuffer(BufferIndex.ATTRIBUTES);
        positions.setArray(getAttributeData(), 0, 0, count * TiledSpriteProgram.ATTRIBUTES_PER_SPRITE);
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
     * Returns the dimension of the sprites, in x and y
     * 
     * @return Width and height of sprite, at index 0 and 1 respectively.
     */
    public float[] getSize() {
        return size;
    }

    /**
     * Returns the anchor points for the sprites.
     * 
     * @return Anchor points for sprites, in x, y and z
     */
    public float[] getAnchor() {
        return anchor;
    }

    /**
     * Returns the tiled texture at the specified active texture index, for tiled sheets that only have 1 texture index
     * will always be 0
     * 
     * @param index Index to texture, starts at 0 and increases.
     * @return The tiled texture
     * @throws ArrayIndexOutOfBoundsException If index < 0 or > number of textures - 1
     */
    public TiledTexture2D getTiledTexture(int index) {
        return (TiledTexture2D) getTexture(index);
    }

}
