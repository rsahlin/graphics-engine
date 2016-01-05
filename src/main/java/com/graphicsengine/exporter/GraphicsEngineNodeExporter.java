package com.graphicsengine.exporter;

import com.graphicsengine.io.GraphicsEngineSceneData;
import com.graphicsengine.map.Playfield;
import com.graphicsengine.map.PlayfieldController;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.graphicsengine.tiledsprite.TiledSpriteController;
import com.nucleus.exporter.NucleusNodeExporter;
import com.nucleus.scene.Node;
import com.nucleus.scene.SceneData;

/**
 * Node exporter for graphics engine nodes
 * 
 * @author Richard Sahlin
 *
 */
public class GraphicsEngineNodeExporter extends NucleusNodeExporter {

    @Override
    public Node exportNode(Node source, SceneData sceneData) {
        GraphicsEngineNodeType type = GraphicsEngineNodeType.valueOf(source.getType());
        switch (type) {
        case tiledCharset:
            exportDataReferences((PlayfieldController) source, (GraphicsEngineSceneData) sceneData);
            return new Node(source);
        case tiledSpriteController:
            exportDataReferences((TiledSpriteController) source, (GraphicsEngineSceneData) sceneData);
            return new Node(source);
        default:
            throw new IllegalArgumentException(NOT_IMPLEMENTED + type);
        }
    }

    /**
     * Collect the data needed for the playfieldcontroller and store in resources
     * This shall only export the references - not the node itself
     * 
     * @param playfield
     * @param sceneData
     */
    private void exportDataReferences(PlayfieldController playfieldController, GraphicsEngineSceneData sceneData) {
        exportMeshes(playfieldController.getMeshes(), sceneData);
        PlayfieldController resource = new PlayfieldController(playfieldController);
        resource.setId(playfieldController.getReference());
        resource.setReference(null);
        sceneData.addResource(resource);
        Playfield playfield = new Playfield(playfieldController);
        sceneData.addResource(playfield);
    }

    /**
     * Collects the data needed for the spritecontroller and store in resources.
     * This shall only export the references - not the node itself
     * 
     * @param tiledSpriteController
     * @param sceneData
     */
    private void exportDataReferences(TiledSpriteController tiledSpriteController,
            GraphicsEngineSceneData sceneData) {
        exportMeshes(tiledSpriteController.getMeshes(), sceneData);
        TiledSpriteController resource = new TiledSpriteController(tiledSpriteController);
        resource.setId(tiledSpriteController.getReference());
        resource.setReference(null);
        sceneData.addResource(resource);
    }

    @Override
    public void exportObject(Object object, SceneData sceneData) {
        // TODO Auto-generated method stub

    }

}
