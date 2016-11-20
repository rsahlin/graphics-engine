package com.graphicsengine.geometry;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineResourcesData;
import com.graphicsengine.map.PlayfieldMeshFactory;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.scene.QuadParentNode;
import com.graphicsengine.scene.SharedMeshQuad;
import com.graphicsengine.spritemesh.SpriteMeshFactory;
import com.graphicsengine.spritemesh.SpriteMeshNode;
import com.nucleus.actor.ComponentNode;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshFactory;
import com.nucleus.io.ResourcesData;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;

public class GraphicsEngineMeshFactory implements MeshFactory {

    @Override
    public Mesh createMesh(NucleusRenderer renderer, Node parent, ResourcesData resources)
            throws IOException {

        if (parent instanceof PlayfieldNode) {
            return PlayfieldMeshFactory.create(renderer, (PlayfieldNode) parent,
                    (GraphicsEngineResourcesData) resources);
        }
        if (parent instanceof SpriteMeshNode) {
            return SpriteMeshFactory.create(renderer, (SpriteMeshNode) parent, (GraphicsEngineResourcesData) resources);
        }
        if (parent instanceof QuadParentNode) {
            return SpriteMeshFactory.create(renderer, (QuadParentNode) parent, (GraphicsEngineResourcesData) resources);
        }
        if (parent instanceof SharedMeshQuad) {
            return SpriteMeshFactory.create(renderer, (SharedMeshQuad) parent, (GraphicsEngineResourcesData) resources);
        }
        if (parent instanceof ComponentNode) {
            /**
             * If ComponentNode then don't create mesh
             */
            return null;
        }
        if (parent instanceof Node) {
            /**
             * If Node then don't create mesh
             */
            return null;
        }
        throw new IllegalArgumentException("Not implemented support for " + parent.getClass().getName());
    }


}
