package core.lighting;

import core.renderer.Shader;
import lombok.Getter;

@Getter
public class LightManager {
    private final DirectionalLight dirLight = new DirectionalLight();
    private final PointLight[] pointLights = new PointLight[PointLight.MAX_POINT_LIGHTS];

    public LightManager() {

    }

    public void render(Shader shader) {
        dirLight.attach(shader);

        for (int i = 0; i < pointLights.length; i++) {
            if (pointLights[i] == null) {
                shader.setBoolean(String.format("%s[%d].%s", LightSourceType.POINT.getArrayName(), i, "enabled"), false);
                continue;
            }
            pointLights[i].attachArray(shader, i);
        }
    }
}
