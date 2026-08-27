package core.model;

import core.renderer.Shader;
import lombok.Getter;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import org.lwjgl.system.MemoryStack;
import util.ResourceLoader;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.lwjgl.assimp.Assimp.*;

@Getter
public class Model {
    private final List<Mesh> meshes;
    private final List<Texture> texturesLoaded;
    private String directory;

    public Model(String path) {
        meshes = new ArrayList<>();
        texturesLoaded = new ArrayList<>();
        loadModel(path);
    }

    private void loadModel(String path) {
//        Thread thread = new Thread(() -> {
//        });
//        thread.start();

//        String extension = path.substring(path.lastIndexOf(".") + 1);
//        AIScene scene = ResourceLoader.useBuffer(path, buffer -> {
//            return Assimp.aiImportFileFromMemory(buffer, aiProcess_Triangulate | aiProcess_FlipUVs, extension);
//        });
//
//        if (scene == null || (scene.mFlags() & AI_SCENE_FLAGS_INCOMPLETE) == 1 || scene.mRootNode() == null) {
//            throw new RuntimeException(Assimp.aiGetErrorString());
//        }
//
//        directory = path.substring(0, path.lastIndexOf("/"));
//
//        processNode(scene.mRootNode(), scene);

        String safePath = path.startsWith("/") ? path : "/" + path;
        java.net.URL resourceUrl = Model.class.getResource(safePath);

        if (resourceUrl == null) {
            throw new IllegalArgumentException("No se pudo encontrar el recurso en la ruta modular: " + safePath);
        }

        // 2. Extraer la ruta absoluta del sistema de archivos
        String absolutePath = resourceUrl.getPath();

        // Si estás en Windows, retiramos la barra inicial '/' sobrante que rompe las rutas de C/C++
        if (absolutePath.startsWith("/") && System.getProperty("os.name").toLowerCase().contains("win")) {
            absolutePath = absolutePath.substring(1);
        }

        // 3. Forzar a Assimp a leerlo directamente desde el disco duro
        // Al abrirlo desde el disco, Assimp buscará automáticamente el archivo '.mtl' en la misma carpeta física.
        AIScene scene = Assimp.aiImportFile(absolutePath, aiProcess_Triangulate | aiProcess_FlipUVs | aiProcess_JoinIdenticalVertices);

        if (scene == null || (scene.mFlags() & AI_SCENE_FLAGS_INCOMPLETE) == 1 || scene.mRootNode() == null) {
            throw new RuntimeException("Error al cargar el modelo con Assimp: " + Assimp.aiGetErrorString());
        }

        // Guardamos el directorio absoluto para usarlo en la carga de texturas
        directory = absolutePath.substring(0, absolutePath.lastIndexOf("/"));

        processNode(Objects.requireNonNull(scene.mRootNode()), scene);

        // ¡CRÍTICO! Liberar la memoria nativa de la escena al terminar de procesarla
        Assimp.aiReleaseImport(scene);
    }

    private void processNode(AINode node, AIScene scene) {
        PointerBuffer meshesPointers = scene.mMeshes();
        IntBuffer meshesBuffer = node.mMeshes();
        if (meshesPointers != null && meshesPointers.remaining() > 0 &&
                meshesBuffer != null && meshesBuffer.remaining() > 0) {

//            IO.println("Procesando " + node.mNumMeshes() + " mallas en el nodo: " + node.mName().dataString());

            for (int i = 0; i < node.mNumMeshes(); i++) {
                int meshIndex = meshesBuffer.get(i);
                long meshPointer = meshesPointers.get(meshIndex);

                AIMesh mesh = AIMesh.create(meshPointer);
                Mesh processedMesh = processMesh(mesh, scene);
                if (processedMesh != null) {
                    meshes.addLast(processedMesh);
                }
            }
        }

        PointerBuffer childrenPointers = node.mChildren();
        if (childrenPointers != null && childrenPointers.remaining() > 0) {
            int numChildren = node.mNumChildren();

            for (int i = 0; i < numChildren; i++) {
                long nodePointer = childrenPointers.get(i);
                processNode(AINode.create(nodePointer), scene);
            }
        }
    }

    private Mesh processMesh(AIMesh aiMesh, AIScene scene) {
        Mesh mesh = new Mesh();
        AIVector3D.Buffer positionBuffer = aiMesh.mVertices();
        AIVector3D.Buffer normalBuffer = aiMesh.mNormals();
        AIVector3D.Buffer texCoordBuffer = aiMesh.mTextureCoords(0);

        if (positionBuffer.remaining() == 0) return null;

        // Process vertices
        for (int i = 0; i < aiMesh.mNumVertices(); i++) {
            Vertex vertex = new Vertex();

            // Process position
            AIVector3D position = aiMesh.mVertices().get(i);
            vertex.setPosition(new Vector3f(position.x(), position.y(), position.z()));

            // Process normals
            if (normalBuffer != null) {
                AIVector3D normal = normalBuffer.get(i);
                vertex.setNormal(new Vector3f(normal.x(), normal.y(), normal.z()));
            }

            // Process texture coords
            if (texCoordBuffer != null) {
                AIVector3D texCoord = texCoordBuffer.get(i);
                vertex.setTexCoords(new Vector2f(texCoord.x(), texCoord.y()));
            }

            mesh.getVertices().addLast(vertex);
        }

        // Process indexes
        for (int i = 0; i < aiMesh.mNumFaces(); i++) {
            AIFace face = aiMesh.mFaces().get(i);

            for (int j = 0; j < face.mNumIndices(); j++) {
                mesh.getIndices().addLast(face.mIndices().get(j));
            }
        }

        // Process materials
        if (aiMesh.mMaterialIndex() >= 0) {
            PointerBuffer materialBuffer = scene.mMaterials();

            if (materialBuffer != null) {
                long materialPointer = materialBuffer.get(aiMesh.mMaterialIndex());

                AIMaterial material = AIMaterial.create(materialPointer);
                List<Texture> diffuseMaps = loadMaterialTextures(material, aiTextureType_DIFFUSE, "texture_diffuse");
                if (!diffuseMaps.isEmpty()) mesh.getTextures().addAll(diffuseMaps);

                List<Texture> specularMaps = loadMaterialTextures(material, aiTextureType_SPECULAR, "texture_specular");
                if (!specularMaps.isEmpty()) mesh.getTextures().addAll(specularMaps);
            }
        }

        mesh.setup();

        return mesh;
    }

    private List<Texture> loadMaterialTextures(AIMaterial material, int aiTextureType, String typeName) {
        List<Texture> textures = new ArrayList<>();

        try (MemoryStack stack = MemoryStack.stackPush()) {
            AIString path = AIString.calloc(stack);
            int materialCount = aiGetMaterialTextureCount(material, aiTextureType);

            for (int i = 0; i < materialCount; i++) {
                int result = Assimp.aiGetMaterialTexture(material, aiTextureType, i, path, (IntBuffer) null, null, null, null, null, null);
                if (result != Assimp.aiReturn_SUCCESS) continue;

                String texturePath = path.dataString().replace("\\", "/");
                Optional<Texture> foundTexture = texturesLoaded.stream().filter(t -> t.getPath().equals(texturePath)).findFirst();

                if (foundTexture.isPresent()) {
                    textures.addLast(foundTexture.get());
                    continue;
                }

                Texture texture = new Texture();
                texture.setId(ResourceLoader.loadTexture(texturePath, directory));
                texture.setType(typeName);
                texture.setPath(texturePath);

                textures.addLast(texture);
                texturesLoaded.addLast(texture);
            }
        }
        return textures;
    }

    public void draw(Shader shader) {
        meshes.forEach(m -> m.draw(shader));
    }
}
