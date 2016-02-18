package com.graphicsengine.ui;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.scene.Node;

/**
 * A button ui component
 * 
 * @author Richard Sahlin
 *
 */
public class Button extends Node {

    /**
     * The mesh that can be rendered
     */
    @SerializedName("mesh")
    private SpriteMesh mesh;

    public Button(Button source) {
        super(source);

    }

}
