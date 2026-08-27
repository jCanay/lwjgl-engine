package core.model;

import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;
import org.joml.Vector3f;

@Getter
@Setter
public class Vertex {
    private Vector3f position;
    private Vector3f normal;
    private Vector2f texCoords;

    public Vertex() {
        position = new Vector3f(0.0f, 0.0f, 0.0f);
        normal = new Vector3f(0.0f, 0.0f, 0.0f);
        texCoords = new Vector2f(0.0f, 0.0f);
    }
}