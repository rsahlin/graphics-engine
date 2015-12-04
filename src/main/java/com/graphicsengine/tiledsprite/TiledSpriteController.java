package com.graphicsengine.tiledsprite;

import java.io.IOException;

import com.graphicsengine.scene.GraphicsEngineSceneData;
import com.graphicsengine.sprite.Sprite;
import com.graphicsengine.sprite.SpriteController;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.SceneData;

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

    @Override
    public void createSprites(NucleusRenderer renderer, TiledSpriteControllerData spriteControllerData,
            SceneData scene) {
        create(spriteControllerData.getId(), spriteControllerData.getLogicdata().getCount());
        try {
            GraphicsEngineSceneData gScene = (GraphicsEngineSceneData) scene;
            spriteSheet = TiledSpriteFactory.create(renderer, spriteControllerData, gScene);
            addMesh(spriteSheet);
            for (int i = 0; i < count; i++) {
                sprites[i] = new TiledSprite(spriteSheet.getAttributeData(), i
                        * TiledSpriteProgram.ATTRIBUTES_PER_SPRITE);
            }
            setLogic(spriteControllerData.getLogicdata().getData());
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
    public TiledSpriteSheet getSpriteSheet() {
        return spriteSheet;
    }

    @Override
    public void play() {
        state = State.PLAY;
    }

    @Override
    public void pause() {
        state = State.PAUSE;
    }

    @Override
    public void stop() {
        state = State.STOPPED;
    }

    @Override
    public void reset() {
        state = State.STOPPED;
    }

    @Override
    public void init() {
        for (Sprite sprite : sprites) {
            sprite.logic.init(sprite);
        }
        state = State.INITIALIZED;
    }

}
