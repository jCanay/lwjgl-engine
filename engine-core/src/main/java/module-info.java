module engine.core {
    requires static lombok;

    requires org.lwjgl;
    requires org.lwjgl.glfw;
    requires org.lwjgl.opengl;
    requires org.joml;
    requires java.logging;
    requires org.lwjgl.stb;
    requires org.lwjgl.assimp;
    requires imgui.binding;
    requires imgui.lwjgl3;

    exports core;
    exports input;
    exports core.renderer;
    exports core.model;
    exports core.geometry;
}