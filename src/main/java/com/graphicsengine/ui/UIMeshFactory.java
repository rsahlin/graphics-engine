package com.graphicsengine.ui;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.graphicsengine.spritemesh.SpriteMeshFactory;
import com.graphicsengine.spritemesh.TiledSpriteProgram;
import com.graphicsengine.spritemesh.UVSpriteProgram;
import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.AttributeUpdater.PropertyMapper;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshBuilder;
import com.nucleus.renderer.BufferObjectsFactory;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D;

/**
 * Factory for UI meshes
 * @author Richard Sahlin
 *
 */
public class UIMeshFactory {


    public static Mesh create(NucleusRenderer renderer, Button node, GraphicsEngineRootNode scene)
            throws IOException {

        Mesh refMesh = scene.getResources().getMesh(node.getMeshRef());
        Texture2D texture = AssetManager.getInstance().getTexture(renderer,
                scene.getResources().getTexture2D(refMesh.getTextureRef()));
        ShaderProgram program = SpriteMeshFactory.createProgram(texture);
        UIMesh mesh = new UIMesh(refMesh);
        renderer.createProgram(program);
        mesh.createMesh(program, texture, 1, node.getSize(), node.getAnchor());
        mesh.setScale(1, 1, 1);
        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, mesh);
        }

        float[] attributeData = mesh.getAttributeData();
        PropertyMapper mapper = new PropertyMapper(program);
        if (program instanceof TiledSpriteProgram) {
            MeshBuilder.prepareTiledUV(mapper, attributeData, 0);
        } else if (program instanceof UVSpriteProgram) {
        } else {
            throw new IllegalArgumentException();
        }

        return mesh;
    }

}
