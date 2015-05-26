package com.graphicsengine.json;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.graphicsengine.scene.SceneSerializer;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;

/**
 * Instantiate a scene.
 * 
 * @author Richard Sahlin
 *
 */
public class JSONSceneFactory implements SceneSerializer {

    public final static String SCENE_KEY = "scene";
    private NucleusRenderer renderer;
    private JSONNodeParser nodeParser;

    /**
     * Empty constructor
     */
    public JSONSceneFactory() {
    }

    @Override
    public Node importScene(String filename, String sceneName) throws IOException {
        if (renderer == null) {
            throw new IllegalStateException(RENDERER_NOT_SET_ERROR);
        }
        ClassLoader loader = getClass().getClassLoader();
        Node node = new Node(sceneName);
        List<JSONObject> nodes = JSONUtils.readJSONArrayByKey(loader.getResourceAsStream(filename), sceneName);
        nodeParser.parse(node, nodes);
        return node;
    }

    @Override
    public void exportScene(OutputStream out, Object obj) throws IOException {

        if (obj instanceof Node) {
            Node node = (Node) obj;
            JSONObject json = new JSONObject();
            JSONObject jsonScene = new JSONObject();
            JSONArray jsonNodes = new JSONArray();
            for (Node child : node.getChildren()) {
                jsonNodes.add(nodeParser.exportObject(child));
            }
            jsonScene.put(SCENE_KEY, jsonNodes);
            System.out.println(jsonScene);
        }

    }

    @Override
    public void setRenderer(NucleusRenderer renderer) {
        if (renderer == null) {
            throw new IllegalArgumentException(NULL_RENDERER_ERROR);
        }
        this.renderer = renderer;
        nodeParser = new JSONNodeParser(renderer);
    }
}
