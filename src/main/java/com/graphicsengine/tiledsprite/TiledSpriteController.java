package com.graphicsengine.tiledsprite;

import java.io.IOException;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.geometry.TiledMesh;
import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.sprite.Sprite;
import com.graphicsengine.sprite.SpriteController;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.RootNode;

/**
 * Controller for tiled sprites, this controller creates the tiled sprite objects.
 * A tiled sprite (quad) can be drawn in one draw call together with a large number of other sprites (they share the
 * same Mesh).
 * This is to allow a very large number of sprites in just 1 draw call to the underlying render API (OpenGLES).
 * Depending on what shader program is used with this class the sprites will have different behavior.
 * {@link TiledSpriteProgram}
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSpriteController extends SpriteController {

    /**
     * The mesh that can be redered
     * TODO Unify all controllers that renders a Mesh, with methods for creating the mesh
     */
    @SerializedName("charset")
    private TiledMesh spriteSheet;

    /**
     * Default constructor
     */
    public TiledSpriteController() {
        super();
    }

    public TiledSpriteController(TiledSpriteController source) {
        set(source);
    }

    /**
     * Sets the data in this class from the source, do not set transient values
     * This is used when importing
     * 
     * @param source The source to copy
     */
    protected void set(TiledSpriteController source) {
        super.set(source);
        spriteSheet = new TiledMesh(source.getSpriteSheet());
    }

    @Override
    public void createSprites(NucleusRenderer renderer, SpriteController source, RootNode scene) {
        TiledSpriteController spriteController = (TiledSpriteController) source;
        create(spriteController.getLogicData().getCount());
        for (int i = 0; i < count; i++) {
            sprites[i] = new TiledSprite(spriteSheet.getAttributeData(), i
                    * TiledSpriteProgram.ATTRIBUTES_PER_SPRITE);
        }
        setLogic(spriteController.getLogicData().getData());
    }

    @Override
    public void createMesh(NucleusRenderer renderer, SpriteController source, RootNode scene) {
        try {
            GraphicsEngineRootNode gScene = (GraphicsEngineRootNode) scene;
            spriteSheet = TiledSpriteFactory.create(renderer, (TiledSpriteController) source, gScene);
            addMesh(spriteSheet);
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
    public TiledMesh getSpriteSheet() {
        return spriteSheet;
    }

    @Override
    public void play() {
        controllerState = State.PLAY;
    }

    @Override
    public void pause() {
        controllerState = State.PAUSE;
    }

    @Override
    public void stop() {
        controllerState = State.STOPPED;
    }

    @Override
    public void reset() {
        controllerState = State.STOPPED;
    }

    @Override
    public void init() {
        for (Sprite sprite : sprites) {
            sprite.logic.init(sprite);
        }
        controllerState = State.INITIALIZED;
    }

}
