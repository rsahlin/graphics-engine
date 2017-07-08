package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.graphicsengine.scene.QuadParentNode;
import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.Material;
import com.nucleus.geometry.Mesh;
import com.nucleus.io.ExternalReference;
import com.nucleus.renderer.BufferObjectsFactory;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.texturing.UVTexture2D;
import com.nucleus.texturing.Untextured;
import com.nucleus.vecmath.Rectangle;

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
     * @param resources
     * @param textureRef
     * @param count
     * @param spriteRect
     * @return The created sprite mesh
     * @throws IOException If there is an error fetching texture resource
     */
    public static SpriteMesh createSpriteMesh(NucleusRenderer renderer, ExternalReference textureRef,
            Material material, int count, Rectangle spriteRect) throws IOException {
        Texture2D texture = AssetManager.getInstance().getTexture(renderer, textureRef);
        ShaderProgram program = SpriteMeshFactory.createProgram(texture);
        SpriteMesh mesh = new SpriteMesh();
        renderer.createProgram(program);
        mesh.createMesh(program, texture, material, count, spriteRect);
        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, mesh);
        }
        return mesh;

    }

    public static Mesh create(NucleusRenderer renderer, QuadParentNode node)
            throws IOException {

        Texture2D texture = AssetManager.getInstance().getTexture(renderer, node.getTextureRef());
        ShaderProgram program = SpriteMeshFactory.createProgram(texture);
        SpriteMesh mesh = new SpriteMesh();
        renderer.createProgram(program);
        mesh.createMesh(program, texture, node.getMaterial(), node.getMaxQuads());
        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, mesh);
        }

        return mesh;
    }

    /**
     * Creates the shader program to use with the specified texture.
     * 
     * @param texture {@link TiledTexture2D} or {@link UVTexture2D}
     * @return The shader program for the specified texture.
     */
    public static ShaderProgram createProgram(Texture2D texture) {
        switch (texture.textureType) {
        case TiledTexture2D:
            return new TiledSpriteProgram();
        case UVTexture2D:
            return new UVSpriteProgram();
        case Untextured:
            return new UntexturedSpriteProgram(((Untextured) texture).getShading());
        default:
            throw new IllegalArgumentException(INVALID_TYPE + texture.textureType);
        }

    }


}
