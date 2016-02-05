package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.graphicsengine.sprite.SpriteControllerFactory;
import com.graphicsengine.sprite.SpriteControllerFactory.SpriteControllers;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D;

/**
 * Creates new instances of tiled spritecontroller, use this when importing data
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteMeshNodeFactory {

    private final static String INVALID_TYPE = "Invalid type: ";

    /**
     * Returns a new instance of a tiled sprite controller, mesh and sprites will be created from the source.
     * Use this when importing
     * 
     * @param renderer
     * @param source The source node to the sprite controller
     * @param scene The graphics engine root node
     * @return The created sprite controller that can be used to render sprites.
     * @throws IOException
     */
    public static SpriteMeshNode create(NucleusRenderer renderer, Node source, GraphicsEngineRootNode scene)
            throws IOException {
        String reference = source.getReference();
        try {
            SpriteMeshNode refNode = (SpriteMeshNode) scene.getResources().getNode(
                    GraphicsEngineNodeType.spriteMeshNode, reference);
            SpriteMeshNode spriteController = (SpriteMeshNode) SpriteControllerFactory.create(
                    SpriteControllers.TILED, refNode);
            spriteController.set(refNode);
            spriteController.toReference(source, spriteController);
            ShaderProgram program = null;
            Texture2D tex = scene.getResources().getTexture2D(spriteController.getSpriteSheet().getTextureRef());
            switch (tex.type) {
            case TiledTexture2D:
                program = new TiledSpriteProgram();
                break;
            case UVTexture2D:
                program = new UVSpriteProgram();
                break;
            default:
                throw new IllegalArgumentException(INVALID_TYPE + tex.type);
            }
            spriteController.createMesh(renderer, spriteController, program, scene);
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
    public static SpriteMeshNode copy(SpriteMeshNode source) {
        return new SpriteMeshNode(source);
    }

}
