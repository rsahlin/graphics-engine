package com.graphicsengine.spritemesh;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.geometry.MeshBuilder;
import com.nucleus.renderer.BufferObjectsFactory;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
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
    public static SpriteMesh create(NucleusRenderer renderer, SpriteMeshNode node, GraphicsEngineRootNode scene)
            throws IOException {

        SpriteMesh mesh = node.getSpriteSheet();
        Texture2D texture = AssetManager.getInstance().getTexture(renderer,
                scene.getResources().getTexture2D(mesh.getTextureRef()));
        ShaderProgram program = null;
        switch (texture.type) {
        case TiledTexture2D:
            program = new TiledSpriteProgram();
            break;
        case UVTexture2D:
            program = new UVSpriteProgram();
            break;
        default:
            throw new IllegalArgumentException(INVALID_TYPE + texture.type);
        }
        renderer.createProgram(program);
        mesh.createMesh(program, texture, node.getCount(), node.getSize(), node.getAnchor());
        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, mesh);
        }

        float[] attributeData = mesh.getAttributeData();
        PropertyMapper mapper = new PropertyMapper(program);
        for (int i = 0; i < node.getCount(); i++) {
            if (program instanceof TiledSpriteProgram) {
                MeshBuilder.prepareTiledUV(mapper, attributeData, i);
            } else if (program instanceof UVSpriteProgram) {
            } else {
                throw new IllegalArgumentException();
            }
        }

        return mesh;
    }

}
