package com.graphicsengine.geometry;

import java.io.IOException;

import com.graphicsengine.map.PlayfieldMesh;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.scene.QuadParentNode;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.bounds.Bounds;
import com.nucleus.component.ComponentNode;
import com.nucleus.geometry.DefaultMeshFactory;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshFactory;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;

public class GraphicsEngineMeshFactory extends DefaultMeshFactory implements MeshFactory {

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
            PlayfieldNode playfield = (PlayfieldNode) parent;
            PlayfieldMesh.Builder mbuilder = new PlayfieldMesh.Builder(renderer);
            mbuilder.setMap(playfield.getMapSize(), playfield.getCharRectangle());
            mbuilder.setOffset(playfield.getAnchorOffset());
            mbuilder.setTexture(playfield.getTextureRef());
            mbuilder.setMaterial(playfield.getMaterial());
            PlayfieldMesh pmesh = mbuilder.create();
            Bounds bounds = mbuilder.createBounds();
            parent.initBounds(bounds);
            return pmesh;
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


}
