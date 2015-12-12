package com.graphicsengine.scene;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.tiledsprite.TiledSpriteSheet;
import com.nucleus.geometry.Mesh;
import com.nucleus.scene.SceneData;
import com.nucleus.texturing.Texture2D;

/**
 * The graphics engine implementation of SceneData, this holds all the objects that can be serialized in this project
 * 
 * @author Richard Sahlin
 *
 */
public class GraphicsEngineSceneData extends SceneData {

    @SerializedName("resources")
    private GraphicsEngineResourcesData resources;

    public GraphicsEngineResourcesData getResources() {
        return resources;
    }

    @Override
    public void addResource(Texture2D texture) {
        System.out.println("texture: " + texture.getClass().getSimpleName());
    }

    @Override
    public void addResource(Mesh mesh) {
        if (mesh instanceof TiledSpriteSheet) {
            System.out.println("tiledspritesheet");
        }
    }
}
