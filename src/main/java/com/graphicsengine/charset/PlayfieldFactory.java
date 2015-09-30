package com.graphicsengine.charset;

import java.io.IOException;

import com.nucleus.assets.AssetManager;
import com.nucleus.renderer.BufferObjectsFactory;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.texturing.Texture2D;

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
     * @param constructor
     * @return
     * @throws IOException
     */
    public static Playfield create(NucleusRenderer renderer, PlayfieldSetup constructor) throws IOException {

        Playfield map = new Playfield(constructor.getId(), constructor.mapWidth * constructor.mapHeight);
        PlayfieldProgram program = new PlayfieldProgram();
        renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer, constructor.getTexture());
        map.createMesh(program, texture, constructor);
        map.setupPlayfield(constructor.mapWidth, constructor.mapHeight, constructor.xpos, constructor.ypos);
        map.setPlayfieldData(constructor.data, 0, 0, constructor.data.length);

        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, map);
        }

        return map;
    }
}
