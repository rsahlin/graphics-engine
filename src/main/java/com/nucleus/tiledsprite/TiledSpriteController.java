package com.nucleus.tiledsprite;

import com.nucleus.geometry.AttributeUpdater;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.opengl.GLES20Wrapper.GLES20;
import com.nucleus.sprite.SpriteController;
import com.nucleus.texturing.Texture2D;

/**
 * Controller for tiled sprites, this controller creates the tiled sprite objects.
 * A tiled sprite (quad) can be drawn in one draw call together with a large number of other sprites (they share the
 * same Mesh).
 * This is to allow a very large number of sprites in just 1 draw call to the underlying render API (OpenGLES).
 * Performance is increased, but all sprites must share the same texture atlas.
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSpriteController extends SpriteController implements AttributeUpdater {

    private Mesh mesh;
    /**
     * Contains attribute data for all sprites.
     * This data must be mapped into the mesh for changes to take place.
     */
    float[] attributeData;

    /**
     * Creates a TiledSpriteController with the specified number of sprites, each sprite can be seen as a portion of the
     * Mesh it belongs to. Each tiled sprite will be created.
     * Before the sprites can be rendered the Mesh must be created, by calling createMesh()
     * 
     * @param count Number of tiled sprites to create. Each tiled sprite will be created.
     * 
     */
    public TiledSpriteController(int count) {
        super(count);
    }

    @Override
    protected void createSprites() {
        attributeData = new float[count * TiledSpriteProgram.ATTRIBUTES_PER_SPRITE];
        for (int i = 0; i < count; i++) {
            sprites[i] = new TiledSprite(attributeData, i * TiledSpriteProgram.ATTRIBUTES_PER_SPRITE);
        }
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
    public Mesh createMesh(TiledSpriteProgram program, Texture2D texture, float width, float height, float z,
            int framesX,
            int framesY) {
        mesh = program.buildTileSpriteMesh(count, width, height, z, GLES20.GL_FLOAT, 1f / framesX, 1f / framesY);
        mesh.setTexture(texture, Texture2D.TEXTURE_0);
        mesh.setAttributeUpdater(this);
        return mesh;
    }

    @Override
    public void setAttributeData() {
        VertexBuffer positions = getMesh().getVerticeBuffer(1);
        positions.setArray(getAttributeData(), 0, 0, count * TiledSpriteProgram.ATTRIBUTES_PER_SPRITE);
    }

    @Override
    public float[] getAttributeData() {
        return attributeData;
    }

    /**
     * Returns the number of sprites in this controller
     * 
     * @return
     */
    @Override
    public int getCount() {
        return count;
    }

    /**
     * Returns the mesh that will render all the sprites.
     * 
     * @return Mesh with all sprites, render this to get sprites on screen.
     */
    public Mesh getMesh() {
        return mesh;
    }

}
