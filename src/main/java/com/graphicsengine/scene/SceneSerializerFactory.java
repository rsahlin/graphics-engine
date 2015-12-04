package com.graphicsengine.scene;

import com.nucleus.io.SceneSerializer;

/**
 * Creates the sceneserializer implementations
 * 
 * @author Richard Sahlin
 *
 */
public class SceneSerializerFactory {

    /**
     * Returns an implementation of the SceneSerializer interface, callers must know the implementing class.
     * 
     * @param className Implementing classname or null for default.
     * @return The scene serializer implementation
     * @throws ClassNotFoundException If the specified class cannot be found
     * @throws InstantiationException If the specified class cannot be created
     * @throws IllegalAccessException If the specified class cannot be created
     */
    public static SceneSerializer getSerializer(String className) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        return (SceneSerializer) Class.forName(className).newInstance();
    }

}
