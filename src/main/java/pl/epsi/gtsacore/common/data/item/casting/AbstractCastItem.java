package pl.epsi.gtsacore.common.data.item.casting;

import lombok.Getter;
import net.minecraft.world.item.Item;
import org.joml.Matrix4f;
import pl.epsi.gtsacore.api.model.ObjVertexFormat;
import pl.epsi.gtsacore.api.renderer.data.StaticVertexBuffer;
import pl.epsi.gtsacore.api.renderer.shader.SACShaderProgram;

import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractCastItem extends Item {

    @Getter
    private final SACShaderProgram shader;
    @Getter
    private final StaticVertexBuffer<ObjVertexFormat> model;
    @Getter
    private final Matrix4f modelMatrix;

    private final Supplier<Map<Integer, Integer>> textures;

    private Map<Integer, Integer> CACHE;

    public AbstractCastItem(Properties properties, SACShaderProgram shader, StaticVertexBuffer<ObjVertexFormat> model, Matrix4f modelMatrix, Supplier<Map<Integer, Integer>> textures) {
        super(properties);
        this.shader = shader;
        this.model = model;
        this.modelMatrix = modelMatrix;
        this.textures = textures;
    }

    public Map<Integer, Integer> getTextures() {
        if (CACHE != null) return CACHE;
        CACHE = textures.get();
        return CACHE;
    }

}
