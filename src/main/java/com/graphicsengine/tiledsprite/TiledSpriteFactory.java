package com.graphicsengine.tiledsprite;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
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

    /**
     * This will create an old school sprite mesh, where each sprite has a frame, the sprite can be rotated in x axis
     * and positioned in x and y.
     * 
     * @param renderer
     * @param source The sprite controller source, an instance of this will be created.
     * @param scene
     * @return
     * @throws IOException
     */
    public static TiledMesh create(NucleusRenderer renderer, TiledSpriteController source,
            GraphicsEngineRootNode scene) throws IOException {

        TiledTexture2D textureData = (TiledTexture2D) scene.getResources().getTexture2DData(
                source.getSpriteSheet().getTextureRef());
        TiledMesh sourceMesh = source.getSpriteSheet();
        TiledMesh sprites = new TiledMesh(sourceMesh);
        TiledSpriteProgram program = new TiledSpriteProgram();
        renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer, textureData);

        sprites.createMesh(program, texture);
        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, sprites);
        }
        return sprites;
    }
}
