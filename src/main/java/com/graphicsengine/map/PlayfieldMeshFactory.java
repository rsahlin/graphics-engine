package com.graphicsengine.map;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshBuilder;
import com.nucleus.renderer.BufferObjectsFactory;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;

/**
 * Use to create charmaps
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldMeshFactory {

    /**
     * Factory method for creating the playfield mesh, after this call the playfield can be rendered, it must
     * be filled with map data.
     * 
     * @param renderer
     * @param node The source playfield, the mesh will be created in this node.
     * @return The created mesh
     * @throws IOException
     */
    public static PlayfieldMesh create(NucleusRenderer renderer, PlayfieldNode node,
            GraphicsEngineRootNode scene)
            throws IOException {

        Mesh refMesh = scene.getResources().getMesh(node.getMeshRef());
        TiledTexture2D textureData = (TiledTexture2D) scene.getResources().getTexture2D(
                refMesh.getTextureRef());

        PlayfieldProgram program = new PlayfieldProgram();
        renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer, textureData);
        PlayfieldMesh playfieldMesh = new PlayfieldMesh(refMesh);
        playfieldMesh.createMesh(program, texture, node.getMapSize(), node.getCharSize(), node.getAnchor());
        playfieldMesh.setupCharmap(node.getMapSize(), node.getCharSize(), node.getAnchor());

        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, playfieldMesh);
        }

        int charCount = node.getMapSize()[0] * node.getMapSize()[1];
        float[] attributeData = playfieldMesh.getAttributeData();
        for (int i = 0; i < charCount; i++) {
            MeshBuilder.prepareTiledUV(playfieldMesh.getMapper(), attributeData, i);
        }

        return playfieldMesh;
    }

    /**
     * Creates a new playfield from the controller source
     * This playfield will contain the mapdata from the source, the id will be the mapReference from the source.
     * Use this when exporting
     * 
     * @param source
     * @return
     */
    public static Playfield createPlayfield(PlayfieldNode source) {
        return new Playfield(source);
    }

}
