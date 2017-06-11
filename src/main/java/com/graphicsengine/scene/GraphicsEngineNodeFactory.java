package com.graphicsengine.scene;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineResourcesData;
import com.graphicsengine.map.PlayfieldNode;
import com.nucleus.camera.ViewFrustum;
import com.nucleus.component.ComponentException;
import com.nucleus.component.ComponentNode;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshFactory;
import com.nucleus.io.ResourcesData;
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
    public Node create(NucleusRenderer renderer, MeshFactory meshFactory, ResourcesData resources, Node source,
            RootNode root)
            throws NodeException {
        GraphicsEngineNodeType type = null;
        try {
            type = GraphicsEngineNodeType.valueOf(source.getType());
        } catch (IllegalArgumentException e) {
            return super.create(renderer, meshFactory, resources, source, root);
        }
        GraphicsEngineResourcesData gResources = (GraphicsEngineResourcesData) resources;
        Node created = null;

        switch (type) {
        case playfieldNode:
            created = source.copy(root);
            internalCreateNode(renderer, source, created, meshFactory, gResources);
            ((PlayfieldNode) created).createMap(gResources);
            break;
        case spriteComponentNode:
            created = source.copy(root);
            internalCreateNode(renderer, source, created, meshFactory, gResources);
            break;
        case sharedMeshNode:
            created = source.copy(root);
            internalCreateNode(renderer, source, created, meshFactory, gResources);
            break;
        case quadNode:
            created = source.copy(root);
            internalCreateNode(renderer, source, created, meshFactory, gResources);
            break;
        case element:
            created = source.copy(root);
            internalCreateNode(renderer, source, created, meshFactory, gResources);
            break;
        default:
            throw new IllegalArgumentException(NOT_IMPLEMENTED + type);
        }
        return created;
    }

    /**
     * Internal method to create node
     * 
     * @param renderer
     * @param source
     * @param node
     * @param meshFactory
     * @param resources The resources in the scene
     * @throws NodeException If there is an error creating the node
     */
    protected void internalCreateNode(NucleusRenderer renderer, Node source, Node node, MeshFactory meshFactory,
            GraphicsEngineResourcesData resources) throws NodeException {
        try {
            node.create();
            // Copy properties from source node into the created node.
            node.setProperties(source);
            Mesh mesh = meshFactory.createMesh(renderer, node, resources);
            node.copyTransform(source);
            if (mesh != null) {
                node.addMesh(mesh);
            }
            if (node instanceof ComponentNode) {
                internalCreateComponents(renderer, (ComponentNode) node, meshFactory, resources);
            }
        } catch (IOException | ComponentException e) {
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
     * @param resources
     * @throws ComponentException
     */
    protected void internalCreateComponents(NucleusRenderer renderer, ComponentNode node, MeshFactory meshFactory,
            GraphicsEngineResourcesData resources) throws ComponentException {
        node.createComponents(renderer, resources);
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
