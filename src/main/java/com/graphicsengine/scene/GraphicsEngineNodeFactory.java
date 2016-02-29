package com.graphicsengine.scene;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.map.PlayfieldNodeFactory;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.graphicsengine.spritemesh.SpriteMeshNode;
import com.graphicsengine.spritemesh.SpriteMeshNodeFactory;
import com.graphicsengine.ui.UINodeFactory;
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
            created = PlayfieldNodeFactory.create(renderer, source, meshFactory, gScene);
            internalCreateNode(renderer, source, created, meshFactory, gScene);
            ((PlayfieldNode) created).createPlayfield(gScene);
            break;
        case spriteMeshNode:
            created = SpriteMeshNodeFactory.create(renderer, source, meshFactory, gScene);
            internalCreateNode(renderer, source, created, meshFactory, gScene);
            // Instead of casting - should the Mesh be attribute consumer?
            ((SpriteMeshNode) created).createSprites(renderer, (SpriteMesh) created.getMeshById(source.getReference()),
                    gScene);
            break;
        case button:
            created = UINodeFactory.createButton(renderer, source, meshFactory, gScene);
            break;
        case uinode:
            throw new IllegalArgumentException(NOT_IMPLEMENTED + type);
        default:
            throw new IllegalArgumentException(NOT_IMPLEMENTED + type);
        }
        return created;
    }

    protected void internalCreateNode(NucleusRenderer renderer, Node source, Node node, MeshFactory meshFactory,
            GraphicsEngineRootNode scene) throws IOException {
        node.create();
        node.toReference(source, node);
        Mesh mesh = meshFactory.createMesh(renderer, node, scene);
        // Check if the mesh has an id, if not set to reference
        if (mesh.getId() == null) {
            mesh.setId(source.getReference());
        }
        node.addMesh(mesh);
        node.copyTransform(source);
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
