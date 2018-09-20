package com.graphicsengine.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.graphicsengine.component.SpriteAttributeComponent;
import com.graphicsengine.component.SpriteComponent;
import com.graphicsengine.exporter.GraphicsEngineNodeExporter;
import com.graphicsengine.io.gson.ComponentDeserializer;
import com.graphicsengine.io.gson.NodeDeserializer;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.nucleus.common.Type;
import com.nucleus.common.TypeResolver;
import com.nucleus.component.Component;
import com.nucleus.io.GSONSceneFactory;
import com.nucleus.io.SceneSerializer;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.scene.Node;

/**
 * Implementation of the scenefactory for the graphics engine, this shall take care of all nodes/datatypes that
 * are specific for the graphics engine.
 * Uses {@link NodeDeserializer} and {@link TypeResolver} to lookup classnames.
 * 
 * @author Richard Sahlin
 *
 */
public class GSONGraphicsEngineFactory extends GSONSceneFactory {

    /**
     * The types that can be used to represent classes when importing/exporting
     * This is used as a means to decouple serialized name from implementing class.
     * 
     */
    public enum GraphicsEngineClasses implements Type<Object> {

        spriteattributecomponent(SpriteAttributeComponent.class),
        spritecomponent(SpriteComponent.class);

        private final Class<?> theClass;

        private GraphicsEngineClasses(Class<?> theClass) {
            this.theClass = theClass;
        }

        @Override
        public Class<Object> getTypeClass() {
            return (Class<Object>) theClass;
        }

        @Override
        public String getName() {
            return name();
        }
    }

    private ComponentDeserializer componentDeserializer = new ComponentDeserializer();

    protected GSONGraphicsEngineFactory() {
        super();
    }

    public static SceneSerializer getInstance() {
        if (sceneFactory == null) {
            sceneFactory = new GSONGraphicsEngineFactory();
        }
        return sceneFactory;
    }

    @Override
    public void init(GLES20Wrapper gles, Type<?>[] types) {
        super.init(gles, types);
        registerTypes(GraphicsEngineClasses.values());
    }

    @Override
    protected void createNodeDeserializer() {
        nodeDeserializer = new NodeDeserializer();
    }

    @Override
    protected void createNodeExporter() {
        nodeExporter = new GraphicsEngineNodeExporter();
    }

    @Override
    protected void registerNodeExporters() {
        super.registerNodeExporters();
        nodeExporter.registerNodeExporter(GraphicsEngineNodeType.values(), new GraphicsEngineNodeExporter());
    }

    @Override
    protected void registerTypeAdapter(GsonBuilder builder) {
        super.registerTypeAdapter(builder);
        builder.registerTypeAdapter(Node.class, nodeDeserializer);
        builder.registerTypeAdapter(Component.class, componentDeserializer);
    }

    @Override
    protected void setGson(Gson gson) {
        super.setGson(gson);
        nodeDeserializer.setGson(gson);
        componentDeserializer.setGson(gson);
    }

}
