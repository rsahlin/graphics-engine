package com.graphicsengine.exporter;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.map.Playfield;
import com.graphicsengine.map.PlayfieldMeshFactory;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.graphicsengine.spritemesh.SpriteMeshNode;
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
        case playfieldNode:
            exportDataReferences((PlayfieldNode) source, (GraphicsEngineRootNode) rootNode);
            return source.copy();
        case spriteMeshNode:
            exportDataReferences((SpriteMeshNode) source, (GraphicsEngineRootNode) rootNode);
            return source.copy();
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
    private void exportDataReferences(PlayfieldNode playfieldController, GraphicsEngineRootNode sceneData) {
        exportMeshes(playfieldController.getMeshes(), sceneData);
        PlayfieldNode resource = playfieldController.copy();
        sceneData.addResource(resource);
        Playfield playfield = PlayfieldMeshFactory.createPlayfield(playfieldController);
        sceneData.addResource(playfield);
    }

    /**
     * Collects the data needed for the spritecontroller and store in resources.
     * This shall only export the references - not the node itself
     * 
     * @param tiledSpriteController
     * @param sceneData
     */
    private void exportDataReferences(SpriteMeshNode tiledSpriteController,
            GraphicsEngineRootNode sceneData) {
        exportMeshes(tiledSpriteController.getMeshes(), sceneData);
        SpriteMeshNode resource = tiledSpriteController.copy();
        sceneData.addResource(resource);
    }

    @Override
    public void exportObject(Object object, RootNode sceneData) {
        // TODO Auto-generated method stub

    }

}
