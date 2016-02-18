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
     * @param source The charset data
     * @param program The shader program to use with the Mesh
     * @param textureData The texture data to be used for the charset
     * @return The created playfield
     * @throws IOException
     */
    public static PlayfieldMesh create(NucleusRenderer renderer, PlayfieldNode source, ShaderProgram program,
            TiledTexture2D textureData) throws IOException {

        PlayfieldMesh sourceMesh = source.getPlayfieldMesh();
        PlayfieldMesh map = new PlayfieldMesh(sourceMesh);
        renderer.createProgram(program);
        Texture2D texture = AssetManager.getInstance().getTexture(renderer, textureData);
        map.createMesh(program, texture);
        float[] size = new float[2];
        int[] mapSize = source.getMapSize();
        size[0] = mapSize[0] * sourceMesh.getTileWidth();
        size[1] = mapSize[1] * sourceMesh.getTileHeight();
        PropertyMapper mapper = new PropertyMapper(program);
        map.setupCharmap(mapper, source.getMapSize());

        if (Configuration.getInstance().isUseVBO()) {
            BufferObjectsFactory.getInstance().createVBOs(renderer, map);
        }

        int charCount = map.getCount();
        float[] attributeData = map.getAttributeData();
        for (int i = 0; i < charCount; i++) {
            MeshBuilder.prepareTiledUV(mapper, attributeData, i);
        }

        return map;
    }

    /**
     * Factory method for creating the playfield mesh, after this call the playfield can be rendered, it must
     * be filled with map data.
     * 
     * @param renderer
     * @param source The source playfield controller, the mesh will be created based on the data in this.
     * @return The created mesh
     * @throws IOException
     */
    public static PlayfieldMesh create(NucleusRenderer renderer, PlayfieldNode source,
            GraphicsEngineRootNode scene)
            throws IOException {

        TiledTexture2D textureData = (TiledTexture2D) scene.getResources().getTexture2D(
                source.getPlayfieldMesh().getTextureRef());

        return create(renderer, source, new PlayfieldProgram(), textureData);
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
