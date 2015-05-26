package com.graphicsengine.tiledsprite;

import com.graphicsengine.charset.TiledSetup;
import com.nucleus.geometry.AttributeUpdater;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.opengl.GLES20Wrapper.GLES20;
import com.nucleus.texturing.Texture2D;

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
    public TiledSpriteSheet(TiledSetup setup) {
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
     * @param texture The texture to use for sprites
     * @param width Width for each sprite
     * @param height Height for each sprite
     * @param z Zpos
     * @param framesX Number of frames on the x axis in the sprite sheet image.
     * @param framesY Number of frames on the y axis in the sprite sheet image.
     * @return
     */
    public void createMesh(TiledSpriteProgram program, Texture2D texture, float width, float height, float z,
            int framesX, int framesY) {
        program.buildTileSpriteMesh(this, count, width, height, z, GLES20.GL_FLOAT, 1f / framesX, 1f / framesY);
        setTexture(texture, Texture2D.TEXTURE_0);
        setAttributeUpdater(this);
    }

    @Override
    public void setAttributeData() {
        VertexBuffer positions = getVerticeBuffer(1);
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

}
