package com.graphicsengine.map;

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
import com.nucleus.texturing.TiledTexture2D;

/**
 * Use to create charmaps
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldMeshFactory {

    /**
     * Factory method for creating the playfield mesh.
     * After this call the playfield can be rendered but it will not contain any specific charmap data.
     * 
     * @param renderer
     * @param node The node that the mesh shall be in
     * @param program The shader program to use with the Mesh
     * @param textureData The texture data to be used for the charset
     * @return The created playfield
     * @throws IOException
     */
    public static PlayfieldMesh create(NucleusRenderer renderer, PlayfieldNode node, ShaderProgram program,
            TiledTexture2D textureData) throws IOException {

        PlayfieldMesh mesh = node.getPlayfieldMesh();
        renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer, textureData);
        mesh.createMesh(program, texture);
        float[] size = new float[2];
        int[] mapSize = node.getMapSize();
        size[0] = mapSize[0] * mesh.getTileWidth();
        size[1] = mapSize[1] * mesh.getTileHeight();
        PropertyMapper mapper = new PropertyMapper(program);
        mesh.setupCharmap(mapper, node.getMapSize());

        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, mesh);
        }

        int charCount = mesh.getCount();
        float[] attributeData = mesh.getAttributeData();
        for (int i = 0; i < charCount; i++) {
            MeshBuilder.prepareTiledUV(mapper, attributeData, i);
        }

        return mesh;
    }

    /**
     * Factory method for creating the playfield mesh, after this call the playfield can be rendered, it must
     * be filled with map data.
     * 
     * @param renderer
     * @param node The source playfield, the mesh will be created in this node.
     * @return The created mesh
     * @throws IOException
     */
    public static PlayfieldMesh create(NucleusRenderer renderer, PlayfieldNode node,
            GraphicsEngineRootNode scene)
            throws IOException {

        TiledTexture2D textureData = (TiledTexture2D) scene.getResources().getTexture2D(
                node.getPlayfieldMesh().getTextureRef());

        return create(renderer, node, new PlayfieldProgram(), textureData);
    }

    /**
     * Creates a new playfield from the controller source
     * This playfield will contain the mapdata from the source, the id will be the mapReference from the source.
     * Use this when exporting
     * 
     * @param source
     * @return
     */
    public static Playfield createPlayfield(PlayfieldNode source) {
        return new Playfield(source);
    }

}
