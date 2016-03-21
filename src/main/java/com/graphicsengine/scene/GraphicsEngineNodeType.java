package com.graphicsengine.scene;

import com.nucleus.common.Key;

/**
 * The different type of nodes that are defined and handled by the Graphics Engine
 * 
 * @author Richard Sahlin
 *
 */
public enum GraphicsEngineNodeType implements Key {

    playfieldNode(),
    spriteMeshNode(),
    button(),
    quadNode();

    @Override
    public String getKey() {
        return name();
    }

}
