package com.graphicsengine.scene;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.nucleus.mmi.NodeInputListener;
import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;
import com.nucleus.ui.Toggle;

public class SharedMeshToggle extends SharedMeshQuad implements Toggle {

    public final static String SELECTED = "selected";
    public final static String SELECTED_FRAMES = "selectedFrames";

    @SerializedName(SELECTED)
    private int selected = 0;

    @SerializedName(SELECTED_FRAMES)
    private int selectedFrames[];

    private ArrayList<ToggleListener> listeners = new ArrayList<>();

    /**
     * Used by GSON and {@link #createInstance(RootNode)} method - do NOT call directly
     */
    @Deprecated
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
    public void onCreated() {
        super.onCreated();
        setFrame(selectedFrames[selected]);
    }

    @Override
    public Node createInstance(RootNode root) {
        SharedMeshToggle copy = new SharedMeshToggle(root);
        copy.set(this);
        return copy;
    }

    public void set(SharedMeshToggle source) {
        selected = source.selected;
        selectedFrames = new int[source.selectedFrames.length];
        System.arraycopy(source.selectedFrames, 0, selectedFrames, 0, selectedFrames.length);
        super.set(source);
    }

    @Override
    public void toggle() {
        selected++;
        if (selected >= selectedFrames.length) {
            selected = 0;
        }
        setFrame(selectedFrames[selected]);

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
    public int getSelected() {
        return selected;
    }

    @Override
    public void setSelected(int selected) {
        throw new IllegalArgumentException("Not implemented");
    }

}
