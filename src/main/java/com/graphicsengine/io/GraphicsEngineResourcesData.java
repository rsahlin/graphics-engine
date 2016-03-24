package com.graphicsengine.io;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.map.Playfield;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.graphicsengine.scene.QuadParentNode;
import com.graphicsengine.scene.SharedMeshQuad;
import com.graphicsengine.spritemesh.SpriteMeshNode;
import com.nucleus.Error;
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
     * Container for all nodes
     */
    @SerializedName("spriteMeshNode")
    private ArrayList<SpriteMeshNode> spriteMeshNodes = new ArrayList<SpriteMeshNode>();
    @SerializedName("playfieldNode")
    private ArrayList<PlayfieldNode> playfieldNodes = new ArrayList<PlayfieldNode>();
    @SerializedName("sharedMeshNode")
    private ArrayList<SharedMeshQuad> sharedMeshNode = new ArrayList<SharedMeshQuad>();
    @SerializedName("quadNode")
    private ArrayList<QuadParentNode> quadNodes = new ArrayList<>();

    /**
     * Other data
     */
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
     * Returns the first quadNode with matching id, or null if not found.
     * 
     * @param id
     * @return
     */
    public QuadParentNode getQuadNode(String id) {
        for (QuadParentNode quad : quadNodes) {
            if (id.equals(quad.getId())) {
                return quad;
            }
        }
        return null;
    }

    /**
     * Adds the node if one does not already exist with the same id.
     * TODO: Should an exception be thrown if a node with same id alredy exists? probably....
     * 
     * @param type The node type
     * @param node The node
     */
    public void addNode(GraphicsEngineNodeType type, Node node) {
        switch (type) {
        case playfieldNode:
            addNode((ArrayList) playfieldNodes, node);
            break;
        case spriteMeshNode:
            addNode((ArrayList) spriteMeshNodes, node);
            break;
        case sharedMeshNode:
            addNode((ArrayList) sharedMeshNode, node);
            break;
        default:
            throw new IllegalArgumentException(Error.NOT_IMPLEMENTED.message);
        }
    }

    private void addNode(ArrayList<Node> nodes, Node node) {
        if (getNode(GraphicsEngineNodeType.valueOf(node.getType()), node.getId()) == null) {
            nodes.add(node);
        } else {
            System.out.println(RESOURCE_ALREADY_EXIST + node.getId() + ", type: " + node.getType());
        }

    }

    /**
     * Returns the (first) Node of the matching type and id, or null if not found.
     * 
     * @param type The type of node to return
     * @param id The id of the node to return
     * @return The first node with matching id and type.
     */
    public Node getNode(GraphicsEngineNodeType type, String id) {
        switch (type) {
        case spriteMeshNode:
            return getNode((ArrayList) spriteMeshNodes, id);
        case playfieldNode:
            return getNode((ArrayList) playfieldNodes, id);
        case sharedMeshNode:
            return getNode((ArrayList) sharedMeshNode, id);
        case quadNode:
            return getNode((ArrayList) quadNodes, id);
        default:
            throw new IllegalArgumentException(Error.NOT_IMPLEMENTED.message + " : " + type);
        }
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
