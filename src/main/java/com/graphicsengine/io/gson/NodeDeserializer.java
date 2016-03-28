package com.graphicsengine.io.gson;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.graphicsengine.map.PlayfieldNode;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.graphicsengine.scene.QuadParentNode;
import com.graphicsengine.scene.SharedMeshQuad;
import com.graphicsengine.spritemesh.SpriteMeshNode;
import com.graphicsengine.ui.Element;
import com.nucleus.Error;
import com.nucleus.scene.Node;

public class NodeDeserializer implements JsonDeserializer<Node> {

    private Gson gson;

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Override
    public Node deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        
        JsonObject obj = json.getAsJsonObject();
        GraphicsEngineNodeType t = GraphicsEngineNodeType.valueOf(obj.get("type").getAsString());
        switch (t) {
        case playfieldNode:
            return gson.fromJson(json, PlayfieldNode.class);
        case element:
            return gson.fromJson(json, Element.class);
        case quadNode:
            return gson.fromJson(json, QuadParentNode.class);
        case sharedMeshNode:
            return gson.fromJson(json, SharedMeshQuad.class);
        case spriteMeshNode:
            return gson.fromJson(json, SpriteMeshNode.class);
        default:
            throw new IllegalArgumentException(Error.NOT_IMPLEMENTED.message);
        }
        
        
        /*
         * JsonObject obj = json.getAsJsonObject();
         * com.nucleus.bounds.Bounds.Type t = com.nucleus.bounds.Bounds.Type
         * .valueOf(obj.get(Bounds.SerializeNames.type.name()).getAsString());
         * JsonArray array = obj.get(Bounds.SerializeNames.bounds.name()).getAsJsonArray();
         * float[] values = new float[array.size()];
         * for (int i = 0; i < array.size(); i++) {
         * values[i] = array.get(i).getAsFloat();
         * }
         * return BoundsFactory.create(t, values);
         */
    }

}
