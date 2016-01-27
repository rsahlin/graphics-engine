package com.graphicsengine.io;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.map.Playfield;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.spritemesh.SpriteMeshNode;
import com.nucleus.io.ResourcesData;

/**
 * Definition of all resources (for a scene)
 * 
 * @author Richard Sahlin
 *
 */
public class GraphicsEngineResourcesData extends ResourcesData {

    @SerializedName("spriteMeshNode")
    private ArrayList<SpriteMeshNode> spriteMeshNodes = new ArrayList<SpriteMeshNode>();
    @SerializedName("playfieldNode")
    private ArrayList<PlayfieldNode> playfieldNodes = new ArrayList<PlayfieldNode>();
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
    public PlayfieldNode[] getPlayfieldNode() {
        return (PlayfieldNode[]) playfieldNodes.toArray();
    }

    /**
     * Returns the (first) playfield node with matching id, or null if not found.
     * 
     * @param id
     * @return
     */
    public PlayfieldNode getPlayfieldNode(String id) {
        for (PlayfieldNode p : playfieldNodes) {
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
    public SpriteMeshNode getSpriteMeshNode(String id) {
        for (SpriteMeshNode t : spriteMeshNodes) {
            if (id.equals(t.getId())) {
                return t;
            }
        }
        return null;
    }

    /**
     * Adds the sprite mesh node if one does not already exist with the same id.
     * 
     * @param spriteControllerData
     */
    public void addSpriteMeshNode(SpriteMeshNode spriteController) {
        if (getSpriteMeshNode(spriteController.getId()) == null) {
            spriteMeshNodes.add(spriteController);
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
     * Adds the playfield node if one does not already exist with the same id.
     * 
     * @param playfieldNode
     */
    public void addPlayfieldNode(PlayfieldNode playfieldNode) {
        if (getSpriteMeshNode(playfieldNode.getId()) == null) {
            playfieldNodes.add(playfieldNode);
        } else {
            System.out.println(RESOURCE_ALREADY_EXIST + playfieldNode.getId());
        }
    }

}
