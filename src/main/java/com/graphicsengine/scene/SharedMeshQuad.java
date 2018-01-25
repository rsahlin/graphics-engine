package com.graphicsengine.scene;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.SimpleLogger;
import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TextureType;
import com.nucleus.vecmath.Rectangle;

/**
 * A Quad child that has to be appended to QuadNode in order to be rendered.
 * This node will share the mesh from the parent {@link QuadParentNode}
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
    transient private SpriteMesh parentMesh;
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
     * @param mesh The source mesh
     * @param index
     */
    public void onCreated(SpriteMesh mesh, int index) {
        this.childIndex = index;
        this.parentMesh = mesh;
        Texture2D texture = mesh.getTexture(Texture2D.TEXTURE_0);
        if (rectangle == null && (texture.getTextureType() == TextureType.Untextured ||
                texture.getWidth() == 0 || texture.getHeight() == 0)) {
            // Must have size
            throw new IllegalArgumentException("Node does not define RECT and texture is untextured or size is zero");
        }
        Rectangle quadRect = rectangle != null ? rectangle
                : texture.calculateWindowRectangle();
        mesh.buildQuad(index, mesh.getMaterial().getProgram(), quadRect);
        initBounds(quadRect);
        if (transform == null) {
            SimpleLogger.d(getClass(), "---------------------------------MUST FIX----------------------------------");
            // mesh.setScale(index, 1, 1);
        } else {
            SimpleLogger.d(getClass(), "---------------------------------MUST FIX----------------------------------");
            // mesh.setTransform(index, transform);
        }
        SimpleLogger.d(getClass(), "---------------------------------MUST FIX----------------------------------");
        // mesh.setFrame(index, frame);
        // if (mesh.getTexture(Texture2D.TEXTURE_0).textureType == TextureType.Untextured) {
        // mesh.setColor(index, getMaterial() != null ? getMaterial().getAmbient() : mesh.getMaterial().getAmbient());
        // }
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
     * Sets the frame number for this child.
     * 
     * @param frame
     */
    public void setFrame(int frame) {
        SimpleLogger.d(getClass(), "---------------------------------MUST FIX----------------------------------");
        // parentMesh.setFrame(childIndex, frame);
    }

}
