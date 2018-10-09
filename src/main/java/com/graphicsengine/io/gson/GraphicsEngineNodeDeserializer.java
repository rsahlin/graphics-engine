package com.graphicsengine.io.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.nucleus.component.Component;
import com.nucleus.io.gson.NucleusNodeDeserializer;
import com.nucleus.scene.Node;

/**
 * Container for adding known node types in the graphics engine, uses the NucleusNodeDeserializer to add
 * type/name.
 *
 */
public class GraphicsEngineNodeDeserializer extends NucleusNodeDeserializer implements JsonDeserializer<Node> {

    private ComponentDeserializer componentDeserializer = new ComponentDeserializer();

    public GraphicsEngineNodeDeserializer() {
        super();
        addNodeTypes(GraphicsEngineNodeType.values());
    }

    @Override
    public void registerTypeAdapter(GsonBuilder builder) {
        super.registerTypeAdapter(builder);
        builder.registerTypeAdapter(Node.class, this);
        // builder.registerTypeAdapter(Node.class, nodeDeserializer);
        builder.registerTypeAdapter(Component.class, componentDeserializer);
    }

    @Override
    public void setGson(Gson gson) {
        super.setGson(gson);
        // nodeDeserializer.setGson(gson);
        componentDeserializer.setGson(gson);
    }

}
