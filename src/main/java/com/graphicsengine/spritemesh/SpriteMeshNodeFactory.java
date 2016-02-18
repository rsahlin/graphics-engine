package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.graphicsengine.sprite.SpriteNodeFactory;
import com.graphicsengine.sprite.SpriteNodeFactory.SpriteControllers;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;

/**
 * Creates new instances of tiled sprite nodes, use this when importing data
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
            SpriteMeshNode spriteNode = (SpriteMeshNode) SpriteNodeFactory.create(SpriteControllers.TILED);
            spriteNode.set(refNode);
            spriteNode.create();
            spriteNode.toReference(source, spriteNode);
            spriteNode.createMesh(renderer, spriteNode, scene);
            spriteNode.copyTransform(source);
            spriteNode.createSprites(renderer, scene);

            return spriteNode;
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
