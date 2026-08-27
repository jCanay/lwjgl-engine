package core.renderer;

import core.Time;
import core.Window;
import input.Input;
import lombok.Getter;
import lombok.Setter;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

@Getter
@Setter
public class Camera {
    public enum Direction {
        UP, DOWN, FORWARD, BACKWARD, LEFT, RIGHT
    }

    private static int index = 0;

    private final Vector3f position = new Vector3f(0.0f, 0.0f, 3.0f);
    private final Vector3f front = new Vector3f(0.0f, 0.0f, -1.0f);
    private final Vector3f up = new Vector3f();
    private final Vector3f right = new Vector3f();
    private final Vector3f worldUp = new Vector3f(0.0f, 1.0f, 0.0f);
    private final Vector3f direction = new Vector3f();
    private final Vector3f tempVector = new Vector3f();

    private final Matrix4f projectionMatrix = new Matrix4f();

    private float yaw = -90.0f;
    private float pitch = 0.0f;
    private float speed, runSpeed, sensitivity, fov;
    private boolean running;

    public record CameraConfig(float speed, float runSpeed, float sensitivity, float fov) {
        public static CameraConfig defaultConfig() {
            return new CameraConfig(2.5f, 10.0f, 0.1f, 45.0f);
        }
    }

    private Camera(float speed, float runSpeed, float sensitivity, float fov) {
        this.speed = speed;
        this.runSpeed = runSpeed;
        this.sensitivity = sensitivity;
        this.fov = fov;
    }

    public static Camera create() {
        CameraConfig config = CameraConfig.defaultConfig();
        return new Camera(config.speed, config.runSpeed, config.sensitivity, config.fov);
    }

    public Matrix4f getViewMatrix() {
        return new Matrix4f().lookAt(position, tempVector.set(position).add(front), worldUp);
    }

    public void move(Direction direction) {
        float velocity = (running ? runSpeed : speed) * Time.deltaTime;

        switch (direction) {
            case UP -> position.add(tempVector.set(worldUp).mul(velocity));
            case DOWN -> position.sub(tempVector.set(worldUp).mul(velocity));
            case FORWARD -> position.add(tempVector.set(front).mul(velocity));
            case BACKWARD -> position.sub(tempVector.set(front).mul(velocity));
            case LEFT -> position.sub(tempVector.set(front).cross(worldUp).normalize().mul(velocity));
            case RIGHT -> position.add(tempVector.set(front).cross(worldUp).normalize().mul(velocity));
        }
    }

    public void update() {
        if (Input.isButton(GLFW.GLFW_MOUSE_BUTTON_2)) {
            Input.getFocusedWindow().setCursorMode(GLFW.GLFW_CURSOR_DISABLED);

            if (Input.xOffset > 0 || Input.xOffset < 0 || Input.yOffset > 0 || Input.yOffset < 0) {
                yaw += (float) Input.xOffset * sensitivity;
                pitch += (float) Input.yOffset * sensitivity;

                pitch = Math.min(89.0f, pitch);
                pitch = Math.max(-89.0f, pitch);

                updateCameraVectors();
            }
        } else {
            Window window = Input.getFocusedWindow();
            if (window != null) window.setCursorMode(GLFW.GLFW_CURSOR_NORMAL);
            updateCameraVectors();
        }
    }

    private void updateCameraVectors() {
        front.x = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        front.y = Math.sin(Math.toRadians(pitch));
        front.z = Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
        front.normalize();

        right.set(tempVector.set(front).cross(worldUp).normalize());
        up.set(tempVector.set(right).cross(front).normalize());

        Window window = Input.getFocusedWindow();
        if (window != null) {
            projectionMatrix.set(new Matrix4f().perspective(Math.toRadians(fov), (float) window.getWidth() / window.getHeight(), 0.1f, 100.0f));
        }

    }
}
