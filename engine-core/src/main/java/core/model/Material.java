package core.model;

import core.renderer.Shader;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

import java.util.Arrays;
import java.util.Objects;

@Getter
@Setter
public class Material {
    private static final int MAX_TEX_DIFFUSE = 4;
    private static final int MAX_TEX_SPECULAR = 2;

    private final Vector3f ambient;
    private final Vector3f diffuse;
    private final Vector3f specular;

    private float shininess;

    private final Texture[] texture_diffuse = new Texture[MAX_TEX_DIFFUSE];
    private final Texture[] texture_specular = new Texture[MAX_TEX_SPECULAR];

    public Material() {
        ambient = new Vector3f(0.15f);
        diffuse = new Vector3f(1.0f);
        specular = new Vector3f(0.5f);
        shininess = 32;
    }

    private boolean containsTexture(Texture[] textures) {
        return Arrays.stream(textures).anyMatch(Objects::nonNull);
    }

    // Implementar esta función en Mesh.render()
    public void render(Shader shader) {
        shader.bind();

        shader.setVec3f("material.ambient", ambient);
        shader.setVec3f("material.diffuse", diffuse);
        shader.setVec3f("material.specular", specular);
        shader.setFloat("material.shininess", shininess);

        int textureCount = texture_diffuse.length + texture_specular.length;
        for (int i = 0; i < texture_diffuse.length; i++) {
            shader.setInt(String.format("material.texture_diffuse[%d]", i), i);
        }
        for (int i = texture_diffuse.length; i < textureCount; i++) {
            shader.setInt(String.format("material.texture_specular[%d]", i - texture_diffuse.length), i);
        }
    }
}
