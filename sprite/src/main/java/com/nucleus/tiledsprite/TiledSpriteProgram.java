package com.nucleus.tiledsprite;

import com.nucleus.geometry.ElementBuffer;
import com.nucleus.geometry.ElementBuffer.Mode;
import com.nucleus.geometry.ElementBuffer.Type;
import com.nucleus.geometry.ElementBuilder;
import com.nucleus.geometry.Material;
import com.nucleus.geometry.Mesh;
import com.nucleus.geometry.VertexBuffer;
import com.nucleus.opengl.GLES20Wrapper;
import com.nucleus.opengl.GLES20Wrapper.GLES20;
import com.nucleus.opengl.GLException;
import com.nucleus.opengl.GLUtils;
import com.nucleus.shader.ShaderProgram;
import com.nucleus.shader.ShaderVariable;
import com.nucleus.shader.ShaderVariable.VariableType;

/**
 * This class defined the mappings for the Fractal vertex and fragment shaders.
 * 
 * @author Richard Sahlin
 *
 */
public class TiledSpriteProgram extends ShaderProgram {

    /**
     * Number of vertices per sprite - this is for a quad that is created using element buffer.
     */
    public final static int VERTICES_PER_SPRITE = 4;
    /**
     * Draw using an index list each quad is made up of 6 indices (2 triangles)
     */
    public final static int INDICES_PER_SPRITE = 6;

    /**
     * Default number of components (x,y,z)
     */
    public final static int DEFAULT_COMPONENTS = 3;

    private final static String ILLEGAL_DATATYPE_STR = "Illegal datatype: ";

    /**
     * Index into uniform sprite data data where the texture fraction s (width) is
     */
    public final static int UNIFORM_TEX_FRACTION_S_INDEX = 0;
    /**
     * Index into uniform sprite data data where the texture fraction t (height) is
     */
    public final static int UNIFORM_TEX_FRACTION_T_INDEX = 1;

    /**
     * Index into uniform sprite data where 1 / texture fraction w - this is used to calculate y pos from frame index
     */
    public final static int UNIFORM_TEX_ONEBY_S_INDEX = 2;

    /**
     * Number of float data per vertex
     */
    public final static int PER_VERTEX_DATA = 8;

    public final static int ATTRIBUTE_1_OFFSET = 0;
    public final static int ATTRIBUTE_2_OFFSET = 4;

    /**
     * Index into aTileSprite for x position
     */
    public final static int ATTRIBUTE_SPRITE_X_INDEX = 0;
    /**
     * Index into aTileSprite for y position
     */
    public final static int ATTRIBUTE_SPRITE_Y_INDEX = 1;
    /**
     * Index into aTileSprite texture u coordinate - this is used to calculate texture coordinate with frame.
     */
    public final static int ATTRIBUTE_SPRITE_U_INDEX = 2;
    /**
     * Index into aTileSprite texture v coordinate - this is used to calculate texture coordinate with frame.
     */
    public final static int ATTRIBUTE_SPRITE_V_INDEX = 3;
    /**
     * Index into aTileSprite frame number, this is the sprite frame number to use.
     */
    public final static int ATTRIBUTE_SPRITE_FRAME_INDEX = 4;
    /**
     * Index into aTileSprite z axis rotation
     */
    public final static int ATTRIBUTE_SPRITE_ROTATION_INDEX = 5;

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

    private final static String VERTEX_SHADER_NAME = "assets/vertexshader.essl";
    private final static String FRAGMENT_SHADER_NAME = "assets/fragmentshader.essl";

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

        VertexBuffer buffer = mesh.getVerticeBuffer(0);
        ShaderVariable attrib = getShaderVariable(VARIABLES.aPosition.index);
        gles.glEnableVertexAttribArray(attrib.getLocation());
        GLUtils.handleError(gles, "glEnableVertexAttribArray1 ");
        gles.glVertexAttribPointer(attrib.getLocation(), buffer.getComponentCount(), buffer.getDataType(), false,
                buffer.getByteStride(), buffer.getBuffer().position(0));
        GLUtils.handleError(gles, "glVertexAttribPointer1 ");
        ShaderVariable attrib2 = getShaderVariable(VARIABLES.aTileSprite.index);
        gles.glEnableVertexAttribArray(attrib2.getLocation());
        GLUtils.handleError(gles, "glEnableVertexAttribArray2 ");
        VertexBuffer buffer2 = mesh.getVerticeBuffer(1);
        gles.glVertexAttribPointer(attrib2.getLocation(), buffer2.getComponentCount(), buffer2.getDataType(), false,
                buffer2.getByteStride(), buffer2.getBuffer().position(ATTRIBUTE_1_OFFSET));
        ShaderVariable attrib3 = getShaderVariable(VARIABLES.aTileSprite2.index);
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
     * @param spriteCount Number of sprites to build, this is NOT the vertex count.
     * @param width The width of a sprite, the sprite will be centered in the middle.
     * @param height The height of a sprite, the sprite will be centered in the middle.
     * @param z The z position for each vertice.
     * @param type The datatype for attribute data - GLES20.GL_FLOAT
     * @param Texture U fraction for each sprite frame, if sheet is 5 frames wide this is 1/5
     * @param Texture V fraction for each sprite frame, if sheet is 3 frames high this is 1/3
     * 
     * @return The mesh that can be rendered.
     * @throws IllegalArgumentException if type is not GLES20.GL_FLOAT
     */
    public Mesh buildTileSpriteMesh(int spriteCount, float width, float height, float z, int type, float fractionU,
            float fractionV) {

        int vertexStride = DEFAULT_COMPONENTS;
        float[] quadPositions = new float[vertexStride * VERTICES_PER_SPRITE];

        float halfWidth = width / 2;
        float halfHeight = height / 2;
        com.nucleus.geometry.MeshBuilder.setPosition(-halfWidth, -halfHeight, z, quadPositions, 0);
        com.nucleus.geometry.MeshBuilder.setPosition(halfWidth, -halfHeight, z, quadPositions,
                vertexStride);
        com.nucleus.geometry.MeshBuilder.setPosition(halfWidth, halfHeight, z, quadPositions,
                vertexStride * 2);
        com.nucleus.geometry.MeshBuilder.setPosition(-halfWidth, halfHeight, z, quadPositions,
                vertexStride * 3);
        Mesh mesh = buildTileSpriteMesh(spriteCount, quadPositions, type);
        float[] uniformVectors = mesh.getUniformVectors();
        uniformVectors[UNIFORM_TEX_FRACTION_S_INDEX] = fractionU;
        uniformVectors[UNIFORM_TEX_FRACTION_T_INDEX] = fractionV;
        uniformVectors[UNIFORM_TEX_ONEBY_S_INDEX] = (int) (1 / fractionU);
        return mesh;
    }

    /**
     * Builds a mesh with data that can be rendered using a tiled sprite renderer, this will draw a number of
     * sprites using one drawcall.
     * Vertex buffer will have storage for XYZ + UV.
     * 
     * @param spriteCount Number of sprites to build, this is NOT the vertex count.
     * @param quadPositions Array with x,y,z - this is set for each tile. Must contain data for 4 vertices.
     * @param type The datatype for attribute data - GLES20.GL_FLOAT
     * 
     * @return The mesh that can be rendered.
     * @throws IllegalArgumentException if type is not GLES20.GL_FLOAT
     */
    public Mesh buildTileSpriteMesh(int spriteCount, float[] quadPositions, int type) {
        if (type != GLES20.GL_FLOAT) {
            throw new IllegalArgumentException(ILLEGAL_DATATYPE_STR + type);
        }
        VertexBuffer[] attributes = new VertexBuffer[2];
        /**
         * Create the buffer for vertex position and UV
         */
        attributes[0] = new VertexBuffer(spriteCount * VERTICES_PER_SPRITE, DEFAULT_COMPONENTS, DEFAULT_COMPONENTS,
                type);
        attributes[1] = new VertexBuffer(spriteCount * VERTICES_PER_SPRITE, 4, PER_VERTEX_DATA, GLES20.GL_FLOAT);
        ElementBuffer indices = new ElementBuffer(Mode.TRIANGLES, INDICES_PER_SPRITE * spriteCount, Type.SHORT);
        ElementBuilder.buildQuadBuffer(indices, indices.getCount() / INDICES_PER_SPRITE, 0);

        float[] vertices = new float[spriteCount * VERTICES_PER_SPRITE * DEFAULT_COMPONENTS];
        int destPos = 0;
        for (int i = 0; i < spriteCount; i++) {
            System.arraycopy(quadPositions, 0, vertices, destPos, quadPositions.length);
            destPos += quadPositions.length;
        }

        attributes[0].setPosition(vertices, 0, 0, spriteCount * VERTICES_PER_SPRITE);
        Material material = new Material(this);
        Mesh mesh = new Mesh(indices, attributes, material, null);
        // TODO: Move to generic method, pass Variable to use as vector/matrix storage
        ShaderVariable uVectors = getShaderVariable(VARIABLES.uSpriteData.index);
        float[] uniformVectors = new float[uVectors.getSizeInFloats()];
        mesh.setUniformVectors(uniformVectors);
        ShaderVariable uMatrices = getShaderVariable(VARIABLES.uMVPMatrix.index);
        float[] uniformMatrices = new float[uMatrices.getSizeInFloats()];
        mesh.setUniformMatrices(uniformMatrices);
        return mesh;

    }

    @Override
    public void createProgram(GLES20Wrapper gles) {
        createProgram(gles, VERTEX_SHADER_NAME, FRAGMENT_SHADER_NAME);
    }

}
