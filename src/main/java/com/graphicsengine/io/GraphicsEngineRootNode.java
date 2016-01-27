package com.graphicsengine.io;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.map.Playfield;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.spritemesh.SpriteMeshNode;
import com.nucleus.scene.RootNode;

/**
 * The graphics engine implementation of SceneData, this holds all the objects that can be serialized in this project
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public class GraphicsEngineRootNode extends RootNode {

    @SerializedName("resources")
    private GraphicsEngineResourcesData resources = new GraphicsEngineResourcesData();

    @Override
    public GraphicsEngineResourcesData getResources() {
        return resources;
    }

    public void addResource(SpriteMeshNode spriteController) {
        getResources().addSpriteMeshNode(spriteController);
    }

    public void addResource(PlayfieldNode playfieldController) {
        getResources().addPlayfieldNode(playfieldController);
    }

    public void addResource(Playfield playfield) {
        getResources().addPlayfield(playfield);
    }
}
