module engine.editor {
    requires static lombok;

    requires engine.core;
    requires org.lwjgl.glfw;
    requires org.lwjgl.stb;
    requires org.joml;
    requires org.lwjgl.opengl;

    exports editor;
}