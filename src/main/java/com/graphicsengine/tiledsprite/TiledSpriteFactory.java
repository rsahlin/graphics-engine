package com.graphicsengine.tiledsprite;

import java.io.IOException;

import com.graphicsengine.scene.GraphicsEngineSceneData;
import com.nucleus.assets.AssetManager;
import com.nucleus.renderer.BufferObjectsFactory;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2DData;

/**
 * Used to create tiled spritesheet.
 * The way to create a tiled spritesheet shall be through this class.
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSpriteFactory {

    public static TiledSpriteSheet create(NucleusRenderer renderer, TiledSpriteControllerData tiledSpriteController,
            GraphicsEngineSceneData scene) throws IOException {
        TiledTexture2DData textureData = (TiledTexture2DData) scene.getResources().getTexture2DData(
                tiledSpriteController.getTileData().getTextureref());

        TiledSpriteSheet sprites = new TiledSpriteSheet(tiledSpriteController.getLogicdata().getCount());
        TiledSpriteProgram program = new TiledSpriteProgram();
        renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer, textureData);

        float[] dimension = tiledSpriteController.getTileData().getDimension().getDimension();
        float[] anchor = tiledSpriteController.getTileData().getTransform().getTranslate();
        sprites.createMesh(program, texture, dimension, anchor);
        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, sprites);
        }

        return sprites;

    }
}
