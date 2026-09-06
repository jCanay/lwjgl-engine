package core.renderer;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import util.ResourceLoader;

import static org.lwjgl.opengl.GL30.*;

/**
 * Supports vertex and fragment shaders
 *
 */
public class Shader {
    private final int programId;

    public Shader(String vertexShaderSource, String fragmentShaderSource) {
        String vertexCode = ResourceLoader.loadString(
                "/shader/" + vertexShaderSource + (vertexShaderSource.contains(".vert") ? "" : ".vert"));
        String fragmentCode = ResourceLoader.loadString(
                "/shader/" + fragmentShaderSource + (fragmentShaderSource.contains(".frag") ? "" : ".frag"));

        int vertexShader = compileShader(GL_VERTEX_SHADER, vertexCode);
        int fragmentShader = compileShader(GL_FRAGMENT_SHADER, fragmentCode);

        programId = glCreateProgram();
        glAttachShader(programId, vertexShader);
        glAttachShader(programId, fragmentShader);
        glLinkProgram(programId);

        if (glGetProgrami(programId, GL_LINK_STATUS) == GL_FALSE) {
            throw new RuntimeException("Error linking Shader Program: " + glGetProgramInfoLog(programId));
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void setInt(String name, int value) {
        glUniform1i(glGetUniformLocation(programId, name), value);
    }

    public void setFloat(String name, float value) {
        glUniform1f(glGetUniformLocation(programId, name), value);
    }

    public void setBoolean(String name, boolean value) {
        glUniform1i(glGetUniformLocation(programId, name), value ? 1 : 0);
    }

    public void setVec3f(String name, Vector3f vector3f) {
        glUniform3f(glGetUniformLocation(programId, name), vector3f.x, vector3f.y, vector3f.z);
    }

    public void setMatrix4f(String name, Matrix4f matrix4f) {
        glUniformMatrix4fv(glGetUniformLocation(programId, name), false, matrix4f.get(new float[16]));
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        glDeleteProgram(programId);
    }

    private int compileShader(int type, String code) {
        int shaderId = glCreateShader(type);
        glShaderSource(shaderId, code);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == GL_FALSE) {
            throw new RuntimeException("Error compiling Shader: " + glGetShaderInfoLog(shaderId));
        }
        return shaderId;
    }
}
