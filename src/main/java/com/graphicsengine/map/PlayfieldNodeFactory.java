package com.graphicsengine.map;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.nucleus.geometry.MeshFactory;
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
     * Returns a new instance of the playfield controller, the returned playfieldcontroller will be a new instance
     * with data collected from the source references.
     * Use this method when importing.
     * 
     * @param renderer
     * @param source The source node for the returned instance.
     * @param scene The scene holding the resources
     * @return New instance of the referenced playfield controlller
     */
    public static PlayfieldNode create(NucleusRenderer renderer, Node source, MeshFactory meshFactory,
            GraphicsEngineRootNode scene)
            throws IOException {
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
