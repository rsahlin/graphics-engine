package com.graphicsengine.map;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineResourcesData;
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
    public static PlayfieldMesh create(NucleusRenderer renderer, PlayfieldNode node,
            GraphicsEngineResourcesData resources)
            throws IOException {

        // TiledTexture2D textureData = (TiledTexture2D) resources.getTexture2D(node.getTextureRef());
        PlayfieldProgram program = new PlayfieldProgram();
        renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer, node.getTextureRef());
        PlayfieldMesh playfieldMesh = new PlayfieldMesh();
        playfieldMesh.createMesh(program, texture, node.getMaterial(), node.getMapSize(), node.getCharRectangle());
        float[] offset = node.getMapOffset();
        if (offset == null) {
            offset = new float[] {
                    -(node.getMapSize()[0] >>> 1) * node.getCharRectangle().getValues()[Rectangle.WIDTH],
                    (node.getMapSize()[1] >>> 1) * node.getCharRectangle().getValues()[Rectangle.HEIGHT] };
        }
        playfieldMesh.setupCharmap(node.getMapSize(), node.getCharRectangle().getSize(), offset);

        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, playfieldMesh);
        }
        return playfieldMesh;
    }

}
