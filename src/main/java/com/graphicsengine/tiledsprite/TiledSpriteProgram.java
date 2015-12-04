package com.graphicsengine.tiledsprite;

import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.Mesh.BufferIndex;
import com.nucleus.geometry.MeshBuilder;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLESWrapper.GLES20;
import com.nucleus.opengl.GLException;
import com.nucleus.opengl.GLUtils;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShaderVariable;
import com.nucleus.shader.ShaderVariable.VariableType;
import com.nucleus.texturing.Texture2D;
import com.nucleus.texturing.TiledTexture2D;
import com.nucleus.vecmath.Axis;

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
     * Index into aTileSprite2 z position
     */
    protected final static int ATTRIBUTE_SPRITE_Z_INDEX = 2;
    /**
     * Index into aTileSprite frame number, this is the sprite frame number to use.
     */
    protected final static int ATTRIBUTE_SPRITE_FRAME_INDEX = 3;
    /**
     * Index into aTileSprite texture u coordinate - this is used to calculate texture coordinate with frame.
     */
    protected final static int ATTRIBUTE_SPRITE_U_INDEX = 4;
    /**
     * Index into aTileSprite texture v coordinate - this is used to calculate texture coordinate with frame.
     */
    protected final static int ATTRIBUTE_SPRITE_V_INDEX = 5;
    /**
     * Index into aTileSprite z axis rotation
     */
    protected final static int ATTRIBUTE_SPRITE_ROTATION_INDEX = 6;

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
        // TODO - make into generic method that can be shared with PlayfieldProgram
        ShaderVariable[] attribs = new ShaderVariable[] { getShaderVariable(VARIABLES.aPosition.index) };
        int[] offsets = new int[] { 0 };
        VertexBuffer buffer = mesh.getVerticeBuffer(BufferIndex.VERTICES);
        gles.glVertexAttribPointer(buffer, GLES20.GL_ARRAY_BUFFER, attribs, offsets);
        GLUtils.handleError(gles, "glVertexAttribPointers ");

        ShaderVariable[] attribs2 = new ShaderVariable[] { getShaderVariable(VARIABLES.aTileSprite.index),
                getShaderVariable(VARIABLES.aTileSprite2.index) };
        // Keep offset in number of floats
        int[] offsets2 = new int[] { ATTRIBUTE_1_OFFSET, ATTRIBUTE_2_OFFSET };
        VertexBuffer buffer2 = mesh.getVerticeBuffer(BufferIndex.ATTRIBUTES);
        gles.glVertexAttribPointer(buffer2, GLES20.GL_ARRAY_BUFFER, attribs2, offsets2);
        GLUtils.handleError(gles, "glVertexAttribPointers ");
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
     * @param size Width and height of each sprite
     * @param translate Offset for each vertex, currently only Z used.
     * @param anchor Anchor for sprite, X,Y and Z
     * @param type The datatype for attribute data - GLES20.GL_FLOAT
     * 
     * @return The mesh that can be rendered.
     * @throws IllegalArgumentException if type is not GLES20.GL_FLOAT
     */
    public void buildMesh(Mesh mesh, Texture2D texture, int spriteCount, float[] size, float[] anchor) {

        int vertexStride = DEFAULT_COMPONENTS;
        float[] quadPositions = MeshBuilder.buildQuadPositionsIndexed(size[Axis.WIDTH.index], size[Axis.HEIGHT.index],
                anchor, vertexStride);
        MeshBuilder.buildQuadMeshIndexed(mesh, this, spriteCount, quadPositions, ATTRIBUTES_PER_VERTEX);

        createUniformStorage(mesh, shaderVariables);

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
