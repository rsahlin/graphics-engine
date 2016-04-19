package com.graphicsengine.io.gson;

import java.lang.reflect.Type;

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
import com.nucleus.ErrorMessage;
import com.nucleus.io.NucleusNodeDeserializer;
import com.nucleus.scene.Node;

public class NodeDeserializer extends NucleusNodeDeserializer implements JsonDeserializer<Node> {

    @Override
    public Node deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        try {
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
                throw new IllegalArgumentException(ErrorMessage.NOT_IMPLEMENTED.message);
            }
        } catch (IllegalArgumentException e) {
            //Try with super
            return super.deserialize(json, type, context);
        }
        
    }

}
