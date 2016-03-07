package com.graphicsengine.ui;

import java.io.IOException;

import com.graphicsengine.io.GraphicsEngineRootNode;
import com.nucleus.assets.AssetManager;
import com.nucleus.geometry.Mesh;
import com.nucleus.renderer.Configuration;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;

/**
 * Factory for UI meshes
 * @author Richard Sahlin
 *
 */
public class UIMeshFactory {


    public static Mesh create(NucleusRenderer renderer, Button node, GraphicsEngineRootNode scene)
            throws IOException {

        Mesh refMesh = scene.getResources().getMesh(node.getMeshRef());
        TiledTexture2D textureData = (TiledTexture2D) scene.getResources().getTexture2D(
                refMesh.getTextureRef());

        // PlayfieldProgram program = new PlayfieldProgram();
        // renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer, textureData);
        // PlayfieldMesh playfieldMesh = new PlayfieldMesh(refMesh);
        // playfieldMesh.createMesh(program, texture, node.getMapSize(), node.getCharSize(), node.getAnchor());
        // playfieldMesh.setupCharmap(node.getMapSize(), node.getCharSize(), node.getAnchor());

        if (Configuration.getInstance().isUseVBO()) {
            // BufferObjectsFactory.getInstance().createVBOs(renderer, playfieldMesh);
        }

        // int charCount = node.getMapSize()[0] * node.getMapSize()[1];
        // float[] attributeData = playfieldMesh.getAttributeData();
        // for (int i = 0; i < charCount; i++) {
        // MeshBuilder.prepareTiledUV(playfieldMesh.getMapper(), attributeData, i);
        // }

        return null;
    }

}
