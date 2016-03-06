package com.graphicsengine.ui;

import com.google.gson.annotations.SerializedName;
import com.nucleus.scene.Node;

/**
 * A button ui component
 * 
 * @author Richard Sahlin
 *
 */
public class Button extends Node {

    /**
     * Reference to the mesh that can be rendered
     */
    @SerializedName("mesh")
    private String mesh;

    public Button(Button source) {
        super(source);
        this.mesh = source.mesh;
    }

    /**
     * Returns the name of the mesh for this node.
     * 
     * @return
     */
    public String getMesh() {
        return mesh;
    }

}
