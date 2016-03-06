package com.graphicsengine.map;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;

/**
 * Creates playfield controllers that can be put in a scene as nodes and rendered.
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldNodeFactory {

    /**
     * Returns a new instance of the playfield node, the returned {@link PlayfieldNode} will be a new instance
     * copy of the referenced node.
     * Mesh and buffers will not be created.
     * 
     * @param renderer
     * @param source The source node, the returned playfield will be a copy of the reference node.
     * @param scene The scene holding the resources
     * @return New instance of the playfield node.
     */
    public static Node create(NucleusRenderer renderer, Node source, GraphicsEngineRootNode scene) {
        PlayfieldNode refNode = (PlayfieldNode) scene.getResources().getNode(GraphicsEngineNodeType.playfieldNode,
                source.getReference());
        PlayfieldNode node = new PlayfieldNode(refNode);
        return node;
    }

    /**
     * Returns a copy of the playfield controller, mesh and buffers will not be created.
     * Use this when exporting/importing
     * 
     * @param source
     * @return
     */
    public static PlayfieldNode copy(PlayfieldNode source) {
        return new PlayfieldNode(source);
    }

}
