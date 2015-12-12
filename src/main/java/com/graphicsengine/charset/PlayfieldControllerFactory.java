package com.graphicsengine.charset;

import com.nucleus.scene.Node;

/**
 * Creates playfield controllers that can be put in a scene as nodes and rendered.
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldControllerFactory {

    /**
     * Returns a new instance of the playfield controller
     * 
     * @source The source node information
     * @return
     */
    public static PlayfieldController create(Node source) {
        return new PlayfieldController(source);
    }

}
