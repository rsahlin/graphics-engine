package com.graphicsengine.exporter;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.map.Map;
import com.graphicsengine.map.MapFactory;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.scene.GraphicsEngineNodeType;
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
        case quadNode:
            // exportDataReferences((QuadParentNode) source, (GraphicsEngineRootNode) rootNode);
            return source.copy();
        default:
            throw new IllegalArgumentException(NOT_IMPLEMENTED + type);
        }
    }

    /**
     * Collect the data needed for the playfield node and store in resources
     * This shall only export the references - not the node itself
     * 
     * @param playfield
     * @param sceneData
     */
    private void exportDataReferences(PlayfieldNode playfieldNode, GraphicsEngineRootNode sceneData) {
        exportMeshes(playfieldNode.getMeshes(), sceneData);
        PlayfieldNode resource = playfieldNode.copy();
        // sceneData.addResource(resource);
        Map map = MapFactory.createMap(playfieldNode);
        sceneData.addResource(map);
    }

    @Override
    public void exportObject(Object object, RootNode sceneData) {
        // TODO Auto-generated method stub

    }

}
