package com.graphicsengine.charset;

import java.io.IOException;

import com.graphicsengine.dataflow.ArrayInputData;
import com.graphicsengine.scene.GraphicsEngineSceneData;
import com.nucleus.assets.AssetManager;
import com.nucleus.renderer.BufferObjectsFactory;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.vecmath.Axis;

/**
 * Use to create charmaps
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldFactory {

    /**
     * Factory method for creating a playfield
     * 
     * @param renderer
     * @param tiledCharsetData The charset data
     * @param playfieldData The playfield (map) data,
     * @param textureData The texture data to be used for the charset
     * @return The created playfield
     * @throws IOException
     */
    public static Playfield create(NucleusRenderer renderer, TiledCharsetData tiledCharsetData,
            PlayfieldData playfieldData, TiledTexture2D textureData) throws IOException {

        ArrayInputData id = playfieldData.getArrayInput();

        Playfield map = new Playfield(tiledCharsetData.getId(), tiledCharsetData.getTileData().getCount());
        PlayfieldProgram program = new PlayfieldProgram();
        renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer, textureData);
        map.createMesh(program, texture, tiledCharsetData.getTileData().getDimension().getDimension(), tiledCharsetData
                .getTileData().getTransform().getTranslate());
        map.setupPlayfield(tiledCharsetData.getMapDimension().getDimension(), tiledCharsetData.getTransform()
                .getTranslate());
        tiledCharsetData.createMapData();
        id.copyArray(tiledCharsetData.getMapData(),
                tiledCharsetData.getMapDimension().getDimension()[Axis.WIDTH.index],
                tiledCharsetData.getMapDimension().getDimension()[Axis.HEIGHT.index], 0, 0);
        map.setPlayfieldData(tiledCharsetData.getMapData(), 0, 0, tiledCharsetData.getMapData().length);

        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, map);
        }

        return map;
    }

    /**
     * Factory method for creating the playfield mesh, after this call the playfield can be rendered
     * 
     * @param renderer
     * @param constructor
     * @return The created playfield
     * @throws IOException
     */
    public static Playfield create(NucleusRenderer renderer, TiledCharsetData tiledCharsetData,
            GraphicsEngineSceneData scene)
            throws IOException {

        TiledTexture2D textureData = (TiledTexture2D) scene.getResources().getTexture2DData(
                tiledCharsetData.getTileData().getTextureref());
        PlayfieldData playfieldData = scene.getResources().getPlayfieldData(tiledCharsetData.getSource());

        return create(renderer, tiledCharsetData, playfieldData, textureData);
    }

}
