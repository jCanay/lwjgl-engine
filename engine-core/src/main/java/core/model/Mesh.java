package core.model;

import core.renderer.Shader;
import lombok.Getter;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

@Getter
public class Mesh {
    private final List<Vertex> vertices;
    private final List<Integer> indices;
    private final List<Texture> textures;
    private int vaoId;
    private int vboId;
    private int eboId;

    public Mesh() {
        this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    public Mesh(List<Vertex> vertices, List<Integer> indices, List<Texture> textures) {
        this.vertices = vertices;
        this.indices = indices;
        this.textures = textures;
    }

    public void setup() {
        // Crear y enlazar VAO
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Transformar datos a buffer
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.size() * 8); // No usar BufferUtils, usar MemoryUtil (heap) o MemoryStack
        for (Vertex vertex : vertices) {
            vertexBuffer.put(vertex.getPosition().x).put(vertex.getPosition().y).put(vertex.getPosition().z);
            vertexBuffer.put(vertex.getNormal().x).put(vertex.getNormal().y).put(vertex.getNormal().z);
            vertexBuffer.put(vertex.getTexCoords().x).put(vertex.getTexCoords().y);
        }
        vertexBuffer.flip();

        // Subir datos al VBO
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Aplanar la lista de índices a un IntBuffer
        IntBuffer indexBuffer = BufferUtils.createIntBuffer(indices.size());
        for (Integer index : indices) {
            indexBuffer.put(index);
        }
        indexBuffer.flip();

        // Subir datos al EBO
        eboId = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexBuffer, GL_STATIC_DRAW);

        // Configurar atributos de vértices (Punteros)
        int stride = 8 * Float.BYTES; // 3 pos + 3 norm + 2 uv = 8 floats (32 bytes)

        // Posiciones (Offset: 0)
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);

        // Normales (Offset: 3 floats = 12 bytes)
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * Float.BYTES);

        // Coordenadas UV (Offset: 6 floats = 24 bytes)
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, stride, 6 * Float.BYTES);

        // Desenlazar VAO
        glBindVertexArray(0);
    }

    public void setupNoIndex() {
        // Crear y enlazar VAO
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Transformar datos a buffer
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertices.size() * 8); // No usar BufferUtils, usar MemoryUtil (heap) o MemoryStack
        for (Vertex vertex : vertices) {
            vertexBuffer.put(vertex.getPosition().x).put(vertex.getPosition().y).put(vertex.getPosition().z);
            vertexBuffer.put(vertex.getNormal().x).put(vertex.getNormal().y).put(vertex.getNormal().z);
            vertexBuffer.put(vertex.getTexCoords().x).put(vertex.getTexCoords().y);
        }
        vertexBuffer.flip();

        // Subir datos al VBO
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Posiciones (Offset: 0)
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 8 * Float.BYTES, 0);

        // Normales (Offset: 3 floats = 12 bytes)
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 8 * Float.BYTES, 3 * Float.BYTES);

        // Coordenadas UV (Offset: 6 floats = 24 bytes)
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);

        // Desenlazar VAO
        glBindVertexArray(0);
    }

    public void draw(Shader shader) {
        int diffuseNumber = 1;
        int specularNumber = 1;

        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0 + i);

            String name = textures.get(i).getType();
            switch (name) {
                case "texture_diffuse" -> name += diffuseNumber++;
                case "texture_specular" -> name += specularNumber++;
            }

            shader.setInt(name, i);

            glBindTexture(GL_TEXTURE_2D, textures.get(i).getId());
        }
        glActiveTexture(GL_TEXTURE0);

        glBindVertexArray(vaoId);
        if (indices.isEmpty()) {
            glDrawArrays(GL_TRIANGLES, 0, vertices.size());
        } else {
            glDrawElements(GL_TRIANGLES, indices.size(), GL_UNSIGNED_INT, 0);
        }
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDisableVertexAttribArray(0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboId);
        if (!indices.isEmpty()) glDeleteBuffers(eboId);

        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}
