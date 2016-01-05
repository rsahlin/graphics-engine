package com.graphicsengine.map;

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

/**
 * This class defines the mappings for the charset vertex and fragment shaders.
 * 
 * @author Richard Sahlin
 *
 */
public class PlayfieldProgram extends ShaderProgram {

    /**
     * Number of vertices per char - this is for a quad that is created using element buffer.
     */
    protected final static int VERTICES_PER_CHAR = 4;
    /**
     * Draw using an index list each quad is made up of 6 indices (2 triangles)
     */
    protected final static int INDICES_PER_CHAR = 6;

    /**
     * Default number of components (x,y,z)
     */
    protected final static int DEFAULT_COMPONENTS = 3;

    private final static String ILLEGAL_DATATYPE_STR = "Illegal datatype: ";

    /**
     * Index into uniform char data where the texture fraction s (width) is
     */
    protected final static int UNIFORM_TEX_FRACTION_S_INDEX = 0;
    /**
     * Index into uniform charmap data where the texture fraction t (height) is
     */
    protected final static int UNIFORM_TEX_FRACTION_T_INDEX = 1;

    /**
     * Index into uniform charmap data where 1 / texture fraction w - this is used to calculate y pos from frame index
     */
    protected final static int UNIFORM_TEX_ONEBY_S_INDEX = 2;

    /**
     * Number of float data per vertex
     */
    protected final static int ATTRIBUTES_PER_VERTEX = 8;
    /**
     * Number of floats for each char in the attribute data.
     */
    protected final static int ATTRIBUTES_PER_CHAR = PlayfieldProgram.ATTRIBUTES_PER_VERTEX
            * PlayfieldProgram.VERTICES_PER_CHAR;

    protected final static int ATTRIBUTE_1_OFFSET = 0;
    protected final static int ATTRIBUTE_2_OFFSET = 4;

    /**
     * Index into aCharset for x position
     */
    protected final static int ATTRIBUTE_CHARMAP_X_INDEX = 0;
    /**
     * Index into aCharset for y position
     */
    protected final static int ATTRIBUTE_CHARMAP_Y_INDEX = 1;
    /**
     * Index into aCharset texture u coordinate - this is used to calculate texture coordinate with frame.
     */
    protected final static int ATTRIBUTE_CHARMAP_U_INDEX = 2;
    /**
     * Index into aCharset texture v coordinate - this is used to calculate texture coordinate with frame.
     */
    protected final static int ATTRIBUTE_CHARMAP_V_INDEX = 3;
    /**
     * Index into aCharset frame number, this is the charmap frame number to use.
     */
    protected final static int ATTRIBUTE_CHARMAP_FRAME_INDEX = 4;
    /**
     * Index into aCharset flags, controls x and y axis flip.
     */
    protected final static int ATTRIBUTE_CHARMAP_FLAGS_INDEX = 5;

    public enum VARIABLES {
        uMVPMatrix(0, ShaderVariable.VariableType.UNIFORM),
        uCharsetData(1, ShaderVariable.VariableType.UNIFORM),
        aPosition(2, ShaderVariable.VariableType.ATTRIBUTE),
        aCharset(3, ShaderVariable.VariableType.ATTRIBUTE),
        aCharset2(4, ShaderVariable.VariableType.ATTRIBUTE);

        public final int index;
        private final VariableType type;

        private VARIABLES(int index, VariableType type) {
            this.index = index;
            this.type = type;
        }

    }

    private final static String VERTEX_SHADER_NAME = "assets/charmapvertex.essl";
    private final static String FRAGMENT_SHADER_NAME = "assets/charmapfragment.essl";

    public PlayfieldProgram() {
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
        // TODO - make into generic method that can be shared with TiledSpriteProgram
        ShaderVariable[] attribs = new ShaderVariable[] { getShaderVariable(VARIABLES.aPosition.index) };
        int[] offsets = new int[] { 0 };
        VertexBuffer buffer = mesh.getVerticeBuffer(BufferIndex.VERTICES);
        gles.glVertexAttribPointer(buffer, GLES20.GL_ARRAY_BUFFER, attribs, offsets);
        GLUtils.handleError(gles, "glVertexAttribPointers ");

        ShaderVariable[] attribs2 = new ShaderVariable[] { getShaderVariable(VARIABLES.aCharset.index),
                getShaderVariable(VARIABLES.aCharset2.index) };
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
        v = getShaderVariable(VARIABLES.uCharsetData.index);
        if (v != null) {
            setVectorUniform(gles, v, mesh.getUniformVectors(), 0);
        }
        GLUtils.handleError(gles, "glUniform4fv ");
    }

    /**
     * Builds a mesh with data that can be rendered using a tiled charmap renderer, this will draw a number of
     * charmaps using one drawcall.
     * Vertex buffer will have storage for XYZ + UV.
     * Before using the mesh the chars needs to be positioned, this call just creates the buffers. All chars will
     * have a position of 0.
     * 
     * @param mesh The mesh to build buffers for
     * @param texture The texture source, if tiling shall be used it must be {@link TiledTexture2D}
     * @param charCount Number of chars to build, this is NOT the vertex count.
     * @param charSizeThe width and height of each char
     * @param translate Translate in X,Y and Z. 0 for upper left corner, half -(width/height) for center. Z is set as
     * the Z position
     * @param type The datatype for attribute data - GLES20.GL_FLOAT
     * 
     * @throws IllegalArgumentException if type is not GLES20.GL_FLOAT
     */
    public void buildMesh(Mesh mesh, Texture2D texture, int charCount, float[] charSize, float[] translate,
            int type) {

        int vertexStride = DEFAULT_COMPONENTS;
        float[] quadPositions = MeshBuilder.buildQuadPositionsIndexed(charSize, translate, vertexStride);
        MeshBuilder.buildQuadMeshIndexed(mesh, this, charCount, quadPositions, ATTRIBUTES_PER_VERTEX);

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
