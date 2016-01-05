package com.graphicsengine.map;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineSceneData;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;

/**
 * Creates playfield controllers that can be put in a scene as nodes and rendered.
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldControllerFactory {

    /**
     * Returns a new instance of the playfield controller, the returned playfieldcontroller will be a new instance
     * with data collected from the source references.
     * Use this method when importing.
     * 
     * @param renderer
     * @param source The source node for the returned instance.
     * @source reference Reference to the playfield controller that shall be created.
     * @param scene The scene holding the resources
     * @return New instance of the referenced playfield controlller
     */
    public static PlayfieldController create(NucleusRenderer renderer, Node source, String reference,
            GraphicsEngineSceneData scene) throws IOException {
        PlayfieldController refNode = scene.getResources().getPlayfieldController(reference);
        PlayfieldController playfieldController = new PlayfieldController(refNode);
        playfieldController.toReference(source, playfieldController);
        playfieldController.createMesh(renderer, playfieldController, scene);
        // playfieldController.copyTransform(source);
        playfieldController.createPlayfield(scene);
        return playfieldController;
    }
}
