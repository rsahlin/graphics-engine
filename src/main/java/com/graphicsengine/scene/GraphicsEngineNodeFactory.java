package com.graphicsengine.scene;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.map.PlayfieldNodeFactory;
import com.graphicsengine.spritemesh.SpriteMeshNodeFactory;
import com.graphicsengine.ui.UINodeFactory;
import com.nucleus.camera.ViewFrustum;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.DefaultNodeFactory;
import com.nucleus.scene.Node;
import com.nucleus.scene.NodeFactory;
import com.nucleus.scene.RootNode;

/**
 * Implementation of {@link NodeFactory}
 * 
 * @author Richard Sahlin
 *
 */
public class GraphicsEngineNodeFactory extends DefaultNodeFactory implements NodeFactory {

    private static final String NOT_IMPLEMENTED = "Not implemented: ";

    @Override
    public Node create(NucleusRenderer renderer, Node source, RootNode scene) throws IOException {
        GraphicsEngineNodeType type = null;
        try {
            type = GraphicsEngineNodeType.valueOf(source.getType());
        } catch (IllegalArgumentException e) {
            return super.create(renderer, source, scene);
        }
        GraphicsEngineRootNode gScene = (GraphicsEngineRootNode) scene;
        Node created = null;

        switch (type) {
        case playfieldNode:
            created = PlayfieldNodeFactory.create(renderer, source, gScene);
            break;
        case spriteMeshNode:
            created = SpriteMeshNodeFactory.create(renderer, source, gScene);
            break;
        case button:
            created = UINodeFactory.createButton(renderer, source, gScene);
            break;
        case uinode:
            throw new IllegalArgumentException(NOT_IMPLEMENTED + type);
        default:
            throw new IllegalArgumentException(NOT_IMPLEMENTED + type);
        }
        return created;
    }

    /**
     * Checks if the node data has viewfrustum data, if it has it is set in the node.
     * 
     * @param source The source node containing the viewfrustum
     * @param node Node to check, or null
     */
    protected void setViewFrustum(Node source, Node node) {
        if (node == null) {
            return;
        }
        ViewFrustum projection = source.getViewFrustum();
        if (projection == null) {
            return;
        }
        node.setViewFrustum(new ViewFrustum(projection));
    }

}
