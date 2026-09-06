package core.lighting;

import core.renderer.Shader;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

@Getter
@Setter
public class DirectionalLight extends LightSource {
    private final Vector3f direction;

    public DirectionalLight() {
        this.type = LightSourceType.DIRECTIONAL;
        this.color = new Vector3f(1.0f);
        this.direction = new Vector3f(0.0f, -1.0f, 0.0f);
    }

    public DirectionalLight(Vector3f color, Vector3f direction) {
        this.type = LightSourceType.DIRECTIONAL;
        this.color = color;
        this.direction = direction;
    }

    @Override
    public void attach(Shader shader) {
        super.attach(shader);

        shader.setVec3f(property("direction"), direction);
    }
}
