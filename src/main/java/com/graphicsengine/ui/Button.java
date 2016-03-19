package com.graphicsengine.ui;

import com.nucleus.scene.Node;

/**
 * A button ui component
 * 
 * @author Richard Sahlin
 *
 */
public class Button extends Element {


    public Button() {
    }

    @Override
    public Button createInstance() {
        return new Button();
    }

    @Override
    public Node copy() {
        Button copy = createInstance();
        copy.set(this);
        return copy;

    }


}
