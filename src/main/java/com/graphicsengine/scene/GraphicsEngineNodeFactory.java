package com.graphicsengine.scene;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.map.PlayfieldControllerFactory;
import com.graphicsengine.spritemesh.SpriteMeshNodeFactory;
import com.nucleus.camera.ViewFrustum;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.DefaultNodeFactory;
import com.nucleus.scene.Node;
import com.nucleus.scene.NodeFactory;
import com.nucleus.scene.NodeType;
import com.nucleus.scene.RootNode;

/**
 * Implementation of {@link NodeFactory}
 * 
 * @author Richard Sahlin
 *
 */
public class GraphicsEngineNodeFactory extends DefaultNodeFactory implements NodeFactory {

    private static final String NOT_IMPLEMENTED = "Not implemented";

    @Override
    public Node create(NucleusRenderer renderer, Node source, String reference, RootNode scene) throws IOException {
        try {
            GraphicsEngineNodeType type = GraphicsEngineNodeType.valueOf(source.getType());
            GraphicsEngineRootNode gScene = (GraphicsEngineRootNode) scene;
            Node created = null;

            switch (type) {
            case playfieldNode:
                created = PlayfieldControllerFactory.create(renderer, source, reference, gScene);
                break;
            case spriteMeshNode:
                created = SpriteMeshNodeFactory.create(renderer, source, reference, gScene);
                break;
            case uinode:

                break;
            default:
                throw new IllegalArgumentException(NOT_IMPLEMENTED + type);
            }
            return created;
        } catch (IllegalArgumentException e) {
            return super.create(renderer, source, reference, scene);
        }
    }

    /**
     * Creates a new node from the source node, looking up resources as needed.
     * The new node will be returned, it is not added to the parent node - this shall be done by the caller.
     * The new node will have parent as its parent node
     * 
     * @param scene
     * @param source
     * @param node
     * @return The created node
     */
    protected Node createNode(RootNode scene, Node source, Node parent) throws IOException {
        try {
            NodeType type = NodeType.valueOf(source.getType());
            Node created = null;
            switch (type) {
            case node:
                created = new Node(source);
                break;
            default:
                throw new IllegalArgumentException(NOT_IMPLEMENTED + type);
            }
            setViewFrustum(source, created);
            createChildNodes(scene, source, created);
            return created;

        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    protected void createChildNodes(RootNode scene, Node source, Node parent) throws IOException {
        // Recursively create children
        for (Node nd : source.getChildren()) {
            Node child = createNode(scene, nd, parent);
            if (child != null) {
                parent.addChild(child);
            }
        }

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
