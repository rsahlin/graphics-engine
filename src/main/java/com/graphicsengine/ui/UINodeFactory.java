package com.graphicsengine.ui;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;

/**
 * Factory class for creating the UI nodes
 * 
 * @author Richard Sahlin
 *
 */
public class UINodeFactory {

    public static Node createButton(NucleusRenderer renderer, Node source, GraphicsEngineRootNode gScene) {

        Button button = new Button();

        return button;
    }
}
