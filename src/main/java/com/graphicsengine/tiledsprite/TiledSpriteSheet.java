package com.graphicsengine.tiledsprite;

import com.graphicsengine.charset.TiledSheetSetup;
import com.nucleus.geometry.AttributeUpdater;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.opengl.GLES20Wrapper.GLES20;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;

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

    /**
     * Creates a new tiledspritesheet from data in the setup class.
     * 
     * @param setup Config of the tiledspritesheet, number of sprites etc.
     */
    public TiledSpriteSheet(TiledSheetSetup setup) {
        setup(setup.getTileCount());
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
        program.buildTileSpriteMesh(this, texture, count, width, height, z, GLES20.GL_FLOAT);
        size[0] = width;
        size[1] = height;
        anchor[2] = z;
        anchor[0] = -width / 2;
        anchor[1] = -height / 2;
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

}
