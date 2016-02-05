package com.graphicsengine.io;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.map.Playfield;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.nucleus.Error;
import com.nucleus.scene.Node;
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
     * Adds the node the the resources in this class - the node must be a type that is known by the graphics engine.
     * 
     * @param node
     * @throws IllegalArgumentException If the node type is not one of {@link GraphicsEngineNodeType}
     */
    public void addResource(Node node) {
        try {
            GraphicsEngineNodeType type = GraphicsEngineNodeType.valueOf(node.getType());
            getResources().addNode(type, node);
        } catch (IllegalArgumentException e) {
            // This means the node is not a type that is known by the graphics engine
            throw new IllegalArgumentException(Error.INVALID_TYPE.message + node.getType());
        }
    }

    /**
     * Adds playfield to the resources.
     * 
     * @param playfield
     */
    public void addResource(Playfield playfield) {
        getResources().addPlayfield(playfield);
    }
}
