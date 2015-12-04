package com.graphicsengine.io;

import java.io.IOException;
import java.io.Reader;

import com.google.gson.Gson;
import com.graphicsengine.charset.PlayfieldFactory;
import com.graphicsengine.charset.TiledCharsetData;
import com.graphicsengine.scene.GraphicsEngineSceneData;
import com.graphicsengine.scene.NodeType;
import com.graphicsengine.sprite.SpriteController;
import com.graphicsengine.sprite.SpriteControllerFactory;
import com.graphicsengine.sprite.SpriteControllerFactory.SpriteControllers;
import com.graphicsengine.tiledsprite.TiledSpriteControllerData;
import com.nucleus.io.GSONSceneFactory;
import com.nucleus.scene.Node;
import com.nucleus.scene.SceneData;

public class GSONGraphicsEngineFactory extends GSONSceneFactory {

    @Override
    protected SceneData getSceneFromJson(Gson gson, Reader reader) {
        return gson.fromJson(reader, GraphicsEngineSceneData.class);
    }

    @Override
    protected SceneData createSceneData() {
        return new GraphicsEngineSceneData();
    }

    /**
     * Creates a Node for the specified nodedata using the resources in the scene.
     * If type is specified then the data for this type is appended to the Node.
     * 
     * @param scene
     * @param nodedata
     * @param node
     * @return The created node
     */
    @Override
    protected Node createNode(SceneData scene, Node node, Node parent) throws IOException {
        GraphicsEngineSceneData gScene = (GraphicsEngineSceneData) scene;
        NodeType type = NodeType.valueOf(node.getType());
        String reference = node.getReference();
        setViewFrustum(node, parent);

        switch (type) {
        case tiledCharset:
            // TODO create methods in GSON data classes that returns corresponding class needed for factory.
            // Or move data classes to factory packages and use directly
            TiledCharsetData charmap = gScene.getResources().getTiledCharset(reference);
            parent.addMesh(PlayfieldFactory.create(renderer, charmap, gScene));
            return parent;
        case tiledSpriteController:
            SpriteController tiledController = null;
            try {
                tiledController = SpriteControllerFactory.create(SpriteControllers.TILED);
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                // Cannot recover
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            TiledSpriteControllerData spriteController = gScene.getResources().getTiledSpriteController(reference);
            tiledController.createSprites(renderer, spriteController, scene);
            parent.addChild(tiledController);
            return tiledController;
        default:
            throw new IllegalArgumentException(NOT_IMPLEMENTED + type);
        }
    }

}
