package com.graphicsengine.scene;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.component.SpriteComponent;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.component.CPUComponentBuffer;
import com.nucleus.component.CPUQuadExpander;
import com.nucleus.component.Component;
import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.geometry.Mesh.BufferIndex;
import com.nucleus.geometry.RectangleShapeBuilder;
import com.nucleus.geometry.RectangleShapeBuilder.RectangleConfiguration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureType;
import com.nucleus.vecmath.Rectangle;

/**
 * Node containing Quad elements, the intended usage is to group as many quad objects as possible under one node
 * and use the mesh in the parent node to render the objects.
 * This means that they need to share the mesh in this node.
 * Use this node for simple objects that does not need to have special behavior and not a large number of objects
 * (roughly < 100) otherwise the overhead will grow.
 * If a large number of objects are needed and/or special behavior then component node, see {@linkplain Component} or
 * {@link SpriteComponent}
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public class QuadParentNode extends Node implements Consumer {

    public static final String MAX_QUADS = "maxQuads";

    @SerializedName(MAX_QUADS)
    private int maxQuads;

    transient private ArrayList<SharedMeshQuad> quadChildren = new ArrayList<>();

    transient SpriteMesh spriteMesh;
    transient PropertyMapper mapper;
    transient CPUQuadExpander quadExpander;
    transient RectangleShapeBuilder shapeBuilder;

    /**
     * Used by GSON and {@link #createInstance(RootNode)} method - do NOT call directly
     */
    @Deprecated
    protected QuadParentNode() {
    }

    private QuadParentNode(RootNode root) {
        super(root, GraphicsEngineNodeType.quadNode);
    }

    @Override
    public Node createInstance(RootNode root) {
        QuadParentNode copy = new QuadParentNode(root);
        copy.set(this);
        return copy;
    }

    /**
     * Sets the data from the source to this, this will not copy transient values or children.
     * 
     * @param source
     */
    public void set(QuadParentNode source) {
        super.set(source);
        this.maxQuads = source.maxQuads;
    }

    /**
     * Returns the max number of quads that this node can handle
     * This is specified when loading the node.
     * 
     * @return
     */
    public int getMaxQuads() {
        return maxQuads;
    }

    protected int addQuad(SharedMeshQuad quadMeshNode) {
        int index = quadChildren.size();
        quadChildren.add(quadMeshNode);
        return index;
    }

    public void removeQuad(SharedMeshQuad quadMeshNode) {
        quadChildren.remove(quadMeshNode);
    }

    /**
     * Internal method
     * Creates the arrays for this quad node, ie the buffers needed to support expanding data from this node
     * into the sprite mesh.
     * 
     * @param mesh
     */
    private void createBuffers(SpriteMesh mesh) {
        mapper = mesh.getMapper();
        CPUComponentBuffer sourceData = new CPUComponentBuffer(maxQuads, mapper.attributesPerVertex);
        CPUComponentBuffer destinationData = new CPUComponentBuffer(maxQuads, mapper.attributesPerVertex * 4);
        quadExpander = new CPUQuadExpander(mesh, mapper, sourceData, destinationData);
    }

    /**
     * Builds a quad for the specified quad index, if rectangle is null then size of texture is used.
     * Initializes bounds
     * 
     * @param quad
     * @param rectangle Rectangle to build quad from, if null then texture is used.
     * @param The rectangle used to build the quad, same as rectangle if specified, otherwise texture rectangle based on
     * frame number
     * @param frame Initial frame
     */
    public Rectangle buildQuad(int quad, Rectangle rectangle, int frame) {
        Texture2D texture = spriteMesh.getTexture(Texture2D.TEXTURE_0);
        if (rectangle == null && (texture.getTextureType() == TextureType.Untextured ||
                texture.getWidth() == 0 || texture.getHeight() == 0)) {
            // Must have size
            throw new IllegalArgumentException("Node does not define RECT and texture is untextured or size is zero");
        }
        Rectangle quadRect = (rectangle != null && rectangle.getValues() != null && rectangle.getValues().length >= 4)
                ? rectangle
                : createRectangle(texture, 0);
        shapeBuilder.setStartQuad(quad).setRectangle(quadRect).build(spriteMesh);
        return quadRect;
    }

    protected Rectangle createRectangle(Texture2D texture, int frame) {
        Rectangle rect = texture.calculateRectangle(frame);
        // Check viewfrustum for scale factor - rectangle created using window aspect where y axis is normalized (1)
        Node view = viewFrustum != null ? this : getParentView();
        if (view != null) {
            float scale = view.getViewFrustum().getHeight();
            rect.scale(scale);
        }
        return rect;
    }

    @Override
    public void onCreated() {
        super.onCreated();
        spriteMesh = (SpriteMesh) getMesh(MeshIndex.MAIN);
        spriteMesh.setAttributeUpdater(this);
        createBuffers(spriteMesh);
        bindAttributeBuffer(spriteMesh.getAttributeBuffer(BufferIndex.ATTRIBUTES.index));
        shapeBuilder = new RectangleShapeBuilder(new RectangleConfiguration(1, 0));
        switch (spriteMesh.getTexture(Texture2D.TEXTURE_0).textureType) {
            case TiledTexture2D:
            case UVTexture2D:
            case Untextured:
                shapeBuilder.setEnableVertexIndex(true);
                break;
            case DynamicTexture2D:
            case Texture2D:
                break;
        }
    }

    @Override
    public void updateAttributeData(NucleusRenderer renderer) {
        quadExpander.updateAttributeData(renderer);
    }

    @Override
    public void bindAttributeBuffer(AttributeBuffer buffer) {
        quadExpander.bindAttributeBuffer(buffer);
    }

    public CPUQuadExpander getExpander() {
        return quadExpander;
    }

}
