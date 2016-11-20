package com.graphicsengine.io;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.map.Map;
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

    /**
     * Adds map to the resources.
     * 
     * @param map
     */
    public void addResource(Map map) {
        getResources().addMap(map);
    }

    @Override
    public RootNode createInstance() {
        return new GraphicsEngineRootNode();
    }

}
