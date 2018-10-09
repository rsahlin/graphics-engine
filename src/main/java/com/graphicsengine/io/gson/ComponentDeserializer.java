package com.graphicsengine.io.gson;

import java.lang.reflect.Type;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.nucleus.common.TypeResolver;
import com.nucleus.component.Component;
import com.nucleus.io.gson.NucleusDeserializer;
import com.nucleus.scene.Node;

/**
 * Deserializes the specified {@linkplain Component} from JSON.
 * Returns the correct subclass of {@linkplain Component}
 * 
 */
public class ComponentDeserializer extends NucleusDeserializer<Node> implements JsonDeserializer<Component> {

    @Override
    public Component deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String typeName = obj.get(Component.TYPE).getAsString();
        Component c = (Component) gson.fromJson(json, TypeResolver.getInstance().getTypeClass(typeName));
        return c;
    }

    @Override
    public void registerTypeAdapter(GsonBuilder builder) {
        // No need to register any adapter
    }

}
