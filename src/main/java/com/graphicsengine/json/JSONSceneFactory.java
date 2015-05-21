package com.graphicsengine.json;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.graphicsengine.scene.SceneFactory;
import com.nucleus.renderer.BaseRenderer;
import com.nucleus.scene.Node;

/**
 * Instantiate a scene.
 * 
 * @author Richard Sahlin
 *
 */
public class JSONSceneFactory implements SceneFactory {

    public final static String SCENE_KEY = "scene";
    private BaseRenderer renderer;
    private JSONNodeParser nodeParser;

    public JSONSceneFactory(BaseRenderer renderer) {
        this.renderer = renderer;
        nodeParser = new JSONNodeParser(renderer);
    }

    @Override
    public Node importScene(String filename, String sceneName) throws IOException {
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
}
