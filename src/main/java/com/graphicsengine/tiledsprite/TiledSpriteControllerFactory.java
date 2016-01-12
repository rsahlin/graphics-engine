package com.graphicsengine.tiledsprite;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.sprite.SpriteControllerFactory;
import com.graphicsengine.sprite.SpriteControllerFactory.SpriteControllers;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;

/**
 * Creates new instances of tiled spritecontroller, use this when importing data
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSpriteControllerFactory {

    /**
     * Returns a new instance of a tiled sprite controller, mesh and sprites will be created from the source.
     * Use this when importing
     * 
     * @param renderer
     * @param source The source node to the sprite controller
     * @source reference Reference to the sprite controller that shall be created.
     * @param scene
     * @return The created sprite controller that can be used to render sprites.
     * @throws IOException
     */
    public static TiledSpriteController create(NucleusRenderer renderer, Node source,
            String reference, GraphicsEngineRootNode scene) throws IOException {

        try {
            TiledSpriteController refNode = scene.getResources().getTiledSpriteController(reference);
            TiledSpriteController spriteController = (TiledSpriteController) SpriteControllerFactory.create(
                    SpriteControllers.TILED, refNode);
            spriteController.set(refNode);
            spriteController.toReference(source, spriteController);
            spriteController.createMesh(renderer, spriteController, scene);
            spriteController.copyTransform(source);
            spriteController.createSprites(renderer, spriteController, scene);
            return spriteController;
        } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns an instance copy of the source sprite controller, mesh and sprites will NOT be created.
     * Use this when exporting/importing
     * 
     * @param source
     * @return
     */
    public static TiledSpriteController copy(TiledSpriteController source) {
        return new TiledSpriteController(source);
    }

}
