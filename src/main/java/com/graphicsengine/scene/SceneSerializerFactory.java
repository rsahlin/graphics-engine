package com.graphicsengine.scene;

import com.nucleus.io.SceneSerializer;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.NodeFactory;

/**
 * Creates the sceneserializer implementations
 * 
 * @author Richard Sahlin
 *
 */
public class SceneSerializerFactory {

    /**
     * Returns an implementation of the SceneSerializer interface, callers must know the implementing class.
     * The returned serializer is ready to be used
     * 
     * @param className Implementing classname or null for default.
     * @param renderer The renderer to be used with the serializer
     * @param nodeFactory The nodefactory to be used with the serializer
     * @return The scene serializer implementation
     * @throws ClassNotFoundException If the specified class cannot be found
     * @throws InstantiationException If the specified class cannot be created
     * @throws IllegalAccessException If the specified class cannot be created
     */
    public static SceneSerializer getSerializer(String className, NucleusRenderer renderer, NodeFactory nodeFactory)
            throws ClassNotFoundException,
            InstantiationException, IllegalAccessException {
        SceneSerializer serializer = (SceneSerializer) Class.forName(className).newInstance();
        serializer.init(renderer, nodeFactory);
        return serializer;
    }

}
