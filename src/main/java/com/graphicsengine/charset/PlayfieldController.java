package com.graphicsengine.charset;

import java.io.IOException;

import com.graphicsengine.scene.GraphicsEngineSceneData;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;
import com.nucleus.scene.SceneData;

/**
 * The playfield controller that contains a playfield (mesh) and can be put in a scene.
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldController extends Node {

    /**
     * The mesh that can be redered
     * TODO Unify controllers that renders a Mesh, with methods for creating the mesh
     */
    private Playfield playfield;

    /**
     * Creates a new playfieldcontroller from the specified source node
     * The id and references from the node will be copied.
     * 
     * @source
     */
    public PlayfieldController(Node source) {
        super(source);
    }

    /**
     * Creates the renderable playfield (mesh)
     * After this call this node can be rendered
     * 
     * @param renderer
     * @param charmap
     * @param scene
     * @throws IOException
     */
    public void createMesh(NucleusRenderer renderer, TiledCharsetData charmap, SceneData scene)
            throws IOException {
        GraphicsEngineSceneData gScene = (GraphicsEngineSceneData) scene;
        playfield = PlayfieldFactory.create(renderer, charmap, gScene);
        addMesh(playfield);
    }
}
