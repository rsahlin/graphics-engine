package com.graphicsengine.spritemesh;

import com.nucleus.geometry.Mesh;
import com.nucleus.renderer.NucleusRenderer;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.texturing.UVTexture2D;
import com.nucleus.texturing.Untextured;

/**
 * SpriteMesh using a geometry shader
 * 
 *
 */
public class SpriteGeometryMesh extends SpriteMesh {

    protected final static String INVALID_TYPE = "Invalid type: ";

    /**
     * Builder for sprite meshes
     *
     */
    public static class Builder extends SpriteMesh.Builder {

        public Builder(NucleusRenderer renderer) {
            super(renderer);
        }

        @Override
        protected Mesh createMesh() {
            return new SpriteGeometryMesh();
        }

        /**
         * Creates the shader program to use with the specified texture.
         * 
         * @param texture {@link TiledTexture2D} or {@link UVTexture2D}
         * @return The shader program for the specified texture.
         */
        @Override
        public ShaderProgram createProgram(Texture2D texture) {
            switch (texture.textureType) {
                case TiledTexture2D:
                    return new TiledSpriteProgram(Texture2D.Shading.textured);
                case UVTexture2D:
                    return new UVSpriteProgram();
                case Untextured:
                    return new TiledSpriteProgram(((Untextured) texture).getShading());
                case Texture2D:
                    // TODO - fix so that transformprogram loads the correct shader - 'transformvertex', currently
                    // loads texturedvertex. Use tiled or uv texture in the meantime.
                    // return new TransformProgram(null, Texture2D.Shading.textured, null);
                default:
                    throw new IllegalArgumentException(INVALID_TYPE + texture.textureType);
            }
        }
    }
}
