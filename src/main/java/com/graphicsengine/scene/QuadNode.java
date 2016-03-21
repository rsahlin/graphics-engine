package com.graphicsengine.scene;

import com.google.gson.annotations.SerializedName;
import com.nucleus.scene.Node;

/**
 * Node containing Quad elements, the intended usage is to group as many quad objects as possible under one node
 * and use the mesh in the parent node to render the objects.
 * This means that they need to share the mesh in this node.
 * Use this node for simple objects that does not need to have special behavior. Use actor/sprite for that.
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public class QuadNode extends Node {

    @SerializedName("maxQuads")
    private int maxQuads;

    public QuadNode() {
        super();
    }

    @Override
    public QuadNode createInstance() {
        return new QuadNode();
    }

    @Override
    public Node copy() {
        QuadNode copy = createInstance();
        copy.set(this);
        return copy;
    }

    /**
     * Sets the data from the source to this, this will not copy transient values or children.
     * 
     * @param source
     */
    public void set(QuadNode source) {
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

    @Override
    public void onCreated() {
        // Setup all childrens vertices
        for (Node n : getChildren()) {
        }
    }

}
