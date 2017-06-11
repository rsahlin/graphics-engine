package com.graphicsengine.exporter;

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
            return source.copy(rootNode);
        case quadNode:
            // exportDataReferences((QuadParentNode) source, (GraphicsEngineRootNode) rootNode);
            return source.copy(rootNode);
        default:
            throw new IllegalArgumentException(NOT_IMPLEMENTED + type);
        }
    }

    @Override
    public void exportObject(Object object, RootNode sceneData) {
        // TODO Auto-generated method stub

    }

}
