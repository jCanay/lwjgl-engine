package core.model;

import core.renderer.Shader;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import util.ResourceLoader;

@Getter
@Setter
public class Material {
    private static final int MAX_TEX_DIFFUSE = 4;
    private static final int MAX_TEX_SPECULAR = 4;

    private final Vector3f ambient;
    private final Vector3f diffuse;
    private final Vector3f specular;

    private float shininess;

    // Quitar ArrayList
    private final Texture diffuseTex = new Texture();
    private final Texture specularTex = new Texture();
    private final Texture[] texture_diffuse = new Texture[MAX_TEX_DIFFUSE];
    private final Texture[] texture_specular = new Texture[MAX_TEX_SPECULAR];

    public Material() {
        ambient = new Vector3f(0.15f);
        diffuse = new Vector3f(1.0f);
        specular = new Vector3f(0.5f);
        shininess = 32;

        // Process textures
        diffuseTex.setId(ResourceLoader.loadTexture("texture/default_diffuse.png"));
        diffuseTex.setType("texture_diffuse");

        specularTex.setId(ResourceLoader.loadTexture("texture/default_specular.jpg"));
        specularTex.setType("texture_specular");
    }

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
            shader.setInt(String.format("material.texture_specular[%d]", i), i);
        }
    }
}
