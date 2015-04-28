package com.nucleus.tiledsprite;

import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.opengl.GLES20Wrapper.GLES20;
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
public class TiledSpriteController {

    /**
     * Number of floats for each tiled sprite in the attribute data.
     */
    public final static int SPRITE_ATTRIBUTE_DATA = TiledSpriteProgram.PER_VERTEX_DATA
            * TiledSpriteProgram.VERTICES_PER_SPRITE;

    TiledSprite[] sprites;
    private Mesh mesh;
    /**
     * Contains attribute data for all sprites.
     */
    float[] attributeData;
    int count;

    /**
     * Creates a TiledSpriteController with the specified number of sprites, each sprite can be seen as a portion of the
     * Mesh it belongs to. Each tiled sprite will be created.
     * Before the sprites can be rendered the Mesh must be created, by calling createMesh()
     * 
     * @param count Number of tiled sprites to create.
     * 
     */
    public TiledSpriteController(int count) {
        this.count = count;
        sprites = new TiledSprite[count];
        attributeData = new float[count * SPRITE_ATTRIBUTE_DATA];
        int frame = 0;
        for (int i = 0; i < count; i++) {
            sprites[i] = new TiledSprite(attributeData, i * SPRITE_ATTRIBUTE_DATA);
        }

    }

    /**
     * Creates the Mesh to be rendered, after this call the all the sprites in this controller can be rendered
     * by fetching the mesh and rendering it.
     * Note that the attributeData in this class must be stored in the mesh before rendering, otherwise sprites
     * will not be updated.
     * 
     * @param program
     * @param texture
     * @param width
     * @param height
     * @param framesX Number of frames on the x axis in the sprite sheet image.
     * @param framesY Number of frames on the y axis in the sprite sheet image.
     * @return
     */
    public Mesh createMesh(TiledSpriteProgram program, Texture2D texture, float width, float height, int framesX,
            int framesY) {
        mesh = program.buildTileSpriteMesh(count, width, height, 0, GLES20.GL_FLOAT, 1f / framesX, 1f / framesY);
        mesh.setTexture(texture, Texture2D.TEXTURE_0);
        return mesh;
    }

    /**
     * Stores the attribute data from this class (position, rotation, offset) in the generic vertex bufffer
     * needed to render the mesh.
     * 
     * @return The mesh to render
     */
    public Mesh prepareToRender() {
        VertexBuffer positions = getMesh().getVerticeBuffer(1);
        positions.setArray(getData(), 0, 0, count * SPRITE_ATTRIBUTE_DATA);
        return getMesh();
    }

    /**
     * Returns the attribute data for all sprites, there is normally no need to access this data.
     * It is used by sprite implementations to update position, frame, rotation and read when the mesh is
     * rendered.
     * 
     * @return Attribute data containing generic attribute data (not vertices)
     */
    public float[] getData() {
        return attributeData;
    }

    /**
     * Returns the number of sprites in this controller
     * 
     * @return
     */
    public int getCount() {
        return count;
    }

    /**
     * Returns the array containing the sprites.
     * 
     * @return The array containing all sprites.
     */
    public TiledSprite[] getSprites() {
        return sprites;
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
