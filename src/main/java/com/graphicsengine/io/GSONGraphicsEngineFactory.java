package com.graphicsengine.io;

import java.io.Reader;
import java.util.Arrays;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.graphicsengine.component.SpriteComponent;
import com.graphicsengine.exporter.GraphicsEngineNodeExporter;
import com.graphicsengine.geometry.GraphicsEngineMeshFactory;
import com.graphicsengine.io.gson.ComponentDeserializer;
import com.graphicsengine.io.gson.NodeDeserializer;
import com.graphicsengine.scene.GraphicsEngineNodeFactory;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.graphicsengine.system.SpriteSystem;
import com.nucleus.common.Type;
import com.nucleus.common.TypeResolver;
import com.nucleus.component.Component;
import com.nucleus.exporter.NucleusNodeExporter;
import com.nucleus.geometry.MeshFactory;
import com.nucleus.io.GSONSceneFactory;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;
import com.nucleus.scene.NodeFactory;
import com.nucleus.scene.RootNode;

/**
 * Implementation of the scenefactory for the graphics engine, this shall take care of all nodes/datatypes that
 * are specific for the graphics engine.
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

        spritecomponent(SpriteComponent.class),
        spritesystem(SpriteSystem.class);

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

    private NodeDeserializer nodeDeserializer = new NodeDeserializer();
    private ComponentDeserializer componentDeserializer = new ComponentDeserializer();

    /**
     * Creates a default scenefactory with {@link NucleusNodeExporter}.
     * Calls {@link #createNodeExporter()} and {@link #registerNodeExporters()}
     * This constructor will call {@link #init(NucleusRenderer, NodeFactory, MeshFactory)}
     * 
     * @param renderer
     * @param nodeFactory
     * @param meshFactory
     * @param types Implementation specific types that shall be registered to the {@linkplain TypeResolver}, may be null
     */
    public GSONGraphicsEngineFactory(NucleusRenderer renderer, NodeFactory nodeFactory,
            MeshFactory meshFactory, List<Type<?>> types) {
        super(renderer, nodeFactory, meshFactory, Arrays.asList((Type<?>[]) GraphicsEngineClasses.values()));
        if (types != null) {
            TypeResolver.getInstance().registerTypes(types);
        }
    }

    @Override
    protected RootNode getSceneFromJson(Gson gson, Reader reader) {
        return gson.fromJson(reader, GraphicsEngineRootNode.class);
    }

    @Override
    protected RootNode createSceneData() {
        return new GraphicsEngineRootNode();
    }

    @Override
    protected void createNodeExporter() {
        nodeExporter = new GraphicsEngineNodeExporter();
    }

    @Override
    protected RootNode createInstance() {
        return new GraphicsEngineRootNode();
    }

    @Override
    protected void registerNodeExporters() {
        super.registerNodeExporters();
        nodeExporter.registerNodeExporter(GraphicsEngineNodeType.values(), new GraphicsEngineNodeExporter());
    }

    /**
     * Utility method to get the default nodefactory
     * 
     * @return
     */
    public static NodeFactory getNodeFactory() {
        return new GraphicsEngineNodeFactory();
    }

    public static MeshFactory getMeshFactory() {
        return new GraphicsEngineMeshFactory();
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
