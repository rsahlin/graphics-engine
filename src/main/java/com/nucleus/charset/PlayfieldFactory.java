package com.nucleus.charset;

import java.io.IOException;

import com.graphicsengine.assets.AssetManager;
import com.nucleus.renderer.BaseRenderer;
import com.nucleus.texturing.Texture2D;

/**
 * Use to create charmaps from JSON source
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldFactory {

    public static Playfield createCharmap(BaseRenderer baseRenderer, PlayfieldSetup constructor) throws IOException {

        Playfield map = new Playfield(constructor.mapWidth * constructor.mapHeight);
        PlayfieldProgram program = new PlayfieldProgram();
        baseRenderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(baseRenderer, constructor.textureSource);
        map.createMesh(program, texture, constructor.tileWidth, constructor.tileHeight, constructor.zpos,
                constructor.textureFramesX, constructor.textureFramesY);
        map.setupPlayfield(constructor.mapWidth, constructor.mapHeight, constructor.xpos, constructor.ypos);
        map.setPlayfieldData(constructor.data, 0, 0, constructor.data.length);
        return map;
    }
}
