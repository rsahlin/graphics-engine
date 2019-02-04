package com.graphicsengine.scene;

import com.nucleus.profiling.FrameSampler;
import com.nucleus.scene.Node;
import com.nucleus.scene.RootNode;
import com.nucleus.ui.Button;

public class SharedMeshButton extends SharedMeshQuad implements Button {

    protected static FrameSampler timeKeeper = FrameSampler.getInstance();

    protected Action action = Action.NONE;

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
    public void clicked() {
        action = Action.CLICKED;
        this.getTransform().setScale(new float[] { 0.7f, 0.7f, 0.7f });
        updateTransform();
    }

    @Override
    protected void prepareRender() {
        switch (action) {
            case CLICKED:
                float delta = timeKeeper.getDelta();
                this.getTransform().setScale(new float[] { 1f, 1f, 1f });
                action = Action.NONE;
            default:
        }
    }

}
