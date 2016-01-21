package com.graphicsengine.exporter;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.map.Playfield;
import com.graphicsengine.map.PlayfieldController;
import com.graphicsengine.map.PlayfieldControllerFactory;
import com.graphicsengine.map.PlayfieldFactory;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.graphicsengine.spritemesh.SpriteMeshController;
import com.graphicsengine.spritemesh.TiledSpriteControllerFactory;
import com.nucleus.exporter.NucleusNodeExporter;
import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;

/**
 * Node exporter for graphics engine nodes
 * 
 * @author Richard Sahlin
 *
 */
public class GraphicsEngineNodeExporter extends NucleusNodeExporter {

    @Override
    public Node exportNode(Node source, RootNode rootNode) {
        GraphicsEngineNodeType type = GraphicsEngineNodeType.valueOf(source.getType());
        switch (type) {
        case tiledCharset:
            exportDataReferences((PlayfieldController) source, (GraphicsEngineRootNode) rootNode);
            return new Node(source);
        case tiledSpriteController:
            exportDataReferences((SpriteMeshController) source, (GraphicsEngineRootNode) rootNode);
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
    private void exportDataReferences(PlayfieldController playfieldController, GraphicsEngineRootNode sceneData) {
        exportMeshes(playfieldController.getMeshes(), sceneData);
        PlayfieldController resource = PlayfieldControllerFactory.copy(playfieldController);
        resource.setId(playfieldController.getReference());
        resource.setReference(null);
        sceneData.addResource(resource);
        Playfield playfield = PlayfieldFactory.createPlayfield(playfieldController);
        sceneData.addResource(playfield);
    }

    /**
     * Collects the data needed for the spritecontroller and store in resources.
     * This shall only export the references - not the node itself
     * 
     * @param tiledSpriteController
     * @param sceneData
     */
    private void exportDataReferences(SpriteMeshController tiledSpriteController,
            GraphicsEngineRootNode sceneData) {
        exportMeshes(tiledSpriteController.getMeshes(), sceneData);
        SpriteMeshController resource = TiledSpriteControllerFactory.copy(tiledSpriteController);
        resource.setId(tiledSpriteController.getReference());
        resource.setReference(null);
        sceneData.addResource(resource);
    }

    @Override
    public void exportObject(Object object, RootNode sceneData) {
        // TODO Auto-generated method stub

    }

}