package com.graphicsengine.scene;

import com.graphicsengine.map.PlayfieldNode;
import com.nucleus.component.ComponentException;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.scene.ComponentNode;
import com.nucleus.scene.DefaultNodeFactory;
import com.nucleus.scene.Node;
import com.nucleus.scene.NodeException;
import com.nucleus.scene.NodeFactory;
import com.nucleus.scene.RootNode;

/**
 * Implementation of {@link NodeFactory}.
 * This factory shall handle all graphics-engine specific nodes.
 * 
 * @author Richard Sahlin
 *
 */
public class GraphicsEngineNodeFactory extends DefaultNodeFactory implements NodeFactory {

    private static final String NOT_IMPLEMENTED = "Not implemented: ";

    @Override
    public Node create(GLES20Wrapper gles, Node source, RootNode root) throws NodeException {
        if (source.getType() == null) {
            throw new NodeException("Type not set in source node - was it created programatically?");
        }
        GraphicsEngineNodeType type = null;
        try {
            type = GraphicsEngineNodeType.valueOf(source.getType());
        } catch (IllegalArgumentException e) {
            return super.create(gles, source, root);
        }
        Node created = internalCreateNode(gles, root, source);
        switch (type) {
            case playfieldNode:
                ((PlayfieldNode) created).createMap();
                break;
            case spriteComponentNode:
            case sharedMeshNode:
            case quadNode:
            case element:
                break;
            default:
                throw new IllegalArgumentException(NOT_IMPLEMENTED + type);
        }
        return created;
    }

    @Override
    protected Node internalCreateNode(GLES20Wrapper gles, RootNode root, Node source)
            throws NodeException {
        Node node = super.internalCreateNode(gles, root, source);
        try {
            if (node instanceof ComponentNode) {
                internalCreateComponents(gles, (ComponentNode) node);
            }
            return node;
        } catch (ComponentException e) {
            throw new NodeException(e);
        }
    }

    /**
     * Creates the components in the node, this could for instance mean creating the mesh that is
     * needed by component.
     * 
     * @param gles
     * @param node
     * @throws ComponentException
     */
    protected void internalCreateComponents(GLES20Wrapper gles, ComponentNode node)
            throws ComponentException {
        node.createComponents(gles);
    }

}
