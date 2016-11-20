package com.graphicsengine.io;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.map.Map;
import com.nucleus.io.ResourcesData;
import com.nucleus.scene.Node;

/**
 * Definition of all resources (for a scene)
 * 
 * @author Richard Sahlin
 *
 */
public class GraphicsEngineResourcesData extends ResourcesData {

    /**
     * Other data
     */
    @SerializedName("map")
    private ArrayList<Map> maps = new ArrayList<Map>();

    /**
     * Returns the defined maps
     * 
     * @return
     */
    public Map[] getMaps() {
        return (Map[]) maps.toArray();
    }

    /**
     * Adds the map if one does not already exist with the same id, used when exporting
     * 
     * @param playfield
     */
    public void addMap(Map playfield) {
        if (getMap(playfield.getId()) == null) {
            maps.add(playfield);
        } else {
            System.out.println(RESOURCE_ALREADY_EXIST + playfield.getId());
        }
    }

    /**
     * Returns the (first) map with matching id, or null if not found.
     * 
     * @param id
     * @return
     */
    public Map getMap(String id) {
        for (Map p : maps) {
            if (id.equals(p.getId())) {
                return p;
            }
        }
        return null;
    }

    /**
     * Internal method - returns the first node with matching id from the list.
     * 
     * @param nodes
     * @param id
     * @return First node with matching id, or null if not found.
     */
    private Node getNode(ArrayList<Node> nodes, String id) {
        for (Node n : nodes) {
            if (id.equals(n.getId())) {
                return n;
            }
        }
        return null;
    }

}
