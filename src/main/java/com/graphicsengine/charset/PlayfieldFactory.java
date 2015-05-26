package com.graphicsengine.charset;

import java.io.IOException;

import com.graphicsengine.assets.AssetManager;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.texturing.Texture2D;

/**
 * Use to create charmaps from JSON source
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldFactory {

    /**
     * Factory method for creating a playfield
     * 
     * @param baseRenderer
     * @param constructor
     * @return
     * @throws IOException
     */
    public static Playfield create(NucleusRenderer baseRenderer, PlayfieldSetup constructor) throws IOException {

        Playfield map = new Playfield(constructor.mapWidth * constructor.mapHeight);
        PlayfieldProgram program = new PlayfieldProgram();
        baseRenderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(baseRenderer, constructor.textureSetup);
        map.createMesh(program, texture, constructor.tileWidth, constructor.tileHeight, constructor.zpos,
                constructor.textureFramesX, constructor.textureFramesY);
        map.setupPlayfield(constructor.mapWidth, constructor.mapHeight, constructor.xpos, constructor.ypos);
        map.setPlayfieldData(constructor.data, 0, 0, constructor.data.length);
        return map;
    }
}
