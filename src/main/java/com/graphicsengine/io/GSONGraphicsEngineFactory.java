package com.graphicsengine.io;

import java.io.IOException;
import java.io.Reader;

import com.google.gson.Gson;
import com.graphicsengine.exporter.GraphicsEngineNodeExporter;
import com.graphicsengine.scene.GraphicsEngineNodeFactory;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.nucleus.io.GSONSceneFactory;
import com.nucleus.scene.Node;
import com.nucleus.scene.NodeFactory;
import com.nucleus.scene.RootNode;

public class GSONGraphicsEngineFactory extends GSONSceneFactory {

    @Override
    protected RootNode getSceneFromJson(Gson gson, Reader reader) {
        return gson.fromJson(reader, GraphicsEngineRootNode.class);
    }

    @Override
    protected RootNode createSceneData() {
        return new GraphicsEngineRootNode();
    }

    @Override
    protected void createNodeExporter() {
        nodeExporter = new GraphicsEngineNodeExporter();
        nodeExporter.registerNodeExporter(GraphicsEngineNodeType.values(), nodeExporter);
    }

    @Override
    protected Node createNode(RootNode scene, Node source, Node parent) throws IOException {
        String reference = source.getReference();
        Node created = nodeFactory.create(renderer, source, reference, scene);
        setViewFrustum(source, created);
        createChildNodes(scene, source, created);
        return created;
    }

    /**
     * Utility method to get the default nodefactory
     * 
     * @return
     */
    public static NodeFactory getNodeFactory() {
        return new GraphicsEngineNodeFactory();
    }

}
