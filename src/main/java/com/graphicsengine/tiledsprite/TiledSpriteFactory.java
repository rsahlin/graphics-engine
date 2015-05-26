package com.graphicsengine.tiledsprite;

import java.io.IOException;

import com.graphicsengine.assets.AssetManager;
import com.graphicsengine.charset.TiledSetup;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.texturing.Texture2D;

/**
 * Used to create tiled spritesheet.
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
    public static TiledSpriteSheet create(NucleusRenderer renderer, TiledSetup constructor) throws IOException {
        TiledSpriteSheet sprites = new TiledSpriteSheet(constructor);
        TiledSpriteProgram program = new TiledSpriteProgram();
        renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer, constructor.getTexture());
        sprites.createMesh(program, texture, constructor.getTileWidth(), constructor.getTileHeight(),
                constructor.getTileZPos(), constructor.getFramesX(), constructor.getFramesY());
        return sprites;

    }

}
