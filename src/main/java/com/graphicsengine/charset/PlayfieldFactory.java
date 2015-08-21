package com.graphicsengine.charset;

import java.io.IOException;

import com.nucleus.assets.AssetManager;
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

        Playfield map = new Playfield(constructor.getId(), constructor.mapWidth * constructor.mapHeight);
        PlayfieldProgram program = new PlayfieldProgram();
        baseRenderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(baseRenderer, constructor.getTexture());
        map.createMesh(program, texture, constructor);
        map.setupPlayfield(constructor.mapWidth, constructor.mapHeight, constructor.xpos, constructor.ypos);
        map.setPlayfieldData(constructor.data, 0, 0, constructor.data.length);
        return map;
    }
}
