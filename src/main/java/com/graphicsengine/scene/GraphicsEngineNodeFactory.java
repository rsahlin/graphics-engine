package com.graphicsengine.scene;

import com.graphicsengine.map.PlayfieldNode;
import com.nucleus.camera.ViewFrustum;
import com.nucleus.component.ComponentException;
import com.nucleus.component.ComponentNode;
import com.nucleus.geometry.MeshFactory;
import com.nucleus.renderer.NucleusRenderer;
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
    public Node create(NucleusRenderer renderer, MeshFactory meshFactory, Node source,
            RootNode root) throws NodeException {
        if (source.getType() == null) {
            throw new NodeException("Type not set in source node - was it created programatically?");
        }
        GraphicsEngineNodeType type = null;
        try {
            type = GraphicsEngineNodeType.valueOf(source.getType());
        } catch (IllegalArgumentException e) {
            return super.create(renderer, meshFactory, source, root);
        }
        Node created = internalCreateNode(renderer, root, source, meshFactory);
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
    protected Node internalCreateNode(NucleusRenderer renderer, RootNode root, Node source, MeshFactory meshFactory)
            throws NodeException {
        Node node = super.internalCreateNode(renderer, root, source, meshFactory);
        try {
            if (node instanceof ComponentNode) {
                internalCreateComponents(renderer, (ComponentNode) node, meshFactory);
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
     * @param renderer
     * @param node
     * @param meshFactory
     * @throws ComponentException
     */
    protected void internalCreateComponents(NucleusRenderer renderer, ComponentNode node, MeshFactory meshFactory)
            throws ComponentException {
        node.createComponents(renderer);
    }

    /**
     * Checks if the node data has viewfrustum data, if it has it is set in the node.
     * 
     * @param source The source node containing the viewfrustum
     * @param node Node to check, or null
     */
    @Override
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
