package editor;

import core.Application;
import core.Engine;
import core.geometry.PrimitiveFactory;
import core.model.Mesh;
import core.model.Model;
import core.renderer.Camera;
import core.renderer.Renderer;
import core.renderer.Shader;
import input.Input;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Editor implements Application {
    float angle = 0;

    float[] trianglePos = {
            0.0f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f
    };
    float[] rectanglePos = {
            // positions // colors // texture coords
            0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, // top right
            0.5f, -0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f, // bottom right
            -0.5f, -0.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, // bottom left
            -0.5f, 0.5f, 0.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f // top left
    };
    float[] cubeVertices = {
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 1.0f, 1.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, 0.0f, -1.0f, 0.0f, 0.0f,

            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f,

            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, -0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            -0.5f, -0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, -1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,

            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,
            0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 1.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, -0.5f, 0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 0.0f,
            -0.5f, -0.5f, -0.5f, 0.0f, -1.0f, 0.0f, 0.0f, 1.0f,

            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f,
            0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 1.0f, 0.0f,
            -0.5f, 0.5f, 0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
            -0.5f, 0.5f, -0.5f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f
    };

    Vector3f[] cubePositions = {
            new Vector3f(0.0f, 0.0f, 0.0f),
            new Vector3f(2.0f, 5.0f, -15.0f),
            new Vector3f(-1.5f, -2.2f, -2.5f),
            new Vector3f(-3.8f, -2.0f, -12.3f),
            new Vector3f(2.4f, -0.4f, -3.5f),
            new Vector3f(-1.7f, 3.0f, -7.5f),
            new Vector3f(1.3f, -2.0f, -2.5f),
            new Vector3f(1.5f, 2.0f, -2.5f),
            new Vector3f(1.5f, 0.2f, -1.5f),
            new Vector3f(-1.3f, 1.0f, -1.5f)
    };

    float[] texCoords = {
            0.0f, 0.0f,
            1.0f, 0.0f,
            0.5f, 1.0f,
    };
    int[] indices = {
            0, 1, 3, // first triangle
            1, 2, 3 // second triangle
    };

    private Shader shader3d;
    private Shader lightShader;
    private Camera camera;
    private Model model3d;
    private Mesh cube;
    float fov = 45;

    @Override
    public void init() {
        camera = Camera.create();
        shader3d = new Shader("shader", "shader");
        lightShader = new Shader("default", "light");
        model3d = new Model("model/backpack/backpack.obj");
        cube = PrimitiveFactory.createCube();
    }

    @Override
    public void fixedUpdate() {

    }

    @Override
    public void update() {
        camera.update();

        // Camera controls
        if (Input.isKey(GLFW.GLFW_KEY_W)) camera.move(Camera.Direction.FORWARD);
        if (Input.isKey(GLFW.GLFW_KEY_S)) camera.move(Camera.Direction.BACKWARD);
        if (Input.isKey(GLFW.GLFW_KEY_A)) camera.move(Camera.Direction.LEFT);
        if (Input.isKey(GLFW.GLFW_KEY_D)) camera.move(Camera.Direction.RIGHT);
        if (Input.isKey(GLFW.GLFW_KEY_SPACE)) camera.move(Camera.Direction.UP);
        if (Input.isKey(GLFW.GLFW_KEY_LEFT_CONTROL)) camera.move(Camera.Direction.DOWN);

        if (Input.yScrollOffset < 0 && camera.getFov() <= 120.0f) camera.setFov(camera.getFov() * 1.1f);
        if (Input.yScrollOffset > 0 && camera.getFov() >= 10.0f) camera.setFov(camera.getFov() / 1.1f);

        camera.setRunning(Input.isKey(GLFW.GLFW_KEY_LEFT_SHIFT));
    }

    @Override
    public void render(Renderer renderer) {
        shader3d.bind();

        // Transform
        shader3d.setMatrix4f("projection", camera.getProjectionMatrix());
        shader3d.setMatrix4f("view", camera.getViewMatrix());

        // Lighting
        shader3d.setVec3f("viewPos", camera.getPosition());

        // Directional Light
        shader3d.setMatrix4f("model", new Matrix4f());
        shader3d.setVec3f("dirLight.direction", new Vector3f(0.0f, -1.0f, 0.0f));
        shader3d.setVec3f("dirLight.ambient", new Vector3f(0.2f, 0.2f, 0.2f));
        shader3d.setVec3f("dirLight.diffuse", new Vector3f(0.35f, 0.35f, 0.35f));
        shader3d.setVec3f("dirLight.specular", new Vector3f(0.15f, 0.15f, 0.15f));

        // Point Light
        lightShader.bind();
        lightShader.setMatrix4f("projection", camera.getProjectionMatrix());
        lightShader.setMatrix4f("view", camera.getViewMatrix());
        lightShader.setMatrix4f("model", new Matrix4f().translate(new Vector3f(2.0f, 2.0f, 2.0f)).scale(0.33f));
        lightShader.setVec3f("color", new Vector3f(1.0f, 0.75f, 0.0f));
        cube.draw(lightShader);
        shader3d.bind();

        shader3d.setVec3f("pointLight.position", new Vector3f(2.0f, 2.0f, 2.0f));
        shader3d.setVec3f("pointLight.ambient", new Vector3f(0.2f, 0.75f * 0.2f, 0.0f));
        shader3d.setVec3f("pointLight.diffuse", new Vector3f(1.0f, 0.75f, 0.0f));
        shader3d.setVec3f("pointLight.specular", new Vector3f(1.0f, 0.75f, 0.0f));
        shader3d.setFloat("pointLight.constant", 1.0f);
        shader3d.setFloat("pointLight.linear", 0.09f);
        shader3d.setFloat("pointLight.quadratic", 0.032f);

        // Model rendering
        shader3d.setMatrix4f("model", new Matrix4f().translate(0.0f, -2.0f, 0.0f).scale(25.0f, 0.01f, 25.0f));
        cube.draw(shader3d);

        shader3d.setMatrix4f("model", new Matrix4f());
        model3d.draw(shader3d);

        shader3d.unbind();
        lightShader.unbind();
    }

    //    public void render(Renderer renderer) {
//        diffuse.bind(0);
//        specular.bind(1);

    /// /        emission.bind(2);
//
//        if (Input.yScrollOffset < 0 && fov <= 120.0f) fov *= 1.1f;
//        if (Input.yScrollOffset > 0 && fov >= 10.0f) fov /= 1.1f;
//
//        Matrix4f projection = new Matrix4f()
//                .perspective(Math.toRadians(fov), 800.0f / 600.0f, 0.1f, 100.0f);
//
//        // Giro alrededor de 0,0,0
//        float radius = 2.0f;
//        float lightX = (float) Math.sin(Time.time * 0) * radius;
//        float lightZ = (float) Math.cos(Time.time * 0) * radius;
//
//        Matrix4f view = camera.getViewMatrix();
//        Vector3f lightPos = new Vector3f(lightX, 1.0f, lightZ);
//
//        shader.bind();
//        // Lighting
//        shader.setVec3f("lightPos", lightPos);
//        shader.setVec3f("viewPos", camera.getPosition());
//        shader.setVec3f("objectColor", new Vector3f(1.0f, 0.5f, 0.31f));
//        shader.setVec3f("lightColor", new Vector3f(1.0f, 1.0f, 1.0f));
//        // Transform
//        shader.setMatrix4f("model", new Matrix4f());
//        shader.setMatrix4f("projection", projection);
//        shader.setMatrix4f("view", view);
//        // Material
//        shader.setInt("material.diffuse", 0);
//        shader.setInt("material.specular", 1);
//        shader.setInt("material.emission", 2);
//        shader.setFloat("material.shininess", 32.0f);
//        // Light material
//        shader.setVec3f("light.ambient", new Vector3f(0.2f, 0.2f, 0.2f));
//        shader.setVec3f("light.diffuse", new Vector3f(0.5f, 0.5f, 0.5f));
//        shader.setVec3f("light.specular", new Vector3f(1.0f, 1.0f, 1.0f));
//        shader.setVec3f("light.direction", new Vector3f(-0.2f, -1.0f, -0.3f));
//        // Point light attenuation
//        shader.setFloat("light.constant", 1.0f);
//        shader.setFloat("light.linear", 0.09f);
//        shader.setFloat("light.quadratic", 0.032f);
//        // Spotlight
//        shader.setVec3f("light.position", camera.getPosition());
//        shader.setVec3f("light.direction", camera.getFront());
//        shader.setFloat("light.cutoff", Math.cos(Math.toRadians(12.25f)));
//        shader.setFloat("light.outerCutoff", Math.cos(Math.toRadians(15.0f)));
//
//        // ------------------------------------
//        // Directional Light
//        shader.setVec3f("dirLight.direction", new Vector3f(0.0f, -1.0f, 0.0f));
//        shader.setVec3f("dirLight.ambient", new Vector3f(0.2f, 0.2f, 0.2f));
//        shader.setVec3f("dirLight.diffuse", new Vector3f(0.5f, 0.5f, 0.5f));
//        shader.setVec3f("dirLight.specular", new Vector3f(1.0f, 1.0f, 1.0f));
//
//        // Point Lights
//        Vector3f[] pointLightPositions = {
//                new Vector3f(0.7f, 0.2f, 2.0f),
//                new Vector3f(2.3f, -3.3f, -4.0f),
//                new Vector3f(-4.0f, 2.0f, -12.0f),
//                new Vector3f(0.0f, 0.0f, -3.0f)
//        };
//        for (int i = 0; i < pointLightPositions.length; i++) {
//            shader.setVec3f(String.format("pointLights[%d].position", i), pointLightPositions[i]);
//
//            shader.setFloat(String.format("pointLights[%d].constant", i), 1.0f);
//            shader.setFloat(String.format("pointLights[%d].linear", i), 0.09f);
//            shader.setFloat(String.format("pointLights[%d].quadratic", i), 0.032f);
//
//            shader.setVec3f(String.format("pointLights[%d].ambient", i), new Vector3f(0.2f, 0.2f, 0.2f));
//            shader.setVec3f(String.format("pointLights[%d].diffuse", i), new Vector3f(Math.sin(0.5f * i * 2 + 30), Math.sin(0.5f * i * 4 + 30), Math.sin(0.5f * i * 8 + 45)));
//            shader.setVec3f(String.format("pointLights[%d].specular", i), new Vector3f(1.0f, 1.0f, 1.0f));
//        }
//
//        // Spotlight (FlashLight)
//        shader.setVec3f("spotLight.position", camera.getPosition());
//        shader.setVec3f("spotLight.direction", camera.getFront());
//
//        shader.setFloat("spotLight.constant", 1.0f);
//        shader.setFloat("spotLight.linear", 0.09f);
//        shader.setFloat("spotLight.quadratic", 0.032f);
//
//        shader.setFloat("spotLight.cutoff", Math.cos(Math.toRadians(12.25f)));
//        shader.setFloat("spotLight.outerCutoff", Math.cos(Math.toRadians(15.0f)));
//
//        shader.setVec3f("spotLight.ambient", new Vector3f(0.2f, 0.2f, 0.2f));
//        shader.setVec3f("spotLight.diffuse", new Vector3f(1.0f, 1.0f, 1.0f));
//        shader.setVec3f("spotLight.specular", new Vector3f(1.0f, 1.0f, 1.0f));
//        // ------------------------------------
//
//    renderer.drawMesh(mesh);
//    Matrix4f model;for (int i = 0; i < cubePositions.length; i++) {
//        model = new Matrix4f();
//        model.translate(cubePositions[i]);
//        float angle = 20.0f * i + 20.0f;
//        model.rotate(i % 3 == 0 ? Math.toRadians(angle) * (float) Time.time : Math.toRadians(angle),
//                new Vector3f(1.0f, 0.3f, 0.5f).normalize()
//        );
//        shader.setMatrix4f("model", model);
//
//        renderer.drawMesh(myMesh);
//    }
//        renderer
//
//        Matrix4f model3d = new Matrix4f();
//        shader.setMatrix4f("model", model3d);
//        this.model.draw(shader);
//
//
//        lightShader.bind();
//        // Transform
//        lightShader.setMatrix4f("projection", projection);
//        lightShader.setMatrix4f("view", view);
//        for (int i = 0; i < pointLightPositions.length; i++) {
//            lightShader.setMatrix4f("model", new Matrix4f()
//                    .translate(pointLightPositions[i])
//                    .rotate(Math.toRadians(radius), pointLightPositions[i])
//                    .scale(0.2f)
//            );
//            lightShader.setVec3f("color", new Vector3f(Math.sin(0.5f * i * 2 + 30), Math.sin(0.5f * i * 4 + 30), Math.sin(0.5f * i * 8 + 45)));
//
//            renderer.drawMesh(light);
//        }
//
//        // Shaders unbind
//        shader.unbind();
//        lightShader.unbind();
//
//        // Textures unbind
//        diffuse.unbind();
//        specular.unbind();
//
//        // emission.unbind();
//    }
    @Override
    public void cleanup() {
        shader3d.cleanup();
    }

    void main() {
        new Engine(this).run();
    }
}