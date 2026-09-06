package core.lighting;

import core.renderer.Shader;
import lombok.Getter;
import org.joml.Vector3f;

@Getter
public class PointLight extends LightSource<PointLight> {
    public static final int MAX_POINT_LIGHTS = 10;

    private final Vector3f position;
    private float radius;
    private float constant;
    private float linear;
    private float quadratic;

    public PointLight() {
        super.type = LightSourceType.POINT;
        super.color = new Vector3f(1.0f);
        this.position = new Vector3f();
        setRadius(10.0f);
    }

    public PointLight(Vector3f color, Vector3f position, float radius) {
        super.type = LightSourceType.POINT;
        super.color = new Vector3f(1.0f);
        this.position = new Vector3f();
        setRadius(radius);
    }

    @Override
    public void attach(Shader shader) {
        super.attach(shader);

        shader.setVec3f(property("position"), position);
        shader.setFloat(property("constant"), constant);
        shader.setFloat(property("linear"), linear);
        shader.setFloat(property("quadratic"), quadratic);
    }

    @Override
    public void attachArray(Shader shader, int index) {
        super.attachArray(shader, index);

        shader.setVec3f(propertyArray(index, "position"), position);
        shader.setFloat(propertyArray(index, "constant"), constant);
        shader.setFloat(propertyArray(index, "linear"), linear);
        shader.setFloat(propertyArray(index, "quadratic"), quadratic);
    }

    public void setRadius(float radius) {
        this.radius = radius;

        this.constant = 1.0f;
        this.linear = 4.7f / radius;
        this.quadratic = 75.0f / (radius * radius);
    }

    public PointLight setPosition(float x, float y, float z) {
        position.set(x, y, z);
        return this;
    }

    public PointLight setPosition(float d) {
        position.set(d);
        return this;
    }
}
