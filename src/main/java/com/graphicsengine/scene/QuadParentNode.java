package com.graphicsengine.scene;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.graphicsengine.spritemesh.SpriteMesh;
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
public class QuadParentNode extends Node {

    @SerializedName("maxQuads")
    private int maxQuads;

    transient private ArrayList<SharedMeshQuad> quadChildren = new ArrayList<>();

    public QuadParentNode() {
        super();
    }

    @Override
    public QuadParentNode createInstance() {
        QuadParentNode copy = new QuadParentNode();
        return copy;
    }

    @Override
    public Node copy() {
        QuadParentNode copy = createInstance();
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

    @Override
    public void onCreated() {
        // Setup all children in this node
        SpriteMesh mesh = (SpriteMesh) getMeshes().get(0);
        for (Node n : getChildren()) {
            if (n instanceof SharedMeshQuad) {
                int index = addQuad((SharedMeshQuad) n);
                ((SharedMeshQuad) n).onCreated(mesh, index);
            }
        }
    }

}
