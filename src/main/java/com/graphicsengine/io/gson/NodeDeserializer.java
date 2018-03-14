package com.graphicsengine.io.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.nucleus.io.gson.NucleusNodeDeserializer;
import com.nucleus.scene.Node;

public class NodeDeserializer extends NucleusNodeDeserializer implements JsonDeserializer<Node> {

    @Override
    public Node deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        GraphicsEngineNodeType t;
        try {
            JsonElement element = obj.get(NODETYPE_JSON_KEY);
            if (element == null) {
                throw new IllegalArgumentException("Node does not contain:" + NODETYPE_JSON_KEY);
            }
            t = GraphicsEngineNodeType.valueOf(element.getAsString());
        } catch (IllegalArgumentException e) {
            // Try with super
            return super.deserialize(json, type, context);
        }
        return (Node) gson.fromJson(json, t.getTypeClass());
    }

}
