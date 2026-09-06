package core.lighting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LightSourceType {
    DIRECTIONAL("dirLight", ""),
    POINT("pointLight", "pointLights"),
    SPOTLIGHT("spotLight", "spotLights");

    private final String name;
    private final String arrayName;
}
