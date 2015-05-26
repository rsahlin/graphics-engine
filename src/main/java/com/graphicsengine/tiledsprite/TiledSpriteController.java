package com.graphicsengine.tiledsprite;

import java.io.IOException;

import com.graphicsengine.charset.TiledSetup;
import com.graphicsengine.sprite.SpriteController;
import com.graphicsengine.sprite.SpriteControllerSetup;
import com.nucleus.geometry.Mesh;
import com.nucleus.renderer.NucleusRenderer;

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

    private TiledSpriteSheet spriteSheet;

    /**
     * Creates the spritesheet used by this controller, this must be called before the sprites can be rendered.
     * 
     * @param renderer
     * @param constructor
     */
    public void createMesh(NucleusRenderer renderer, TiledSetup constructor) throws IOException {
        spriteSheet = TiledSpriteFactory.create(renderer, constructor);
    }

    @Override
    public void createSprites(NucleusRenderer renderer, SpriteControllerSetup setup) {
        validateResolver();
        create(setup.getId(), setup.getCount());
        try {
            createMesh(renderer, ((TiledSpriteSetup) setup).getTiledSetup());
            addMesh(spriteSheet);
            for (int i = 0; i < count; i++) {
                sprites[i] = new TiledSprite(spriteSheet.getAttributeData(), i
                        * TiledSpriteProgram.ATTRIBUTES_PER_SPRITE);
            }
            setLogic(setup);
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    /**
     * Returns the renderable object for this spritecontroller.
     * 
     * @return
     */
    public Mesh getSpriteSheet() {
        return spriteSheet;
    }

}
