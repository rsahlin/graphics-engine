package com.graphicsengine.tiledsprite;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineSceneData;
import com.nucleus.assets.AssetManager;
import com.nucleus.renderer.BufferObjectsFactory;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;

/**
 * Used to create tiled spritesheet.
 * The way to create a tiled spritesheet shall be through this class.
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSpriteFactory {

    public static TiledSpriteMesh create(NucleusRenderer renderer, TiledSpriteController parent,
            GraphicsEngineSceneData scene) throws IOException {

        TiledTexture2D textureData = (TiledTexture2D) scene.getResources().getTexture2DData(
                parent.getSpriteSheet().getTextureRef());

        TiledSpriteMesh source = parent.getSpriteSheet();
        TiledSpriteMesh sprites = new TiledSpriteMesh(source);
        TiledSpriteProgram program = new TiledSpriteProgram();
        renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer, textureData);

        float[] dimension = source.getSize();
        float[] translate = source.getTransform().getTranslate();
        sprites.createMesh(program, texture, dimension, translate);
        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, sprites);
        }

        return sprites;

    }
}
