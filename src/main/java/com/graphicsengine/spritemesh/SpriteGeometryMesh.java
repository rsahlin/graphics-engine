package com.graphicsengine.spritemesh;

import com.graphicsengine.component.SpriteAttributeComponent;
import com.nucleus.geometry.Mesh;
import com.nucleus.renderer.NucleusRenderer;

/**
 * SpriteMesh using a geometry shader
 * 
 *
 */
public class SpriteGeometryMesh extends SpriteMesh {

    /**
     * Builder for sprite meshes
     *
     */
    public static class Builder extends Mesh.Builder<SpriteGeometryMesh> {
        /**
         * Internal constructor - avoid using directly if the mesh should belong to a specific node type.
         * Use
         * {@link SpriteAttributeComponent#createMeshBuilder(NucleusRenderer, com.nucleus.scene.ComponentNode, int, com.nucleus.vecmath.Rectangle)}
         * instead
         * 
         * @param renderer
         */
        Builder(NucleusRenderer renderer) {
            super(renderer);
        }

    }
}
