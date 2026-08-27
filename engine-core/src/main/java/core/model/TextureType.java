package core.model;

import lombok.Getter;

@Getter
public enum TextureType {
    DIFFUSE("texture_diffuse"),
    SPECULAR("texture_specular");

    private final String shaderName;

    TextureType(String shaderName) {
        this.shaderName = shaderName;
    }

}
