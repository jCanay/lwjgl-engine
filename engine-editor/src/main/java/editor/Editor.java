package editor;

import core.Application;
import core.Engine;
import core.geometry.PrimitiveFactory;
import core.lighting.DirectionalLight;
import core.lighting.LightManager;
import core.lighting.PointLight;
import core.model.Material;
import core.model.Mesh;
import core.model.Model;
import core.renderer.Camera;
import core.renderer.DebugGui;
import core.renderer.Renderer;
import core.renderer.Shader;
import input.Input;
import org.joml.Math;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class Editor implements Application {
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

    private Shader shader3d;
    private Shader stencilBorder;
    private Shader lightShader;
    private Camera camera;
    private Model backpack;
    private Mesh cube;
    private Mesh square;
    private Material material;
    private DirectionalLight directionalLight;
    private PointLight pointLight;
    private LightManager lightManager;

    @Override
    public void init() {
        camera = Camera.create();
        shader3d = new Shader("shader", "shader");
        lightShader = new Shader("default", "light");
        stencilBorder = new Shader("shader", "border");

        backpack = new Model("model/backpack/backpack.obj");
        material = new Material();
        cube = PrimitiveFactory.createCube(material);
        square = PrimitiveFactory.createSquare(material);

        lightManager = new LightManager();
        lightManager.getPointLights()[0] = new PointLight().setPosition(2.5f);
        lightManager.getPointLights()[0].setEnabled(true);
        lightManager.getPointLights()[1] = new PointLight().setPosition(-2.5f, 0.0f, -2.5f).setColor(2.0f);
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
//        GL30.glStencilOp(GL30.GL_KEEP, GL30.GL_KEEP, GL30.GL_REPLACE);
//        GL30.glStencilMask(0x00);
        shader3d.bind();

        // Transform
        shader3d.setMatrix4f("projection", camera.getProjectionMatrix());
        shader3d.setMatrix4f("view", camera.getViewMatrix());
        shader3d.setVec3f("viewPos", camera.getPosition());

        // Lighting ----------------------------------------------
        // Directional Light
        lightManager.getDirLight().getColor().set(DebugGui.dirLightColor[0], DebugGui.dirLightColor[1], DebugGui.dirLightColor[2]);
        lightManager.getDirLight().setIntensity(DebugGui.dirLightIntensity[0]);

        // Point Light
        lightManager.getPointLights()[0].getColor().set(DebugGui.pointLightColor[0], DebugGui.pointLightColor[1], DebugGui.pointLightColor[2]);
        lightManager.getPointLights()[0].setIntensity(DebugGui.pointLightIntensity[0]);
        lightManager.getPointLights()[0].setRadius(DebugGui.pointLightRadius[0]);

        lightManager.render(shader3d);

        // Model rendering

        shader3d.setMatrix4f("model", new Matrix4f().translate(0.0f, -0.5f, 0.0f).rotateX(Math.toRadians(90.0f)).scale(20.0f, 20.0f, 1.0f));
        square.render(shader3d);

//        GL30.glStencilFunc(GL30.GL_ALWAYS, 1, 0xFF);
//        GL30.glStencilMask(0xFF);

        shader3d.setMatrix4f("model", new Matrix4f());
        cube.render(shader3d);

//        GL30.glStencilFunc(GL30.GL_NOTEQUAL, 1, 0xFF);
//        GL30.glStencilMask(0x00);
//        GL30.glDisable(GL30.GL_DEPTH_TEST);

//        stencilBorder.bind();
//        stencilBorder.setMatrix4f("projection", camera.getProjectionMatrix());
//        stencilBorder.setMatrix4f("view", camera.getViewMatrix());
//        stencilBorder.setVec3f("viewPos", camera.getPosition());

//        shader3d.setMatrix4f("model", new Matrix4f().scale(1.025f));
//        cube.render(shader3d);
//        GL30.glStencilMask(0xFF);
//        GL30.glStencilFunc(GL30.GL_ALWAYS, 1, 0xFF);
//        GL30.glEnable(GL30.GL_DEPTH_TEST);

        shader3d.unbind();
    }

    @Override
    public void cleanup() {
        shader3d.cleanup();
    }

    void main() {
        new Engine(this).run();
    }
}