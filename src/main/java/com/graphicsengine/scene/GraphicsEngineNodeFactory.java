package com.graphicsengine.scene;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineResourcesData;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.graphicsengine.spritemesh.SpriteMeshNode;
import com.nucleus.camera.ViewFrustum;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshFactory;
import com.nucleus.io.ResourcesData;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.DefaultNodeFactory;
import com.nucleus.scene.Node;
import com.nucleus.scene.NodeFactory;

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
    public Node create(NucleusRenderer renderer, MeshFactory meshFactory, ResourcesData resources, Node source)
            throws IOException {
        GraphicsEngineNodeType type = null;
        try {
            type = GraphicsEngineNodeType.valueOf(source.getType());
        } catch (IllegalArgumentException e) {
            return super.create(renderer, meshFactory, resources, source);
        }
        GraphicsEngineResourcesData gResources = (GraphicsEngineResourcesData) resources;
        Node created = null;

        switch (type) {
        case playfieldNode:
            // PlayfieldNode playfieldNode = (PlayfieldNode) gScene.getResources().getNode(
            // GraphicsEngineNodeType.playfieldNode, source.getReference());
            created = source.copy();
            internalCreateNode(renderer, source, created, meshFactory, gResources);
            ((PlayfieldNode) created).createPlayfield(gResources);
            break;
        case spriteMeshNode:
            // SpriteMeshNode spriteMeshNode = (SpriteMeshNode) gScene.getResources().getNode(
            // GraphicsEngineNodeType.spriteMeshNode,
            // source.getReference());
            // This will set the actor resolver
            created = source.copy();
            internalCreateNode(renderer, source, created, meshFactory, gResources);
            // Instead of casting - should the Mesh be attribute consumer?
            ((SpriteMeshNode) created).createSprites(renderer, (SpriteMesh) created.getMeshById(created.getMeshRef()),
                    gResources);
            break;
        case sharedMeshNode:
            // SharedMeshQuad sharedQuad = (SharedMeshQuad) gScene.getResources().getNode(
            // GraphicsEngineNodeType.sharedMeshNode,
            // source.getReference());
            created = source.copy();
            internalCreateNode(renderer, source, created, meshFactory, gResources);
            break;
        case quadNode:
            // QuadParentNode quadParent = (QuadParentNode)
            // gScene.getResources().getNode(GraphicsEngineNodeType.quadNode,
            // source.getReference());
            created = source.copy();
            internalCreateNode(renderer, source, created, meshFactory, gResources);
            break;
        case element:
            // Element element = (Element) gScene.getResources().getNode(
            // GraphicsEngineNodeType.element,
            // source.getReference());
            created = source.copy();
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
     * @throws IOException
     */
    protected void internalCreateNode(NucleusRenderer renderer, Node source, Node node, MeshFactory meshFactory,
            GraphicsEngineResourcesData resources) throws IOException {
        node.create();
        // Copy properties from source node into the created node.
        node.setProperties(source);
        Mesh mesh = meshFactory.createMesh(renderer, node, resources);
        node.copyTransform(source);
        if (mesh != null) {
            node.addMesh(mesh);
        }

    }

    protected void createChildNodes(NucleusRenderer renderer, Node node, MeshFactory meshFactory,
            GraphicsEngineResourcesData resources) throws IOException {
        // Recursively create children
        for (Node nd : node.getChildren()) {
            Node child = create(renderer, meshFactory, resources, nd);
            if (child != null) {
                node.addChild(child);
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
