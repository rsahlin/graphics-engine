package com.graphicsengine.app;

import com.graphicsengine.scene.SceneFactory;

/**
 * Base functionallity for an application using the graphics engine, such as rendering and import/export.
 * The goal is to use interfaces whenever possible and inject dependencies to decouple from implementation.
 * 
 * @author Richard Sahlin
 *
 */
public abstract class AppHandler {

    /**
     * The factory used when loading and saving scene data.
     */
    SceneFactory sceneFactory;

    /**
     * Sets the scenefactory to use when loading or saving graphics engine scenes.
     * The scene factory must be set before scenes can be loaded or saved.
     * 
     * @param sceneFactory
     */
    public void setSceneFactory(SceneFactory sceneFactory) {
        this.sceneFactory = sceneFactory;
    }

}
