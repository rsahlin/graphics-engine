package com.graphicsengine.tiledsprite;

import java.io.IOException;

import com.graphicsengine.charset.TiledSheetSetup;
import com.nucleus.assets.AssetManager;
import com.nucleus.renderer.BufferObjectsFactory;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.texturing.Texture2D;

/**
 * Used to create tiled spritesheet.
 * The way to create a tiled spritesheet shall be through this class.
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSpriteFactory {

    /**
     * Creates a new TiledSpriteSheet using data from constructor.
     * 
     * @param renderer
     * @param constructor The data used when creating the TiledSpriteSheet, number of sprites etc.
     * @return
     * @throws IOException If there is an exception loading an asset
     */
    public static TiledSpriteSheet create(NucleusRenderer renderer, TiledSheetSetup constructor) throws IOException {
        TiledSpriteSheet sprites = new TiledSpriteSheet(constructor);
        TiledSpriteProgram program = new TiledSpriteProgram();
        renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer, constructor.getTexture());
        sprites.createMesh(program, texture, constructor.getTileWidth(), constructor.getTileHeight(),
                constructor.getTileZPos());
        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, sprites);
        }
        return sprites;

    }

}
