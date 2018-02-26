package com.graphicsengine.scene;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.component.SpriteComponent;
import com.nucleus.geometry.Mesh;
import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureType;
import com.nucleus.vecmath.Rectangle;
import com.nucleus.vecmath.Transform;

/**
 * A Quad child that has to be appended to QuadNode in order to be rendered.
 * This node will share the mesh from the parent {@link QuadParentNode}
 * This is for objects that are mostly static, for instance UI elements, and objects that need touch events.
 * If a large number of objects with shared behavior are needed use {@link SpriteComponent} instead.
 * 
 * @author Richard Sahlin
 *
 */
public class SharedMeshQuad extends Node {

    /**
     * The framenumber for this quad, from the texture in the referenced mesh.
     */
    private int frame;
    /**
     * The index of this shared mesh quad node with it's parent.
     */
    transient private int childIndex;
    transient private QuadParentNode parent;
    /**
     * The rectangle defining the sprites, all sprites will have same size
     * 4 values = x1,y1 + width and height
     */
    @SerializedName(Rectangle.RECT)
    private Rectangle rectangle;

    /**
     * Used by GSON and {@link #createInstance(RootNode)} method - do NOT call directly
     */
    @Deprecated
    protected SharedMeshQuad() {
    }

    protected SharedMeshQuad(RootNode root) {
        super(root, GraphicsEngineNodeType.sharedMeshNode);
    }

    /**
     * Called when the parent node is created - remember that shared mesh quad does not
     * use it's own mesh
     * TODO Need to provide size and color from scene definition.
     * 
     * @param Parent The parent node holding all quads
     * @param index
     */
    public void onCreated(QuadParentNode parent, int index) {
        this.childIndex = index;
        this.parent = parent;
        initBounds(parent.buildQuad(index, rectangle));
        if (transform == null) {
            transform = new Transform();
        }
        parent.getExpander().setData(index, transform);
        parent.getExpander().setFrame(index, frame);
        Mesh mesh = parent.getMesh(MeshType.MAIN);
        if (mesh.getTexture(Texture2D.TEXTURE_0).textureType == TextureType.Untextured) {
            parent.getExpander().setColor(index,
                    getMaterial() != null ? getMaterial().getAmbient() : mesh.getMaterial().getAmbient());
        }
    }

    /**
     * Sets the index of this child with the parent, this is normally done by the parent.
     * 
     * @param index
     */
    public void setChildIndex(int index) {
        this.childIndex = index;
    }

    @Override
    public Node createInstance(RootNode root) {
        SharedMeshQuad copy = new SharedMeshQuad(root);
        copy.set(this);
        return copy;

    }

    /**
     * Sets the values from the source into this node
     * 
     * @param source
     */
    public void set(SharedMeshQuad source) {
        super.set(source);
        this.frame = source.frame;
        if (source.rectangle != null) {
            setQuadRectangle(source.rectangle);
        } else {
            rectangle = null;
        }
    }

    /**
     * Internal method, sets the rectangle defining each sprite
     * This will only set the size parameter, createMesh must be called to actually create the mesh
     * 
     * param rectangle values defining sprite, X1, Y1, width, height.
     */
    private void setQuadRectangle(Rectangle rectangle) {
        this.rectangle = new Rectangle(rectangle);
    }

    /**
     * Sets the frame number
     * 
     * @param frame
     */
    public void setFrame(int frame) {
        parent.getExpander().setFrame(childIndex, frame);
    }

    /**
     * Copies the transform so that the Quad is updated on screen.
     * Call this after the transform in the Mesh has been changed.
     */
    public void updateTransform() {
        parent.getExpander().setData(childIndex, transform);
    }

}
