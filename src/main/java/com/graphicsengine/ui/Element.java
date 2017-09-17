package com.graphicsengine.ui;

import com.graphicsengine.scene.SharedMeshQuad;
import com.nucleus.scene.RootNode;

/**
 * Base class for UI elements, this is the base for focus and touch events to UI.
 * 
 * @author Richard Sahlin
 *
 */
public class Element extends SharedMeshQuad {

    /**
     * Used by GSON and {@link #createInstance(RootNode)} method - do NOT call directly
     */
	protected Element() {
		super();
	}
	
	public Element(RootNode root) {
		super(root);
	}
	
}
