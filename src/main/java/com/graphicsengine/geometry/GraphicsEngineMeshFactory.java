package com.graphicsengine.geometry;

import java.io.IOException;

import com.graphicsengine.map.PlayfieldMeshFactory;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.scene.QuadParentNode;
import com.graphicsengine.spritemesh.SpriteMeshFactory;
import com.nucleus.assets.AssetManager;
import com.nucleus.component.ComponentNode;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshFactory;
import com.nucleus.io.ExternalReference;
import com.nucleus.renderer.BufferObjectsFactory;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D;

public class GraphicsEngineMeshFactory implements MeshFactory {

    @Override
    public Mesh createMesh(NucleusRenderer renderer, Node parent)
            throws IOException {

        if (parent instanceof PlayfieldNode) {
            return PlayfieldMeshFactory.create(renderer, (PlayfieldNode) parent);
        }
        if (parent instanceof QuadParentNode) {
            return SpriteMeshFactory.create(renderer, (QuadParentNode) parent);
        }
        if (parent instanceof ComponentNode) {
            /**
             * If ComponentNode then don't create mesh, mesh is created when create on component is called.
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

    @Override
    public Mesh createMesh(NucleusRenderer renderer, ShaderProgram program, Mesh mesh, ExternalReference textureRef)
            throws IOException {

        Texture2D texture = AssetManager.getInstance().getTexture(renderer, textureRef);
        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, mesh);
        }
        return mesh;
    }

}
