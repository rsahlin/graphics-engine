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

    // TODO where is a good place to store this constant?
    public final static String NODETYPE_JSON_KEY = "type";

    @Override
    public Node deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        try {
            GraphicsEngineNodeType t = GraphicsEngineNodeType.valueOf(obj.get(NODETYPE_JSON_KEY).getAsString());
            return (Node) gson.fromJson(json, t.getTypeClass());
        } catch (IllegalArgumentException e) {
            //Try with super
            return super.deserialize(json, type, context);
        }
        
    }

}
