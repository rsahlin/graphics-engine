package com.graphicsengine.geometry;

import java.io.IOException;

import com.graphicsengine.map.PlayfieldMesh;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.scene.QuadParentNode;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.assets.AssetManager;
import com.nucleus.component.ComponentNode;
import com.nucleus.geometry.Material;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshFactory;
import com.nucleus.io.ExternalReference;
import com.nucleus.renderer.BufferObjectsFactory;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;
import com.nucleus.texturing.Texture2D;

public class GraphicsEngineMeshFactory implements MeshFactory {

    PlayfieldMesh.Builder playfieldBuilder;
    SpriteMesh.Builder spriteMeshBuilder;

    public GraphicsEngineMeshFactory(NucleusRenderer renderer) {
        if (renderer == null) {
            throw new IllegalArgumentException("Renderer may not be null");
        }
        playfieldBuilder = new PlayfieldMesh.Builder(renderer);
        spriteMeshBuilder = new SpriteMesh.Builder(renderer);
    }


    @Override
    public Mesh createMesh(NucleusRenderer renderer, Node parent)
            throws IOException {

        if (parent instanceof PlayfieldNode) {
            return playfieldBuilder.create((PlayfieldNode) parent);
        }
        if (parent instanceof QuadParentNode) {
            return spriteMeshBuilder.create((QuadParentNode) parent);
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
    public Mesh createMesh(NucleusRenderer renderer, Material material, ExternalReference textureRef, int vertexCount,
            int indiceCount)
            throws IOException {

        Texture2D texture = AssetManager.getInstance().getTexture(renderer, textureRef);
        Mesh mesh = new Mesh();
        mesh.createMesh(texture, material, vertexCount, indiceCount);
        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, mesh);
        }
        return mesh;
    }

}
