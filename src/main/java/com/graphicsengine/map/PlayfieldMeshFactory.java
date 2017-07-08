package com.graphicsengine.map;

import java.io.IOException;

import com.nucleus.assets.AssetManager;
import com.nucleus.renderer.BufferObjectsFactory;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.texturing.Texture2D;
import com.nucleus.vecmath.Rectangle;

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
    public static PlayfieldMesh create(NucleusRenderer renderer, PlayfieldNode node)
            throws IOException {

        PlayfieldProgram program = new PlayfieldProgram();
        renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer, node.getTextureRef());
        PlayfieldMesh playfieldMesh = new PlayfieldMesh();
        playfieldMesh.createMesh(program, texture, node.getMaterial(), node.getMapSize(), node.getCharRectangle());
        float[] offset = node.getAnchorOffset();
        Rectangle bounds = playfieldMesh.setupCharmap(node.getMapSize(), node.getCharRectangle().getSize(), offset);
        node.initBounds(bounds);
        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, playfieldMesh);
        }
        return playfieldMesh;
    }

}
