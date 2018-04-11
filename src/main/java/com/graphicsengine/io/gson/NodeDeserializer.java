package com.graphicsengine.io.gson;

import com.google.gson.JsonDeserializer;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.nucleus.io.gson.NucleusNodeDeserializer;
import com.nucleus.scene.Node;

/**
 * Container for adding known node types in the graphics engine, uses the NucleusNodeDeserializer to add
 * type/name.
 *
 */
public class NodeDeserializer extends NucleusNodeDeserializer implements JsonDeserializer<Node> {

    public NodeDeserializer() {
        super();
        addNodeTypes(GraphicsEngineNodeType.values());
    }

}
