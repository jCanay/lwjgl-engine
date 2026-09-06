package core.lighting;

import core.renderer.Shader;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector3f;

@Getter
public abstract class LightSource<T extends LightSource<T>> {
    protected LightSourceType type;
    protected Vector3f color;
    @Setter
    protected float intensity = 1.0f;
    @Setter
    protected boolean enabled = true;

    private final Vector3f tempColor = new Vector3f();

    public void attach(Shader shader) {
        color.mul(intensity * 0.25f, tempColor);
        shader.setVec3f(property("ambient"), tempColor);

        color.mul(intensity * 0.75f, tempColor);
        shader.setVec3f(property("diffuse"), tempColor);

        color.mul(intensity, tempColor);
        shader.setVec3f(property("specular"), tempColor);

        shader.setBoolean(property("enabled"), enabled);
    }

    public void attachArray(Shader shader, int index) {
        color.mul(intensity * 0.25f, tempColor);
        shader.setVec3f(propertyArray(index, "ambient"), tempColor);

        color.mul(intensity * 0.75f, tempColor);
        shader.setVec3f(propertyArray(index, "diffuse"), tempColor);

        color.mul(intensity, tempColor);
        shader.setVec3f(propertyArray(index, "specular"), tempColor);

        shader.setBoolean(propertyArray(index, "enabled"), enabled);
    }

    protected String property(String property) {
        return String.format("%s.%s", type.getName(), property);
    }

    protected String propertyArray(int index, String property) {
        return String.format("%s[%d].%s", type.getArrayName(), index, property);
    }

    public T setColor(float x, float y, float z) {
        color.set(x, y, z);
        return (T) this;
    }

    public T setColor(float d) {
        color.set(d);
        return (T) this;
    }
}
