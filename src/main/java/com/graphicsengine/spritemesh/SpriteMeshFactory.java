package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineResourcesData;
import com.graphicsengine.scene.QuadParentNode;
import com.graphicsengine.scene.SharedMeshQuad;
import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.Mesh;
import com.nucleus.renderer.BufferObjectsFactory;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.texturing.UVTexture2D;

/**
 * Used to create tiled spritesheet.
 * The way to create a tiled spritesheet shall be through this class.
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteMeshFactory {

    private final static String INVALID_TYPE = "Invalid type: ";

    /**
     * This will create an old school sprite mesh, where each sprite has a frame, the sprite can be rotated in x axis
     * and positioned in x and y.
     * The attribute data will be prepared, ie when this call returns the mesh is ready to be rendered.
     * 
     * @param renderer
     * @param node The node that the mesh shall be created for
     * @param program The shader program to use with the mesh
     * @param scene
     * @return
     * @throws IOException
     */
    public static SpriteMesh create(NucleusRenderer renderer, SpriteMeshNode node,
            GraphicsEngineResourcesData resources)
            throws IOException {

        Mesh refMesh = resources.getMesh(node.getMeshRef());
        Texture2D texture = AssetManager.getInstance().getTexture(renderer,
                resources.getTexture2D(refMesh.getTextureRef()));
        ShaderProgram program = SpriteMeshFactory.createProgram(texture);
        SpriteMesh mesh = new SpriteMesh(refMesh);
        renderer.createProgram(program);
        mesh.createMesh(program, texture, node.getCount(), node.getSpriteRectangle());
        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, mesh);
        }
        return mesh;
    }

    public static Mesh create(NucleusRenderer renderer, QuadParentNode node, GraphicsEngineResourcesData resources)
            throws IOException {

        Mesh refMesh = resources.getMesh(node.getMeshRef());
        Texture2D texture = AssetManager.getInstance().getTexture(renderer,
                resources.getTexture2D(refMesh.getTextureRef()));
        ShaderProgram program = SpriteMeshFactory.createProgram(texture);
        SpriteMesh mesh = new SpriteMesh(refMesh);
        renderer.createProgram(program);
        mesh.createMesh(program, texture, node.getMaxQuads());
        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, mesh);
        }

        return mesh;
    }

    public static Mesh create(NucleusRenderer renderer, SharedMeshQuad node, GraphicsEngineResourcesData resources)
            throws IOException {

        // TODO shall it not be allowed to reference a mesh - maybe this node MUST share another node?
        if (node.getMeshRef() != null) {
            throw new IllegalArgumentException(node.getClass().getSimpleName() + " can not have a mesh reference.");
            // Using the parents mesh
        }
        return null;
    }

    /**
     * Creates the shader program to use with the specified texture.
     * 
     * @param texture {@link TiledTexture2D} or {@link UVTexture2D}
     * @return The shader program for the specified texture.
     */
    public static ShaderProgram createProgram(Texture2D texture) {
        switch (texture.type) {
        case TiledTexture2D:
            return new TiledSpriteProgram();
        case UVTexture2D:
            return new UVSpriteProgram();
        default:
            throw new IllegalArgumentException(INVALID_TYPE + texture.type);
        }

    }


}
