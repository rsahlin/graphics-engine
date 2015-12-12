package com.graphicsengine.io;

import java.io.IOException;
import java.io.Reader;

import com.google.gson.Gson;
import com.graphicsengine.charset.PlayfieldController;
import com.graphicsengine.charset.PlayfieldControllerFactory;
import com.graphicsengine.charset.TiledCharsetData;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.graphicsengine.scene.GraphicsEngineSceneData;
import com.graphicsengine.sprite.SpriteController;
import com.graphicsengine.sprite.SpriteControllerFactory;
import com.graphicsengine.sprite.SpriteControllerFactory.SpriteControllers;
import com.graphicsengine.tiledsprite.TiledSpriteController;
import com.graphicsengine.tiledsprite.TiledSpriteControllerData;
import com.nucleus.io.GSONSceneFactory;
import com.nucleus.io.NodeExporter;
import com.nucleus.scene.Node;
import com.nucleus.scene.SceneData;

public class GSONGraphicsEngineFactory extends GSONSceneFactory implements NodeExporter {

    public GSONGraphicsEngineFactory() {
        registerNodeExporter(GraphicsEngineNodeType.tiledCharset, this);
    }

    @Override
    protected SceneData getSceneFromJson(Gson gson, Reader reader) {
        return gson.fromJson(reader, GraphicsEngineSceneData.class);
    }

    @Override
    protected SceneData createSceneData() {
        return new GraphicsEngineSceneData();
    }

    @Override
    protected Node createNode(SceneData scene, Node source, Node parent) throws IOException {
        GraphicsEngineSceneData gScene = (GraphicsEngineSceneData) scene;
        GraphicsEngineNodeType type = GraphicsEngineNodeType.valueOf(source.getType());
        String reference = source.getReference();
        Node created = null;

        switch (type) {
        case tiledCharset:
            // TODO create methods in GSON data classes that returns corresponding class needed for factory.
            // Or move data classes to factory packages and use directly
            TiledCharsetData charmap = gScene.getResources().getTiledCharset(reference);
            PlayfieldController playfieldController = PlayfieldControllerFactory.create(source);
            playfieldController.createMesh(renderer, charmap, gScene);
            created = playfieldController;
            break;
        case tiledSpriteController:
            TiledSpriteControllerData spriteControllerData = gScene.getResources().getTiledSpriteController(reference);
            SpriteController tiledController = null;
            try {
                tiledController = SpriteControllerFactory.create(SpriteControllers.TILED, source);
            } catch (IllegalAccessException | InstantiationException | ClassNotFoundException e) {
                // Cannot recover
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            tiledController.createMesh(renderer, spriteControllerData, gScene);
            tiledController.createSprites(renderer, spriteControllerData, scene);
            created = tiledController;
            break;
        default:
            throw new IllegalArgumentException(NOT_IMPLEMENTED + type);
        }
        if (created != null) {
            setViewFrustum(source, created);
        }
        return created;
    }

    @Override
    public Node exportNode(Node source, SceneData sceneData) {
        GraphicsEngineNodeType type = GraphicsEngineNodeType.valueOf(source.getType());
        switch (type) {
        case tiledCharset:
            exportPlayfieldController((PlayfieldController) source, sceneData);
            return new Node(source);
        case tiledSpriteController:
            exportTiledSpriteController((TiledSpriteController) source, sceneData);
            return new Node(source);
        default:
            throw new IllegalArgumentException(NOT_IMPLEMENTED + type);
        }
    }

    private void exportPlayfieldController(PlayfieldController playfield, SceneData sceneData) {
    }

    private void exportTiledSpriteController(TiledSpriteController tiledSpriteController, SceneData sceneData) {
    }

    @Override
    public void exportObject(Object object, SceneData sceneData) {
        // TODO Auto-generated method stub

    }

}
