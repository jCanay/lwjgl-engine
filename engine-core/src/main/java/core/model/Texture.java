package core.model;

import lombok.Getter;
import lombok.Setter;
import util.ResourceLoader;

@Getter
@Setter
public class Texture {
    public static final Texture defaultDiffuse;
    public static final Texture defaultSpecular;

    static {
        defaultDiffuse = new Texture(ResourceLoader.loadTexture("texture/default_diffuse.png"), TextureType.DIFFUSE);
        defaultSpecular = new Texture(ResourceLoader.loadTexture("texture/default_specular.png"), TextureType.SPECULAR);
    }

    private int id;
    private TextureType type;
    private String path;

    public Texture(int id, TextureType type) {
        this.id = id;
        this.type = type;
    }

    public Texture() {

    }
}
