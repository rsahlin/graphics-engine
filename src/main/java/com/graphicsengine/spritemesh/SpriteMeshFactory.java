package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.Mesh;
import com.nucleus.renderer.BufferObjectsFactory;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.scene.Node;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D;

/**
 * Used to create tiled spritesheet.
 * The way to create a tiled spritesheet shall be through this class.
 * 
 * @author Richard Sahlin
 *
 */
public class SpriteMeshFactory {

    /**
     * This will create an old school sprite mesh, where each sprite has a frame, the sprite can be rotated in x axis
     * and positioned in x and y.
     * 
     * @param renderer
     * @param source The sprite controller source, an instance of this will be created.
     * @param program The shader program to use with the mesh
     * @param scene
     * @return
     * @throws IOException
     */
    public static SpriteMesh create(NucleusRenderer renderer, SpriteMeshNode source, ShaderProgram program,
            GraphicsEngineRootNode scene) throws IOException {

        SpriteMesh sourceMesh = source.getSpriteSheet();
        SpriteMesh sprites = new SpriteMesh(sourceMesh);
        renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer,
                scene.getResources().getTexture2D(source.getSpriteSheet().getTextureRef()));

        sprites.createMesh(program, texture);
        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, sprites);
        }
        return sprites;
    }

    public static void create(NucleusRenderer renderer, Node source, Mesh mesh, ShaderProgram program,
            GraphicsEngineRootNode scene) throws IOException {

        renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer,
                scene.getResources().getTexture2D(mesh.getTextureRef()));

        mesh.createMesh(program, texture);
        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, mesh);
        }
    }
}
