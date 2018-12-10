package com.graphicsengine.scene;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.nucleus.mmi.NodeInputListener;
import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;
import com.nucleus.ui.Toggle;

public class SharedMeshToggle extends SharedMeshQuad implements Toggle {

    public final static String SELECTED = "selected";
    public final static String SELECTED_FRAME = "selectedFrame";

    @SerializedName(SELECTED)
    private boolean selected = false;

    @SerializedName(SELECTED_FRAME)
    private int selectedFrame = -1;

    private ArrayList<ToggleListener> listeners = new ArrayList<>();

    public SharedMeshToggle() {
        super();
    }

    protected SharedMeshToggle(RootNode root) {
        super(root, GraphicsEngineNodeType.toggle);
    }

    public void addToggleListener(ToggleListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeToggleListener(ToggleListener listener) {
        listeners.remove(listener);
    }

    @Override
    public Node createInstance(RootNode root) {
        SharedMeshToggle copy = new SharedMeshToggle(root);
        copy.set(this);
        return copy;
    }

    public void set(SharedMeshToggle source) {
        selected = source.selected;
        selectedFrame = source.selectedFrame;
        super.set(source);
    }

    @Override
    public void toggleState() {
        selected = !selected;
        setFrame((selected ? (selectedFrame != -1 ? selectedFrame : getFrame() + 1) : getFrame()));

        // TODO - how shall the case when root has objectinputlistener and one or more listeners are registered in this
        // object be handled?
        NodeInputListener nodeListener = getRootNode().getObjectInputListener();
        if (nodeListener != null) {
            nodeListener.onStateChange(this);
        }
        for (ToggleListener l : listeners) {
            if (l != null) {
                l.onStateChanged(selected);
            }
        }
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

}
