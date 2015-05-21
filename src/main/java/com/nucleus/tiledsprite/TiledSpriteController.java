package com.nucleus.tiledsprite;

import com.nucleus.sprite.SpriteController;

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
public class TiledSpriteController extends SpriteController {
    /**
     * Reference to float array with attribute data, this is the data that is used to update the mesh
     */
    private float[] attributeData;

    /**
     * Creates a TiledSpriteController with the specified number of sprites, each sprite can be seen as a portion of the
     * Mesh it belongs to. Each tiled sprite will be created.
     * Before the sprites can be rendered the Mesh must be created, by calling createMesh()
     * 
     * @param count Number of tiled sprites to create. Each tiled sprite will be created.
     * @param tiledSprites Ref to the tiled spritesheet.
     * 
     */
    public TiledSpriteController(int count, float[] attributeData) {
        super(count, attributeData);
    }

    @Override
    protected void createSprites(Object data) {
        attributeData = (float[]) data;
        for (int i = 0; i < count; i++) {
            sprites[i] = new TiledSprite(attributeData, i * TiledSpriteProgram.ATTRIBUTES_PER_SPRITE);
        }
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

}
