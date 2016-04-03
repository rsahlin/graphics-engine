package com.graphicsengine.io;

import java.io.IOException;
import java.io.Reader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.graphicsengine.exporter.GraphicsEngineNodeExporter;
import com.graphicsengine.geometry.GraphicsEngineMeshFactory;
import com.graphicsengine.io.gson.NodeDeserializer;
import com.graphicsengine.scene.GraphicsEngineNodeFactory;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.nucleus.geometry.MeshFactory;
import com.nucleus.io.GSONSceneFactory;
import com.nucleus.io.ResourcesData;
import com.nucleus.scene.Node;
import com.nucleus.scene.NodeFactory;
import com.nucleus.scene.RootNode;

/**
 * Implementation of the scenefactory for the graphics engine, this shall take care of all nodes/datatypes that
 * are specific for the graphics engine.
 * 
 * @author Richard Sahlin
 *
 */
public class GSONGraphicsEngineFactory extends GSONSceneFactory {

    private NodeDeserializer nodeDeserializer = new NodeDeserializer();

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
    }

    @Override
    protected RootNode createRoot() {
        return new GraphicsEngineRootNode();
    }

    @Override
    protected void registerNodeExporters() {
        super.registerNodeExporters();
        nodeExporter.registerNodeExporter(GraphicsEngineNodeType.values(), new GraphicsEngineNodeExporter());
    }

    @Override
    protected Node createNode(ResourcesData resources, Node source, Node parent) throws IOException {
        Node created = nodeFactory.create(renderer, meshFactory, resources, source);
        created.setRootNode(parent.getRootNode());
        setViewFrustum(source, created);
        createChildNodes(resources, source, created);
        created.onCreated();
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

    public static MeshFactory getMeshFactory() {
        return new GraphicsEngineMeshFactory();
    }

    @Override
    protected void registerTypeAdapter(GsonBuilder builder) {
        super.registerTypeAdapter(builder);
        builder.registerTypeAdapter(Node.class, nodeDeserializer);
    }

    @Override
    protected void setGson(Gson gson) {
        super.setGson(gson);
        nodeDeserializer.setGson(gson);
    }
    
}
