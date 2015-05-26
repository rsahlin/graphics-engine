package com.graphicsengine.json;

import java.io.IOException;
import java.util.List;

import org.json.simple.JSONObject;

import com.graphicsengine.charset.TiledSetup;
import com.graphicsengine.sprite.SpriteController;
import com.graphicsengine.sprite.SpriteControllerFactory;
import com.graphicsengine.sprite.SpriteControllerFactory.SpriteControllers;
import com.graphicsengine.tiledsprite.TiledSpriteSetup;
import com.nucleus.renderer.NucleusRenderer;

/**
 * Utilities for TiledSpriteController to/from JSON
 * 
 * @author Richard Sahlin
 *
 */
public class JSONTiledSpriteParser extends JSONParser {

    private final static String TILEDSPRITECONTROLLER_KEY = "tiledspritecontroller";

    public JSONTiledSpriteParser(NucleusRenderer renderer) {
        super(renderer);
    }

    @Override
    public Object parseKey(Object jsonKey, JSONObject json, List<JSONObject> nodes) throws IOException {
        JSONObject spriteSheet = lookupNodeForKey(jsonKey, TILEDSPRITECONTROLLER_KEY, json, nodes);
        if (spriteSheet == null) {
            return null;
        }
        TiledSpriteSetup spriteSetup = new TiledSpriteSetup();
        getSetup(spriteSheet, nodes, spriteSetup);
        TiledSetup tiledSetup = new TiledSetup();
        getSetup(spriteSheet, nodes, tiledSetup);
        spriteSetup.setTiledSetup(tiledSetup);
        try {

            SpriteController tiledController = SpriteControllerFactory.create(SpriteControllers.TILED);
            spriteSetup.setId((String) json.get(TILEDSPRITECONTROLLER_KEY));
            tiledController.createSprites(renderer, spriteSetup);

            return tiledController;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Object exportObject(Object obj) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }
}
