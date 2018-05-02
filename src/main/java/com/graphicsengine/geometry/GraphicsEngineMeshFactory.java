package com.graphicsengine.geometry;

import java.io.IOException;

import com.graphicsengine.map.PlayfieldMesh;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.scene.QuadParentNode;
import com.graphicsengine.scene.SharedMeshQuad;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.bounds.Bounds;
import com.nucleus.geometry.DefaultMeshFactory;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshFactory;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.ComponentNode;
import com.nucleus.scene.Node;

/**
 * Mesh factory for graphics-engine meshes
 * This is the main entrypoint for creating graphics-engine meshes
 *
 */
public class GraphicsEngineMeshFactory extends DefaultMeshFactory implements MeshFactory {

    PlayfieldMesh.Builder playfieldBuilder;

    public GraphicsEngineMeshFactory(NucleusRenderer renderer) {
        if (renderer == null) {
            throw new IllegalArgumentException("Renderer may not be null");
        }
    }

    @Override
    public Mesh createMesh(NucleusRenderer renderer, Node parent) throws IOException, GLException {

        if (parent instanceof PlayfieldNode) {
            PlayfieldMesh.Builder builder = PlayfieldNode.createMeshBuilder(renderer, (PlayfieldNode) parent);
            PlayfieldMesh pmesh = (PlayfieldMesh) builder.create();
            Bounds bounds = builder.createBounds();
            parent.initBounds(bounds);
            return pmesh;
        }
        if (parent instanceof QuadParentNode) {
            QuadParentNode quadParent = (QuadParentNode) parent;
            SpriteMesh.Builder mbuilder = QuadParentNode.createMeshBuilder(renderer, quadParent);
            // TODO Fix generics so that cast is not needed
            SpriteMesh mesh = (SpriteMesh) mbuilder.create();
            return mesh;
        }
        if (parent instanceof ComponentNode) {
            /**
             * If ComponentNode then don't create mesh, mesh is created when create on component is called.
             */
            return null;
        }
        if (parent instanceof SharedMeshQuad) {
            // This is child to quad parent node, do not create mesh
            return null;
        }
        return super.createMesh(renderer, parent);
    }

}
