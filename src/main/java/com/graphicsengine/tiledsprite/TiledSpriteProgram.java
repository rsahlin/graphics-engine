package com.graphicsengine.tiledsprite;

import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.Mesh.BufferIndex;
import com.nucleus.geometry.MeshBuilder;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLException;
import com.nucleus.opengl.GLUtils;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShaderVariable;
import com.nucleus.shader.ShaderVariable.VariableType;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;

/**
 * This class defines the mappings for the tile sprite vertex and fragment shaders.
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSpriteProgram extends ShaderProgram {

    /**
     * Number of vertices per sprite - this is for a quad that is created using element buffer.
     */
    protected final static int VERTICES_PER_SPRITE = 4;
    /**
     * Draw using an index list each quad is made up of 6 indices (2 triangles)
     */
    protected final static int INDICES_PER_SPRITE = 6;

    /**
     * Default number of components (x,y,z)
     */
    protected final static int DEFAULT_COMPONENTS = 3;

    private final static String ILLEGAL_DATATYPE_STR = "Illegal datatype: ";

    /**
     * Index into uniform sprite data data where the texture fraction s (width) is
     */
    private final static int UNIFORM_TEX_FRACTION_S_INDEX = 0;
    /**
     * Index into uniform sprite data data where the texture fraction t (height) is
     */
    protected final static int UNIFORM_TEX_FRACTION_T_INDEX = 1;

    /**
     * Index into uniform sprite data where 1 / texture fraction w - this is used to calculate y pos from frame index
     */
    protected final static int UNIFORM_TEX_ONEBY_S_INDEX = 2;

    /**
     * Number of float data per vertex
     */
    protected final static int ATTRIBUTES_PER_VERTEX = 8;
    /**
     * Number of floats for each tiled sprite in the attribute data.
     */
    public final static int ATTRIBUTES_PER_SPRITE = ATTRIBUTES_PER_VERTEX * VERTICES_PER_SPRITE;

    protected final static int ATTRIBUTE_1_OFFSET = 0;
    protected final static int ATTRIBUTE_2_OFFSET = 4;

    /**
     * Index into aTileSprite for x position
     */
    protected final static int ATTRIBUTE_SPRITE_X_INDEX = 0;
    /**
     * Index into aTileSprite for y position
     */
    protected final static int ATTRIBUTE_SPRITE_Y_INDEX = 1;
    /**
     * Index into aTileSprite texture u coordinate - this is used to calculate texture coordinate with frame.
     */
    protected final static int ATTRIBUTE_SPRITE_U_INDEX = 2;
    /**
     * Index into aTileSprite texture v coordinate - this is used to calculate texture coordinate with frame.
     */
    protected final static int ATTRIBUTE_SPRITE_V_INDEX = 3;
    /**
     * Index into aTileSprite frame number, this is the sprite frame number to use.
     */
    protected final static int ATTRIBUTE_SPRITE_FRAME_INDEX = 4;
    /**
     * Index into aTileSprite z axis rotation
     */
    protected final static int ATTRIBUTE_SPRITE_ROTATION_INDEX = 5;

    public enum VARIABLES {
        uMVPMatrix(0, ShaderVariable.VariableType.UNIFORM),
        uSpriteData(1, ShaderVariable.VariableType.UNIFORM),
        aPosition(2, ShaderVariable.VariableType.ATTRIBUTE),
        aTileSprite(3, ShaderVariable.VariableType.ATTRIBUTE),
        aTileSprite2(4, ShaderVariable.VariableType.ATTRIBUTE);

        public final int index;
        private final VariableType type;

        private VARIABLES(int index, VariableType type) {
            this.index = index;
            this.type = type;
        }

    }

    private final static String VERTEX_SHADER_NAME = "assets/tiledspritevertex.essl";
    private final static String FRAGMENT_SHADER_NAME = "assets/tiledspritefragment.essl";

    public TiledSpriteProgram() {
        super();
    }

    @Override
    public int getVariableIndex(ShaderVariable variable) {
        return VARIABLES.valueOf(getVariableName(variable)).index;
    }

    @Override
    public int getVariableCount() {
        return VARIABLES.values().length;
    }

    @Override
    public void bindAttributes(GLES20Wrapper gles, Mesh mesh) throws GLException {

        VertexBuffer buffer = mesh.getVerticeBuffer(BufferIndex.VERTICES);
        ShaderVariable attrib = getShaderVariable(VARIABLES.aPosition.index);
        gles.glEnableVertexAttribArray(attrib.getLocation());
        GLUtils.handleError(gles, "glEnableVertexAttribArray1 ");
        gles.glVertexAttribPointer(attrib.getLocation(), buffer.getComponentCount(), buffer.getDataType(), false,
                buffer.getByteStride(), buffer.getBuffer().position(0));
        GLUtils.handleError(gles, "glVertexAttribPointer1 ");
        ShaderVariable attrib2 = getShaderVariable(VARIABLES.aTileSprite.index);
        gles.glEnableVertexAttribArray(attrib2.getLocation());
        GLUtils.handleError(gles, "glEnableVertexAttribArray2 ");
        VertexBuffer buffer2 = mesh.getVerticeBuffer(BufferIndex.ATTRIBUTES);
        gles.glVertexAttribPointer(attrib2.getLocation(), buffer2.getComponentCount(), buffer2.getDataType(), false,
                buffer2.getByteStride(), buffer2.getBuffer().position(ATTRIBUTE_1_OFFSET));
        ShaderVariable attrib3 = getShaderVariable(VARIABLES.aTileSprite2.index);
        if (attrib3 != null) {
            gles.glEnableVertexAttribArray(attrib3.getLocation());
            GLUtils.handleError(gles, "glEnableVertexAttribArray3 ");
            VertexBuffer buffer3 = mesh.getVerticeBuffer(BufferIndex.ATTRIBUTES);
            gles.glVertexAttribPointer(attrib3.getLocation(), buffer3.getComponentCount(), buffer3.getDataType(),
                    false,
                    buffer3.getByteStride(), buffer3.getBuffer().position(ATTRIBUTE_2_OFFSET));
        }
        GLUtils.handleError(gles, "glVertexAttribPointer3 ");

    }

    @Override
    public void bindUniforms(GLES20Wrapper gles, float[] modelviewMatrix, Mesh mesh) throws GLException {
        ShaderVariable v = getShaderVariable(VARIABLES.uMVPMatrix.index);
        System.arraycopy(modelviewMatrix, 0, mesh.getUniformMatrices(), 0, modelviewMatrix.length);
        gles.glUniformMatrix4fv(getShaderVariable(VARIABLES.uMVPMatrix.index).getLocation(), v.getSize(), false,
                mesh.getUniformMatrices(), 0);
        GLUtils.handleError(gles, "glUniformMatrix4fv ");
        v = getShaderVariable(VARIABLES.uSpriteData.index);
        if (v != null) {
            setVectorUniform(gles, v, mesh.getUniformVectors(), 0);
        }
        GLUtils.handleError(gles, "glUniform4fv ");
    }

    /**
     * Builds a mesh with data that can be rendered using a tiled sprite renderer, this will draw a number of
     * sprites using one drawcall.
     * Vertex buffer will have storage for XYZ + UV.
     * 
     * @param mesh The mesh to build buffers for, this mesh can be rendered after this call.
     * @param texture Must be {@link TiledTexture2D} if tiling shall work
     * @param spriteCount Number of sprites to build, this is NOT the vertex count.
     * @param width The width of a sprite, the sprite will be centered in the middle.
     * @param height The height of a sprite, the sprite will be centered in the middle.
     * @param z The z position for each vertice.
     * @param type The datatype for attribute data - GLES20.GL_FLOAT
     * 
     * @return The mesh that can be rendered.
     * @throws IllegalArgumentException if type is not GLES20.GL_FLOAT
     */
    public void buildTileSpriteMesh(Mesh mesh, Texture2D texture, int spriteCount, float width, float height, float z,
            int type) {

        int vertexStride = DEFAULT_COMPONENTS;
        float[] quadPositions = MeshBuilder.buildQuadPositionsIndexed(width, height, z, width / 2, height / 2,
                vertexStride);
        MeshBuilder.buildQuadMeshIndexed(mesh, this, spriteCount, quadPositions, ATTRIBUTES_PER_VERTEX);

        setUniformArrays(mesh, getShaderVariable(VARIABLES.uSpriteData.index),
                getShaderVariable(VARIABLES.uMVPMatrix.index));

        float[] uniformVectors = mesh.getUniformVectors();
        if (texture instanceof TiledTexture2D) {
            setTextureUniforms((TiledTexture2D) texture, uniformVectors, UNIFORM_TEX_FRACTION_S_INDEX);
        } else {
            uniformVectors[UNIFORM_TEX_FRACTION_S_INDEX] = 1f;
            uniformVectors[UNIFORM_TEX_FRACTION_T_INDEX] = 1f;
            uniformVectors[UNIFORM_TEX_ONEBY_S_INDEX] = 1f;
        }
    }

    @Override
    public void createProgram(GLES20Wrapper gles) {
        createProgram(gles, VERTEX_SHADER_NAME, FRAGMENT_SHADER_NAME);
    }

}
