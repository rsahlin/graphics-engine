package com.graphicsengine.scene;

import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;
import com.nucleus.mmi.Pointer;
import com.nucleus.mmi.UIElementInput;
import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;
import com.nucleus.ui.Toggle;

public class SharedMeshToggle extends SharedMeshQuad implements Toggle {

    public final static String SELECTED = "selected";
    public final static String SELECTED_FRAMES = "selectedFrames";

    /**
     * Default is to start with selected frame 1 which means selected
     */
    @SerializedName(SELECTED)
    private int selected = 1;

    /**
     * Index 0 is unselected, index 1 is selected
     */
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
        if (selectedFrames == null || selectedFrames.length > 2) {
            throw new IllegalArgumentException("Toggle must have 2 selected frames");
        }
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

    /**
     * Selects the next item
     */
    protected void toggle() {
        selected++;
        if (selected >= selectedFrames.length) {
            selected = 0;
        }
        setFrame(selectedFrames[selected]);
        for (ToggleListener l : listeners) {
            if (l != null) {
                l.onStateChanged(this);
            }
        }
    }

    @Override
    public boolean isSelected() {
        return selected != 0;
    }

    @Override
    public void onInputEvent(Pointer event) {
    }

    @Override
    public void onClick(Pointer event, UIElementInput listener) {
        toggle();
        if (listener != null) {
            listener.onStateChange(this);
        }
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = !selected ? 0 : 1;
    }

    @Override
    public Type getElementType() {
        return Type.TOGGLE;
    }

}
