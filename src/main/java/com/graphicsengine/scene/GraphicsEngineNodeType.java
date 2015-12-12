package com.graphicsengine.scene;

import com.nucleus.common.Key;

public enum GraphicsEngineNodeType implements Key {

    tiledCharset(),
    tiledSpriteController();

    @Override
    public String getKey() {
        return name();
    }

}
