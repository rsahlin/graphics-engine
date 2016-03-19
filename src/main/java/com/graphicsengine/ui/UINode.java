package com.graphicsengine.ui;

import com.nucleus.scene.Node;

/**
 * Node containing UI elements, the intended usage is to group UI elements contained in the same screen into one UI
 * node.
 * This class can be serialized using GSON
 * 
 * @author Richard Sahlin
 *
 */
public class UINode extends Node {

    public UINode() {
        super();
    }

    @Override
    public UINode createInstance() {
        return new UINode();
    }

    @Override
    public Node copy() {
        UINode copy = createInstance();
        copy.set(this);
        return copy;
    }

}
