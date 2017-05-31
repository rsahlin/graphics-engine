package com.graphicsengine.scene;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.scene.Node;
import com.nucleus.texturing.Texture2D;
import com.nucleus.vecmath.Rectangle;

/**
 * A Quad child that has to be appended to QuadNode in order to be rendered.
 * This node will share the mesh from the parent QuadNode.
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
    /**
     * The rectangle defining the sprites, all sprites will have same size
     * 4 values = x1,y1 + width and height
     */
    @SerializedName(Rectangle.RECT)
    private Rectangle rectangle;

    public SharedMeshQuad() {
    }

    public void onCreated(SpriteMesh mesh, int index) {
        this.childIndex = index;
        Rectangle quadRect = rectangle != null ? rectangle
                : mesh.getTexture(Texture2D.TEXTURE_0).calculateWindowRectangle();
        mesh.buildQuad(index, mesh.getMaterial().getProgram(), quadRect);
        initBounds(quadRect);
        if (transform == null) {
            mesh.setScale(index, 1, 1);
        } else {
            mesh.setTransform(index, transform);
        }
        mesh.setFrame(index, frame);
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
    public SharedMeshQuad createInstance() {
        SharedMeshQuad copy = new SharedMeshQuad();
        return copy;
    }

    @Override
    public Node copy() {
        SharedMeshQuad copy = createInstance();
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

}
