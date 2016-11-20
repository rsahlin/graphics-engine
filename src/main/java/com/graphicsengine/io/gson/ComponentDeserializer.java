package com.graphicsengine.io.gson;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.graphicsengine.component.GraphicsEngineComponentFactory;
import com.nucleus.component.Component;
import com.nucleus.io.gson.NucleusDeserializer;

public class ComponentDeserializer extends NucleusDeserializer implements JsonDeserializer<Component> {

    @Override
    public Component deserialize(JsonElement json, Type type, JsonDeserializationContext context)
            throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        GraphicsEngineComponentFactory.Type t = com.graphicsengine.component.GraphicsEngineComponentFactory.Type
                .valueOf(obj.get("type").getAsString());

        return (Component) gson.fromJson(json, t.getTypeClass());
    }

}
