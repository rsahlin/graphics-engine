package com.graphicsengine.tiledsprite;

import java.io.IOException;

import com.graphicsengine.charset.TiledSheetSetup;
import com.graphicsengine.scene.GraphicsEngineSceneData;
import com.graphicsengine.sprite.Sprite;
import com.graphicsengine.sprite.SpriteController;
import com.graphicsengine.sprite.SpriteControllerSetup;
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

    /**
     * Creates the spritesheet used by this controller, this must be called before the sprites can be rendered.
     * 
     * @param renderer
     * @param constructor
     */
    public void createMesh(NucleusRenderer renderer, TiledSheetSetup constructor) throws IOException {
        spriteSheet = TiledSpriteFactory.create(renderer, constructor);
    }

    /**
     * Sets the logic for the sprites as defined in the setup class.
     * The sprites must be created before calling this method.
     * 
     * @param setup Setup containing the logic id, offset and count, for the sprites.
     */
    public void setLogic(SpriteControllerSetup setup) {
        setLogic(setup.getLogicId(), setup.getLogicOffset(), setup.getLogicCount());
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
