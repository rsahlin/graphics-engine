package com.graphicsengine.io;

import java.io.IOException;
import java.io.Reader;

import com.google.gson.Gson;
import com.graphicsengine.exporter.GraphicsEngineNodeExporter;
import com.graphicsengine.map.PlayfieldControllerFactory;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.graphicsengine.tiledsprite.TiledSpriteControllerFactory;
import com.nucleus.io.GSONSceneFactory;
import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;
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
        nodeExporter.registerNodeExporter(GraphicsEngineNodeType.tiledCharset, nodeExporter);
        nodeExporter.registerNodeExporter(GraphicsEngineNodeType.tiledSpriteController, nodeExporter);
    }

    @Override
    protected Node createNode(RootNode scene, Node source, Node parent) throws IOException {
        try {
            GraphicsEngineNodeType type = GraphicsEngineNodeType.valueOf(source.getType());

            GraphicsEngineRootNode gScene = (GraphicsEngineRootNode) scene;
            String reference = source.getReference();
            Node created = null;

            switch (type) {
            case tiledCharset:
                // TODO create methods in GSON data classes that returns corresponding class needed for factory.
                // Or move data classes to factory packages and use directly
                created = PlayfieldControllerFactory.create(renderer, source, reference, gScene);
                break;
            case tiledSpriteController:
                created = TiledSpriteControllerFactory.create(renderer, source, reference, gScene);
                break;
            default:
                throw new IllegalArgumentException(NOT_IMPLEMENTED + type);
            }
            setViewFrustum(source, created);
            createChildNodes(scene, source, created);
            return created;

        } catch (IllegalArgumentException e) {
            return super.createNode(scene, source, parent);
        }
    }

}
