package com.graphicsengine.renderer;

import com.graphicsengine.scene.SharedMeshQuad;
import com.nucleus.opengl.GLException;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.renderer.NucleusRenderer.NodeRenderer;
import com.nucleus.renderer.Pass;

public class SharedMeshNodeRenderer extends NodeRenderer<SharedMeshQuad> {

    @Override
    public void renderNode(NucleusRenderer renderer, SharedMeshQuad node, Pass currentPass, float[][] matrices)
            throws GLException {
        // Do nothing since this node shares mesh from parent.
    }

}
