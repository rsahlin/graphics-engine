package com.graphicsengine.map;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineSceneData;
import com.nucleus.assets.AssetManager;
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
public class PlayfieldFactory {

    /**
     * Factory method for creating the playfield mesh.
     * After this call the playfield can be rendered but it will not contain any specific charmap data.
     * 
     * @param renderer
     * @param source The charset data
     * @param textureData The texture data to be used for the charset
     * @return The created playfield
     * @throws IOException
     */
    public static PlayfieldMesh create(NucleusRenderer renderer, PlayfieldController parent,
            TiledTexture2D textureData) throws IOException {

        PlayfieldMesh source = parent.getPlayfieldMesh();
        PlayfieldMesh map = new PlayfieldMesh(source);
        PlayfieldProgram program = new PlayfieldProgram();
        renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer, textureData);
        map.createMesh(program, texture);
        float[] size = new float[2];
        int[] mapSize = parent.getMapSize();
        size[0] = mapSize[0] * source.getTileWidth();
        size[1] = mapSize[1] * source.getTileHeight();
        map.setupCharmap(parent.getMapSize());

        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, map);
        }

        return map;
    }

    /**
     * Factory method for creating the playfield mesh, after this call the playfield can be rendered, it must
     * be filled with map data.
     * 
     * @param renderer
     * @param source The source playfield controller, the mesh will be created based on the data in this.
     * @return The created mesh
     * @throws IOException
     */
    public static PlayfieldMesh create(NucleusRenderer renderer, PlayfieldController source,
            GraphicsEngineSceneData scene)
            throws IOException {

        TiledTexture2D textureData = (TiledTexture2D) scene.getResources().getTexture2DData(
                source.getPlayfieldMesh().getTextureRef());

        return create(renderer, source, textureData);
    }
}
