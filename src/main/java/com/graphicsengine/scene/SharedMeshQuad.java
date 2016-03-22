package com.graphicsengine.scene;

import com.graphicsengine.spritemesh.SpriteMesh;
import com.nucleus.scene.Node;

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

    public SharedMeshQuad() {
    }

    public void onCreated(SpriteMesh mesh, int index) {
        this.childIndex = index;
        mesh.buildQuad(index, mesh.getMaterial().getProgram(), getSize(), getAnchor());
        if (transform == null) {
            mesh.setScale(index, 1, 1, 1);
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
        return new SharedMeshQuad();
    }

    @Override
    public Node copy() {
        SharedMeshQuad copy = createInstance();
        copy.set(this);
        return copy;

    }


}
