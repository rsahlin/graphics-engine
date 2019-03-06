package com.graphicsengine.scene;

import com.nucleus.mmi.Pointer;
import com.nucleus.mmi.UIElementInput;
import com.nucleus.profiling.FrameSampler;
import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;
import com.nucleus.ui.Button;

public class SharedMeshButton extends SharedMeshQuad implements Button {

    protected static FrameSampler timeKeeper = FrameSampler.getInstance();

    protected Action action = Action.NONE;
    protected float duration;
    protected float clickAnimTimeout = 0.1f;

    protected Action nextAction = null;

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

    /**
     * The button was clicked, update button on screen and dispatch {@link ButtonListener} if attached.
     * This will be called on the thread issuing touch events and is not synced to any drawing/update
     */
    protected void clicked() {
        switch (action) {
            case NONE:
            case PRESSED:
                nextAction = Action.CLICKED;
                break;
            default:
                // Nothing to do
        }
    }

    private void beginClicked() {
        this.getTransform().setScale(new float[] { 0.7f, 0.7f, 0.7f });
        updateTransform();
        duration = 0;
    }

    private void endClicked() {
        this.getTransform().setScale(new float[] { 1f, 1f, 1f });
        updateTransform();
        action = Action.NONE;
    }

    private Action dispatchAction(Action action) {
        this.action = action;
        switch (action) {
            case CLICKED:
                beginClicked();
                return null;
            default:
                return null;
        }
    }

    @Override
    protected void prepareRender() {
        if (nextAction != null) {
            nextAction = dispatchAction(nextAction);
        } else {
            switch (action) {
                case CLICKED:
                    duration += timeKeeper.getDelta();
                    if (duration >= clickAnimTimeout) {
                        endClicked();
                    }
                default:
            }
        }
    }

    @Override
    public void onInputEvent(Pointer event) {
    }

    @Override
    public void onClick(Pointer event, UIElementInput listener) {
        clicked();
        if (listener != null) {
            listener.onPressed(this);
        }
    }

    @Override
    public Type getElementType() {
        return Type.BUTTON;
    }

}
