package com.graphicsengine.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.graphicsengine.charset.Playfield;
import com.nucleus.renderer.BaseRenderer;
import com.nucleus.scene.Node;

/**
 * Factory methods to create a node from JSON data.
 * 
 * @author Richard Sahlin
 *
 */
public class JSONNodeParser extends JSONParser {

    public final static String INSTANCE_NODE_KEY = "instance-node";
    public final static String ID_KEY = "id";

    List<JSONParser> parsers = new ArrayList<JSONParser>();

    public JSONNodeParser(BaseRenderer renderer) {
        super(renderer);
        parsers.add(new JSONPlayfieldParser(renderer));
        parsers.add(new JSONTextureParser(renderer));
        parsers.add(new JSONTiledSpriteParser(renderer));
    }

    @Override
    public Object parseKey(Object jsonKey, JSONObject json, List<JSONObject> nodes) throws IOException {
        JSONObject JSONnode = getValueAsJSON(jsonKey, INSTANCE_NODE_KEY, json);
        if (JSONnode == null) {
            return callParsers(jsonKey, json, nodes);
        }
        Node node = new Node((String) JSONnode.get(ID_KEY));
        parse(node, JSONnode, nodes);
        return node;
    }

    /**
     * Parses the specified JSONobject and adds to node.
     * 
     * @param node The node where the parsed object will be added
     * @param json The JSON object to parse
     * @param nodes List of nodes, used to lookup references.
     * @throws IOException
     */
    protected void parse(Node node, JSONObject json, List<JSONObject> nodes) throws IOException {
        for (Object o : json.keySet()) {
            Object obj = parseKey(o, json, nodes);
            if (obj != null) {
                addToNode(node, obj);
            }
        }
    }

    /**
     * Creates nodes from the json data, adding created object to node.
     * 
     * @param node Created nodes will be added here
     * @param nodes List of JSON nodes
     */
    public void parse(Node node, List<JSONObject> nodes) throws IOException {
        for (JSONObject json : nodes) {
            for (Object o : json.keySet()) {
                Object result = parseKey(o, json, nodes);
                if (result != null) {
                    addToNode(node, result);
                }
            }
        }
    }

    /**
     * Internal method, adds an object to the node
     * 
     * @param node
     * @param obj
     */
    private void addToNode(Node node, Object obj) {
        if (obj instanceof Playfield) {
            node.addMesh((Playfield) obj);
        } else if (obj instanceof Node) {
            System.out.println("Adding node: " + ((Node) obj).getId() + " to " + node.getId());
            node.addChild((Node) obj);
        }
    }

    /**
     * Internal method to call all parses for one key to parse, if the key was parsed an object is returned - otherwise
     * null is returned.
     * 
     * @param jsonKey
     * @param json
     * @param nodes
     * @return The parse object, or null if not understood
     * @throws IOException
     */
    protected Object callParsers(Object jsonKey, JSONObject json, List<JSONObject> nodes) throws IOException {
        for (JSONParser p : parsers) {
            Object o = p.parseKey(jsonKey, json, nodes);
            if (o != null) {
                return o;
            }
        }
        return null;
    }

    @Override
    public Object exportObject(Object obj) throws IOException {

        if (obj instanceof Node) {
            Node node = (Node) obj;
            JSONObject json = new JSONObject();
            JSONObject jsonNode = new JSONObject();
            jsonNode.put(ID_KEY, node.getId());
            json.put(INSTANCE_NODE_KEY, jsonNode);
            return json;
        }
        return null;
    }
}
