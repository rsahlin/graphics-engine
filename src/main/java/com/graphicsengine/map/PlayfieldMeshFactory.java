package com.graphicsengine.map;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineResourcesData;
import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.Mesh;
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
            GraphicsEngineResourcesData resources)
            throws IOException {

        Mesh refMesh = resources.getMesh(node.getMeshRef());
        TiledTexture2D textureData = (TiledTexture2D) resources.getTexture2D(
                refMesh.getTextureRef());

        PlayfieldProgram program = new PlayfieldProgram();
        renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer, textureData);
        PlayfieldMesh playfieldMesh = new PlayfieldMesh(refMesh);
        playfieldMesh.createMesh(program, texture, node.getMapSize(), node.getCharRectangle());
        float[] offset = node.getMapOffset();
        if (offset == null) {
            offset = new float[] { 0, 0 };
        }
        playfieldMesh.setupCharmap(node.getMapSize(), node.getCharRectangle().getSize(0), offset);

        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, playfieldMesh);
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
