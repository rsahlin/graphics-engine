package com.graphicsengine.io;

import com.nucleus.scene.RootNode;

/**
 * The graphics engine implementation of SceneData, this holds all the objects that can be serialized in this project
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public class GraphicsEngineRootNode extends RootNode {

    @Override
    public RootNode createInstance() {
        return new GraphicsEngineRootNode();
    }

}
