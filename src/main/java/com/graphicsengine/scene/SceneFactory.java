package com.graphicsengine.scene;

import java.io.IOException;
import java.io.OutputStream;

import com.nucleus.scene.Node;

/**
 * Create a scene node without a direct connection to the underlying implementation of how to load and parse
 * scene data.
 * 
 * @author Richard Sahlin
 *
 */
public interface SceneFactory {

    /**
     * Creates a named node from a scene, the scene will be loaded using filename and the node returned shall be named
     * name
     * 
     * @param filename Name of file containing scene data.
     * @param name Name of scene to create
     * @return The scene node with matching name, including all defined children.
     * @throws IOException If there is an exception loading the data.
     */
    public Node importScene(String filename, String name) throws IOException;

    /**
     * Exports a scene in the same format as this factory can import.
     * 
     * @param out
     * @param obj
     * @throws IOException
     */
    public void exportScene(OutputStream out, Object obj) throws IOException;

}
