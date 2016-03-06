package com.graphicsengine.spritemesh;

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


    /**
     * Creates a new instance of a sprite mesh node {@link SpriteMeshNode} based on the reference taken from the source.
     * The returned sprite node will be a copy of the reference.
     * Mesh and buffers will not be created.
     * 
     * @param renderer
     * @param source The source node, the returned sprite mesh node will be a copy of the reference node.
     * @param scene The scene holding resources
     * @return New instance of the referenced sprite mesh node
     */
    public static Node create(NucleusRenderer renderer, Node source, GraphicsEngineRootNode scene) {
        Node refNode = scene.getResources().getNode(GraphicsEngineNodeType.spriteMeshNode, source.getReference());
        SpriteMeshNode node = (SpriteMeshNode) SpriteNodeFactory.create(SpriteControllers.TILED);
        refNode.copyTo(node);
        return node;
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
