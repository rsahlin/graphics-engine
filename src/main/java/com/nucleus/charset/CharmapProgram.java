package com.nucleus.charset;

import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.MeshBuilder;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLException;
import com.nucleus.opengl.GLUtils;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShaderVariable;
import com.nucleus.shader.ShaderVariable.VariableType;

/**
 * This class defines the mappings for the charset vertex and fragment shaders.
 * 
 * @author Richard Sahlin
 *
 */
public class CharmapProgram extends ShaderProgram {

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
    protected final static int ATTRIBUTES_PER_CHAR = CharmapProgram.ATTRIBUTES_PER_VERTEX
            * CharmapProgram.VERTICES_PER_CHAR;

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

    public CharmapProgram() {
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

        VertexBuffer buffer = mesh.getVerticeBuffer(0);
        ShaderVariable attrib = getShaderVariable(VARIABLES.aPosition.index);
        gles.glEnableVertexAttribArray(attrib.getLocation());
        GLUtils.handleError(gles, "glEnableVertexAttribArray1 ");
        gles.glVertexAttribPointer(attrib.getLocation(), buffer.getComponentCount(), buffer.getDataType(), false,
                buffer.getByteStride(), buffer.getBuffer().position(0));
        GLUtils.handleError(gles, "glVertexAttribPointer1 ");
        ShaderVariable attrib2 = getShaderVariable(VARIABLES.aCharset.index);
        gles.glEnableVertexAttribArray(attrib2.getLocation());
        GLUtils.handleError(gles, "glEnableVertexAttribArray2 ");
        VertexBuffer buffer2 = mesh.getVerticeBuffer(1);
        gles.glVertexAttribPointer(attrib2.getLocation(), buffer2.getComponentCount(), buffer2.getDataType(), false,
                buffer2.getByteStride(), buffer2.getBuffer().position(ATTRIBUTE_1_OFFSET));
        ShaderVariable attrib3 = getShaderVariable(VARIABLES.aCharset2.index);
        if (attrib3 != null) {
            gles.glEnableVertexAttribArray(attrib3.getLocation());
            GLUtils.handleError(gles, "glEnableVertexAttribArray3 ");
            VertexBuffer buffer3 = mesh.getVerticeBuffer(1);
            gles.glVertexAttribPointer(attrib3.getLocation(), buffer3.getComponentCount(), buffer3.getDataType(),
                    false,
                    buffer3.getByteStride(), buffer3.getBuffer().position(ATTRIBUTE_2_OFFSET));
        }
        GLUtils.handleError(gles, "glVertexAttribPointer3 ");

    }

    @Override
    public void bindUniforms(GLES20Wrapper gles, float[] modelviewMatrix, Mesh mesh) throws GLException {
        ShaderVariable v = getShaderVariable(VARIABLES.uMVPMatrix.index);
        System.arraycopy(modelviewMatrix, 0, mesh.getUniformMatrices(), 0, v.getSizeInFloats());
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
     * 
     * @param charCount Number of chars to build, this is NOT the vertex count.
     * @param width The width of a char, the char will be centered in the middle.
     * @param height The height of a char, the char will be centered in the middle.
     * @param z The z position for each vertice.
     * @param type The datatype for attribute data - GLES20.GL_FLOAT
     * @param Texture U fraction for each char frame, if sheet is 5 frames wide this is 1/5
     * @param Texture V fraction for each char frame, if sheet is 3 frames high this is 1/3
     * 
     * @return The mesh that can be rendered.
     * @throws IllegalArgumentException if type is not GLES20.GL_FLOAT
     */
    public Mesh buildCharsetMesh(int charCount, float width, float height, float z, int type, float fractionU,
            float fractionV) {

        int vertexStride = DEFAULT_COMPONENTS;
        float[] quadPositions = MeshBuilder.buildQuadPositionsIndexed(width, height, z, 0, 0, vertexStride);
        Mesh mesh = MeshBuilder.buildQuadMeshIndexed(this, charCount, quadPositions, ATTRIBUTES_PER_VERTEX);

        setUniformArrays(mesh, getShaderVariable(VARIABLES.uCharsetData.index),
                getShaderVariable(VARIABLES.uMVPMatrix.index));

        float[] uniformVectors = mesh.getUniformVectors();
        uniformVectors[UNIFORM_TEX_FRACTION_S_INDEX] = fractionU;
        uniformVectors[UNIFORM_TEX_FRACTION_T_INDEX] = fractionV;
        uniformVectors[UNIFORM_TEX_ONEBY_S_INDEX] = (int) (1 / fractionU);

        return mesh;
    }

    @Override
    public void createProgram(GLES20Wrapper gles) {
        createProgram(gles, VERTEX_SHADER_NAME, FRAGMENT_SHADER_NAME);
    }

}
