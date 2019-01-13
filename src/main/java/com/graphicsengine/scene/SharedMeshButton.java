package com.graphicsengine.scene;

import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;
import com.nucleus.ui.Button;

public class SharedMeshButton extends SharedMeshQuad implements Button {

    public SharedMeshButton() {
        super();
    }

    protected SharedMeshButton(RootNode root) {
        super(root, GraphicsEngineNodeType.button);
    }

    @Override
    public Node createInstance(RootNode root) {
        SharedMeshButton copy = new SharedMeshButton(root);
        copy.set(this);
        return copy;
    }

    @Override
    public void pressed() {
    }

}
