#version 330 core

// Texture samplers
uniform sampler2D texture_diffuse1;
uniform sampler2D texture_diffuse2;
uniform sampler2D texture_diffuse3;
uniform sampler2D texture_specular1;
uniform sampler2D texture_specular2;

// Cam pos
uniform vec3 viewPos;

in vec2 texCoords;
in vec3 fragPos;
in vec3 normals;

out vec4 fragColor;

// Material
#define MAX_TEX_DIFFUSE 4
#define MAX_TEX_SPECULAR 2
struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    vec3 emission;

    float shininess;

    sampler2D texture_diffuse[MAX_TEX_DIFFUSE];
    sampler2D texture_specular[MAX_TEX_SPECULAR];

    bool enabled;
};
uniform Material material;

// Directional light
struct DirLight {
    vec3 direction;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    bool enabled;
};
uniform DirLight dirLight;
vec3 calcDirLight(DirLight light, vec3 normal, vec3 viewDir);

// Point light
struct PointLight {
    vec3 position;

    float constant;
    float linear;
    float quadratic;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    bool enabled;
};
//uniform PointLight pointLight;
#define MAX_POINT_LIGHTS 10
uniform PointLight pointLights[MAX_POINT_LIGHTS];
vec3 calcPointLight(PointLight light, vec3 normal, vec3 viewDir, vec3 fragPos);

float linearDepth();

void main() {
    vec3 normal = normalize(normals);
    vec3 viewDir = normalize(viewPos - fragPos);

    vec3 result = vec3(0.0f);
    result += calcDirLight(dirLight, normal, viewDir);
    for (int i = 0; i < MAX_POINT_LIGHTS; i++) {
        if (!pointLights[i].enabled) continue;
        result += calcPointLight(pointLights[i], normal, viewDir, fragPos);
    }

    fragColor = vec4(result, 1.0f);



    //    fragColor = vec4(vec3(linearDepth()), 1.0f);
}

float linearDepth() {
    float near = 0.1;
    float far = 100.0;
    float z = gl_FragCoord.z * 2.0 - 1.0; // back to NDC
    return ((2.0 * near * far) / (far + near - z * (far - near))) / far;
}

vec3 calcDirLight(DirLight light, vec3 normal, vec3 viewDir) {
    // Ambient
    vec3 ambient = light.ambient * vec3(texture(texture_diffuse1, texCoords));

    // Diffuse
    vec3 lightDir = normalize(-light.direction);
    float diff = max(dot(normal, lightDir), 0.0f);
    vec3 diffuse = light.diffuse * diff * vec3(texture(texture_diffuse1, texCoords));

    // Specular
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 32.0);
    vec3 specular = light.specular * spec * vec3(texture(texture_specular1, texCoords));

    return ambient + diffuse + specular;
}

vec3 calcPointLight(PointLight light, vec3 normal, vec3 viewDir, vec3 fragPos) {
    // Ambient
    vec3 ambient = light.ambient * vec3(texture(texture_diffuse1, texCoords));

    // Diffuse
    vec3 lightDir = normalize(light.position - fragPos);
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 diffuse = light.diffuse * diff * vec3(texture(texture_diffuse1, texCoords));

    // Specular
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), 64);
    vec3 specular = light.specular * spec * vec3(texture(texture_specular1, texCoords));

    // Attenuation
    float distance = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * pow(distance, 2.0f));

    ambient *= attenuation;
    diffuse *= attenuation;
    specular *= attenuation;

    return ambient + diffuse + specular;
}