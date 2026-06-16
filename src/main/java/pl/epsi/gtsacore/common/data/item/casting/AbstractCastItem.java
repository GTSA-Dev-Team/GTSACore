package pl.epsi.gtsacore.common.data.item.casting;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.api.model.ObjParser;
import pl.epsi.gtsacore.api.model.ObjVertexFormat;
import pl.epsi.gtsacore.api.renderer.data.StaticVertexBuffer;
import pl.epsi.gtsacore.api.renderer.shader.SACShaderProgram;
import pl.epsi.gtsacore.common.render.ObjRenderer;

import java.io.IOException;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractCastItem extends Item {

    @Getter
    private final CastRenderInfo renderInfo;
    @Getter
    private final String itemSuffix;

    private Map<Integer, Integer> CACHE;

    public AbstractCastItem(Properties properties, CastRenderInfo renderInfo, String itemSuffix) {
        super(properties);
        this.renderInfo = renderInfo;
        this.itemSuffix = itemSuffix;
    }

    public Map<Integer, Integer> getTextures() {
        if (CACHE != null) return CACHE;
        CACHE = renderInfo.textures().get();
        return CACHE;
    }

    public record CastRenderInfo(StaticVertexBuffer<ObjVertexFormat> VBO, SACShaderProgram shader, Matrix4f localMat, AABB cavityBounds,
                                 Vector3f pourPoint, Supplier<Map<Integer, Integer>> textures) {
        public static CastRenderInfo of(ResourceLocation model, Matrix4f localMat, AABB cavityBounds, Vector3f pourPoint) {
            StaticVertexBuffer<ObjVertexFormat> buf;
            try {
                var mesh = ObjParser.load(model);
                buf = new StaticVertexBuffer<>(mesh.vertices(), mesh.indices());
            } catch (IOException e) {
                throw new RuntimeException("Failed to find model: " + model);
            }

            return new CastRenderInfo(buf, ObjRenderer.getDefaultObjShader(), localMat, cavityBounds, pourPoint, () -> {
                AbstractTexture tex =
                        Minecraft.getInstance()
                                .getTextureManager()
                                .getTexture(GTSubatomicCore.id("textures/block/ceramic_block.png"));

                return Map.of(0, tex.getId());
            });
        }
    }

}
