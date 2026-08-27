package core.model;

import core.renderer.Shader;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;
import util.ResourceLoader;

@Getter
@Setter
public class Material {
    private final Vector3f ambient;
    private final Vector3f diffuse;
    private final Vector3f specular;
    private float shininess;

    private final Texture diffuseTex = new Texture();
    private final Texture specularTex = new Texture();

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


    }
}
