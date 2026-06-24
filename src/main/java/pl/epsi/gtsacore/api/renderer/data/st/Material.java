package pl.epsi.gtsacore.api.renderer.data.st;

import net.minecraft.client.renderer.texture.AbstractTexture;
import org.joml.Vector3f;

public class Material {

    public final AbstractTexture albedo;
    public final AbstractTexture normal;
    public final AbstractTexture roughness;
    public final AbstractTexture metallic;

    public final Vector3f tint;

    public Material(AbstractTexture albedo, AbstractTexture normal, AbstractTexture roughness, AbstractTexture metallic, Vector3f tint) {
        this.albedo = albedo;
        this.normal = normal;
        this.roughness = roughness;
        this.metallic = metallic;
        this.tint = tint;
    }
    
}
