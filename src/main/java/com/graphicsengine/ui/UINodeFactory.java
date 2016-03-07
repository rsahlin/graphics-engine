package com.graphicsengine.ui;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;

/**
 * Factory class for creating the UI nodes
 * 
 * @author Richard Sahlin
 *
 */
public class UINodeFactory {

    /**
     * Returns a new instance of the playfield controller, the returned {@link PlayfieldNode} will be a new instance
     * copy of the referenced node. Mesh and buffers will not be created.
     * 
     * @param renderer
     * @param source The source node, the returned playfield will be a copy of the reference node.
     * @param scene The scene holding the resources
     * @return New instance of the playfield node.
     */
    public static Node create(NucleusRenderer renderer, Node source, GraphicsEngineRootNode scene)
            throws IOException {
        Button refNode = (Button) scene.getResources().getNode(GraphicsEngineNodeType.button, source.getReference());
        Button node = new Button(refNode);
        return node;
    }

}
