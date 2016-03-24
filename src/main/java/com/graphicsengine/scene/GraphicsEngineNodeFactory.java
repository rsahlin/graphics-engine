package com.graphicsengine.scene;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.graphicsengine.spritemesh.SpriteMeshNode;
import com.nucleus.camera.ViewFrustum;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshFactory;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.DefaultNodeFactory;
import com.nucleus.scene.Node;
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
    public Node create(NucleusRenderer renderer, Node source, MeshFactory meshFactory, RootNode scene)
            throws IOException {
        GraphicsEngineNodeType type = null;
        try {
            type = GraphicsEngineNodeType.valueOf(source.getType());
        } catch (IllegalArgumentException e) {
            return super.create(renderer, source, meshFactory, scene);
        }
        GraphicsEngineRootNode gScene = (GraphicsEngineRootNode) scene;
        Node created = null;

        switch (type) {
        case playfieldNode:
            PlayfieldNode playfieldNode = (PlayfieldNode) gScene.getResources().getNode(
                    GraphicsEngineNodeType.playfieldNode, source.getReference());
            created = playfieldNode.copy();
            internalCreateNode(renderer, source, created, meshFactory, gScene);
            ((PlayfieldNode) created).createPlayfield(gScene);
            break;
        case spriteMeshNode:
            SpriteMeshNode spriteMeshNode = (SpriteMeshNode) gScene.getResources().getNode(
                    GraphicsEngineNodeType.spriteMeshNode,
                    source.getReference());
            // This will set the actor resolver
            created = spriteMeshNode.copy();
            internalCreateNode(renderer, source, created, meshFactory, gScene);
            // Instead of casting - should the Mesh be attribute consumer?
            ((SpriteMeshNode) created).createSprites(renderer, (SpriteMesh) created.getMeshById(created.getMeshRef()),
                    gScene);
            break;
        case sharedMeshNode:
            SharedMeshQuad button = (SharedMeshQuad) gScene.getResources().getNode(
                    GraphicsEngineNodeType.sharedMeshNode,
                    source.getReference());
            created = button.copy();
            internalCreateNode(renderer, source, created, meshFactory, gScene);
            break;
        case quadNode:
            QuadParentNode uiNode = (QuadParentNode) gScene.getResources().getNode(GraphicsEngineNodeType.quadNode,
                    source.getReference());
            created = uiNode.copy();
            internalCreateNode(renderer, source, created, meshFactory, gScene);
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
     * @param scene
     * @throws IOException
     */
    protected void internalCreateNode(NucleusRenderer renderer, Node source, Node node, MeshFactory meshFactory,
            GraphicsEngineRootNode scene) throws IOException {
        node.create();
        node.toReference(source, node);
        Mesh mesh = meshFactory.createMesh(renderer, node, scene);
        if (mesh == null) {
            return;
        }
        // Check if the mesh has an id, if not set to reference
        if (mesh.getId() == null) {
            mesh.setId(source.getReference());
        }
        node.addMesh(mesh);
        node.copyTransform(source);
    }

    protected void createChildNodes(NucleusRenderer renderer, Node node, MeshFactory meshFactory,
            GraphicsEngineRootNode scene) throws IOException {
        // Recursively create children
        for (Node nd : node.getChildren()) {
            Node child = create(renderer, nd, meshFactory, scene);
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
