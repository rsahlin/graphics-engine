package com.graphicsengine.scene;

import java.util.ArrayList;

import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;
import com.nucleus.ui.Toggle;

public class SharedMeshToggle extends SharedMeshQuad implements Toggle {

    private boolean selected = false;

    private ArrayList<ToggleListener> listeners = new ArrayList<>();

    protected SharedMeshToggle(RootNode root) {
        super(root, GraphicsEngineNodeType.toggle);
    }

    @Override
    public Node createInstance(RootNode root) {
        SharedMeshToggle copy = new SharedMeshToggle(root);
        copy.set(this);
        return copy;
    }

    @Override
    public void toggleState() {
        selected = !selected;
        for (ToggleListener l : listeners) {
            if (l != null) {
                l.onStateChanged(selected);
            }
        }
    }

}
