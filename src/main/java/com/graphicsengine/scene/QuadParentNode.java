package com.graphicsengine.scene;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.component.SpriteComponent;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.component.Component;
import com.nucleus.geometry.AttributeBuffer;
import com.nucleus.geometry.AttributeUpdater.Consumer;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.geometry.Mesh.BufferIndex;
import com.nucleus.geometry.QuadExpander;
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
    /**
     * The sprites common float data storage, this is the sprite visible (mesh) properties such as position, scale and
     * frame, plus entity data needed to process the logic.
     * This is what is generally needed in order to put sprite on screen.
     * In order to render a mesh with sprites this data is copied one -> four in the mesh.
     */
    transient public float[] spriteData;
    transient protected int spritedataSize;
    transient SpriteMesh spriteMesh;
    transient PropertyMapper mapper;
    transient QuadExpander quadExpander;

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

    public int addQuad(SharedMeshQuad quadMeshNode) {
        int index = quadChildren.size();
        quadChildren.add(quadMeshNode);
        return index;
    }

    public void removeQuad(SharedMeshQuad quadMeshNode) {
        quadChildren.remove(quadMeshNode);
    }

    /**
     * Internal method
     * Creates the arrays for this quad node
     * 
     * @param mesh
     */
    private void createBuffers(SpriteMesh mesh) {
        mapper = mesh.getMapper();
        spritedataSize = mapper.attributesPerVertex;
        this.spriteData = new float[spritedataSize * maxQuads];
        quadExpander = new QuadExpander(mesh.getTexture(Texture2D.TEXTURE_0), mapper, spriteData, spritedataSize,
                maxQuads, 4);
    }

    /**
     * Builds a quad for the specified quad index, if rectangle is null then size of texture is used.
     * Initializes bounds
     * 
     * @param quad
     * @param rectangle Rectangle to build quad from, if null then texture is used.
     * @param The rectangle used to build the quad, same as rectangle if specified, otherwise texture rectangle.
     */
    public Rectangle buildQuad(int quad, Rectangle rectangle) {
        Texture2D texture = spriteMesh.getTexture(Texture2D.TEXTURE_0);
        if (rectangle == null && (texture.getTextureType() == TextureType.Untextured ||
                texture.getWidth() == 0 || texture.getHeight() == 0)) {
            // Must have size
            throw new IllegalArgumentException("Node does not define RECT and texture is untextured or size is zero");
        }
        Rectangle quadRect = rectangle != null ? rectangle
                : texture.calculateWindowRectangle();
        spriteMesh.buildQuad(quad, spriteMesh.getMaterial().getProgram(), quadRect);
        return quadRect;
    }

    @Override
    public void onCreated() {
        super.onCreated();
        spriteMesh = (SpriteMesh) getMesh(MeshType.MAIN);
        spriteMesh.setAttributeUpdater(this);
        bindAttributeBuffer(spriteMesh.getVerticeBuffer(BufferIndex.ATTRIBUTES.index));

        // Setup all children in this node last
        for (Node n : getChildren()) {
            if (n instanceof SharedMeshQuad) {
                // This is a special case since the mesh belongs to this node.
                int index = addQuad((SharedMeshQuad) n);
                ((SharedMeshQuad) n).onCreated(this, index);
            }
        }
    }

    @Override
    public void updateAttributeData() {
        quadExpander.updateAttributeData();
    }

    @Override
    public void bindAttributeBuffer(AttributeBuffer buffer) {
        createBuffers(spriteMesh);
        quadExpander.bindAttributeBuffer(buffer);
    }

    public QuadExpander getExpander() {
        return quadExpander;
    }

}
