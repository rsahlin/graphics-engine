package com.graphicsengine.io;

import com.graphicsengine.component.SpriteAttributeComponent;
import com.graphicsengine.component.SpriteComponent;
import com.graphicsengine.exporter.GraphicsEngineNodeExporter;
import com.graphicsengine.io.gson.GraphicsEngineNodeDeserializer;
import com.graphicsengine.scene.GraphicsEngineNodeType;
import com.nucleus.common.Type;
import com.nucleus.common.TypeResolver;
import com.nucleus.io.GSONSceneFactory;
import com.nucleus.io.SceneSerializer;
import com.nucleus.io.gson.NucleusRootDeserializer;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;

/**
 * Implementation of the scenefactory for the graphics engine, this shall take care of all nodes/datatypes that
 * are specific for the graphics engine.
 * Uses {@link GraphicsEngineNodeDeserializer} and {@link TypeResolver} to lookup classnames.
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

    protected GSONGraphicsEngineFactory() {
        super();
    }

    public static SceneSerializer<RootNode> getInstance() {
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
    protected NucleusRootDeserializer<Node> createNucleusNodeDeserializer() {
        return new GraphicsEngineNodeDeserializer();
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

}
