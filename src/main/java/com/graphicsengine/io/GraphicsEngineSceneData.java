package com.graphicsengine.io;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.map.Playfield;
import com.graphicsengine.map.PlayfieldController;
import com.graphicsengine.tiledsprite.TiledSpriteController;
import com.nucleus.scene.SceneData;

/**
 * The graphics engine implementation of SceneData, this holds all the objects that can be serialized in this project
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public class GraphicsEngineSceneData extends SceneData {

    @SerializedName("resources")
    private GraphicsEngineResourcesData resources = new GraphicsEngineResourcesData();

    @Override
    public GraphicsEngineResourcesData getResources() {
        return resources;
    }

    public void addResource(TiledSpriteController spriteController) {
        getResources().addSpriteController(spriteController);
    }

    public void addResource(PlayfieldController playfieldController) {
        getResources().addPlayfieldController(playfieldController);
    }

    public void addResource(Playfield playfield) {
        getResources().addPlayfield(playfield);
    }
}
