package com.graphicsengine.io;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.map.Playfield;
import com.graphicsengine.map.PlayfieldController;
import com.graphicsengine.tiledsprite.TiledSpriteController;
import com.nucleus.io.ResourcesData;

/**
 * Definition of all resources (for a scene)
 * 
 * @author Richard Sahlin
 *
 */
public class GraphicsEngineResourcesData extends ResourcesData {

    @SerializedName("tiledSpriteController")
    private ArrayList<TiledSpriteController> tiledSpriteControllers = new ArrayList<TiledSpriteController>();
    @SerializedName("playfieldController")
    private ArrayList<PlayfieldController> playfieldControllers = new ArrayList<PlayfieldController>();
    @SerializedName("playfield")
    private ArrayList<Playfield> playfields = new ArrayList<Playfield>();

    /**
     * Returns the defined playfields
     * 
     * @return
     */
    public Playfield[] getPlayfields() {
        return (Playfield[]) playfields.toArray();
    }

    /**
     * Returns the defined tiled charset objects as an array
     * 
     * @return
     */
    public PlayfieldController[] getPlayfieldController() {
        return (PlayfieldController[]) playfieldControllers.toArray();
    }

    /**
     * Returns the (first) tiledcharset with matching id, or null if not found.
     * 
     * @param id
     * @return
     */
    public PlayfieldController getPlayfieldController(String id) {
        for (PlayfieldController p : playfieldControllers) {
            if (id.equals(p.getId())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Returns the (first) playfield with matching id, or null if not found.
     * 
     * @param id
     * @return
     */
    public Playfield getPlayfield(String id) {
        for (Playfield p : playfields) {
            if (id.equals(p.getId())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Returns the (first) tiledspritecontroller with matching id, or null if not found.
     * 
     * @param id
     * @return
     */
    public TiledSpriteController getTiledSpriteController(String id) {
        for (TiledSpriteController t : tiledSpriteControllers) {
            if (id.equals(t.getId())) {
                return t;
            }
        }
        return null;
    }

    /**
     * Adds the sprite controller data if one does not already exist with the same id.
     * 
     * @param spriteControllerData
     */
    public void addSpriteController(TiledSpriteController spriteController) {
        if (getTiledSpriteController(spriteController.getId()) == null) {
            tiledSpriteControllers.add(spriteController);
        } else {
            System.out.println(RESOURCE_ALREADY_EXIST + spriteController.getId());
        }
    }

    /**
     * Adds the playfield if one does not already exist with the same id.
     * 
     * @param playfield
     */
    public void addPlayfield(Playfield playfield) {
        if (getPlayfield(playfield.getId()) == null) {
            playfields.add(playfield);
        } else {
            System.out.println(RESOURCE_ALREADY_EXIST + playfield.getId());
        }
    }

    /**
     * Adds the playfield controller data if one does not already exist with the same id.
     * 
     * @param playfieldControllerData
     */
    public void addPlayfieldController(PlayfieldController playfieldController) {
        if (getTiledSpriteController(playfieldController.getId()) == null) {
            playfieldControllers.add(playfieldController);
        } else {
            System.out.println(RESOURCE_ALREADY_EXIST + playfieldController.getId());
        }
    }

}
