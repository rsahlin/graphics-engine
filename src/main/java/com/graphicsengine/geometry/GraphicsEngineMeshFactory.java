package com.graphicsengine.geometry;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.map.PlayfieldMeshFactory;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.spritemesh.SpriteMeshFactory;
import com.graphicsengine.spritemesh.SpriteMeshNode;
import com.graphicsengine.ui.Button;
import com.graphicsengine.ui.UIMeshFactory;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshFactory;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;

public class GraphicsEngineMeshFactory implements MeshFactory {

    @Override
    public Mesh createMesh(NucleusRenderer renderer, Node parent, RootNode scene) throws IOException {

        if (parent instanceof PlayfieldNode) {
            return PlayfieldMeshFactory.create(renderer, (PlayfieldNode) parent, (GraphicsEngineRootNode) scene);
        }
        if (parent instanceof SpriteMeshNode) {
            return SpriteMeshFactory.create(renderer, (SpriteMeshNode) parent, (GraphicsEngineRootNode) scene);
        }
        if (parent instanceof Button) {
            return UIMeshFactory.create(renderer, (Button) parent, (GraphicsEngineRootNode) scene);
        }
        return null;
    }
}
