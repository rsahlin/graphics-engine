package com.graphicsengine.geometry;

import java.io.IOException;

import com.graphicsengine.map.PlayfieldMesh;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.scene.QuadParentNode;
import com.graphicsengine.scene.SharedMeshQuad;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.bounds.Bounds;
import com.nucleus.component.ComponentNode;
import com.nucleus.geometry.DefaultMeshFactory;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshFactory;
import com.nucleus.geometry.RectangleShapeBuilder;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;

/**
 * Mesh factory for graphics-engine meshes
 * This is the main entrypoint for creating graphics-engine meshes
 *
 */
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
    public Mesh createMesh(NucleusRenderer renderer, Node parent) throws IOException, GLException {

        if (parent instanceof PlayfieldNode) {
            PlayfieldNode playfield = (PlayfieldNode) parent;

            playfieldBuilder.setMap(playfield.getMapSize(), playfield.getCharRectangle());
            playfieldBuilder.setOffset(playfield.getAnchorOffset());
            playfieldBuilder.setTexture(playfield.getTextureRef());
            playfieldBuilder.setMaterial(playfield.getMaterial());
            PlayfieldMesh pmesh = (PlayfieldMesh) playfieldBuilder.create();
            Bounds bounds = playfieldBuilder.createBounds();
            parent.initBounds(bounds);
            return pmesh;
        }
        if (parent instanceof QuadParentNode) {
            QuadParentNode quadParent = (QuadParentNode) parent;
            SpriteMesh.Builder mbuilder = new SpriteMesh.Builder(renderer);
            mbuilder.setSpriteCount(quadParent.getMaxQuads());
            mbuilder.setTexture(parent.getTextureRef());
            mbuilder.setMaterial(quadParent.getMaterial());
            RectangleShapeBuilder.RectangleConfiguration config = new RectangleShapeBuilder.RectangleConfiguration(
                    quadParent.getMaxQuads(), 0);
            mbuilder.setShapeBuilder(new RectangleShapeBuilder(config));
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
