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
     * Returns a new instance of a tiled sprite controller.
     * Use this when importing
     * 
     * @param renderer
     * @param source
     * @source reference Reference to the playfield controller that shall be created.
     * @param scene
     * @return
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
}
