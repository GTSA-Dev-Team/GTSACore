package pl.epsi.gtsacore.common.data.item.casting;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public abstract class AbstractCastItem extends Item {

    private static final HashMap<Integer, Integer> REGULAR_CACHE = new HashMap<>();
    private static final HashMap<Integer, Integer> SLIGHTLY_CRACKED_CACHE = new HashMap<>();
    private static final HashMap<Integer, Integer> SEVERELY_CRACKED_CACHE = new HashMap<>();

    private static final String CRACKED_TAG = "Cracked";

    static {
        RenderSystem.recordRenderCall(() -> {
            var texManager = Minecraft.getInstance().getTextureManager();
            REGULAR_CACHE.put(0, texManager.getTexture(GTSubatomicCore.id("textures/block/ceramic_block.png")).getId());
            SLIGHTLY_CRACKED_CACHE.put(0, texManager.getTexture(GTSubatomicCore.id("textures/block/slightly_cracked_ceramic_block.png")).getId());
            SEVERELY_CRACKED_CACHE.put(0, texManager.getTexture(GTSubatomicCore.id("textures/block/severely_cracked_ceramic_block.png")).getId());
        });
    }

    @Getter
    private final CastRenderInfo renderInfo;
    @Getter
    private final String itemSuffix;

    public AbstractCastItem(Properties properties, CastRenderInfo renderInfo, String itemSuffix) {
        super(properties);
        this.renderInfo = renderInfo;
        this.itemSuffix = itemSuffix;
    }

    public static Map<Integer, Integer> getTextures(ItemStack stack) {
        int cracked = getCracked(stack);
        return switch (cracked) {
            case 1 -> SLIGHTLY_CRACKED_CACHE;
            case 2-> SEVERELY_CRACKED_CACHE;
            default -> REGULAR_CACHE;
        };
    }

    public static int getCracked(ItemStack stack) {
        if (!stack.hasTag()) setCracked(stack, 0);
        return stack.getOrCreateTag().getInt(CRACKED_TAG);
    }

    public static String getCrackedName(ItemStack stack) {
        int cracked = getCracked(stack);
        return switch (cracked) {
            case 1 -> "Slightly Cracked";
            case 2 -> "Severely Cracked";
            default -> "Intact";
        };
    }

    public static void setCracked(ItemStack stack, int cracked) {
        stack.getOrCreateTag().putInt(CRACKED_TAG, cracked);
    }

    public static int onUsed(RandomSource random, ItemStack stack) {
        int current = getCracked(stack);
        double percent = current == 0 ? 0.1 : current == 1 ? 0.2 : 0.35;
        if (random.nextDouble() < percent) {
            if (current == 2) return 1;
            setCracked(stack, current + 1);
            return 2;
        }
        return 0;
    }

    public record CastRenderInfo(StaticVertexBuffer<ObjVertexFormat> VBO, SACShaderProgram shader, Matrix4f localMat, AABB cavityBounds,
                                 Vector3f pourPoint) {
        public static CastRenderInfo of(ResourceLocation model, Matrix4f localMat, AABB cavityBounds, Vector3f pourPoint) {
            StaticVertexBuffer<ObjVertexFormat> buf;
            try {
                var mesh = ObjParser.load(model);
                buf = new StaticVertexBuffer<>(mesh.vertices(), mesh.indices());
            } catch (IOException e) {
                throw new RuntimeException("Failed to find model: " + model);
            }

            return new CastRenderInfo(buf, ObjRenderer.getDefaultObjShader(), localMat, cavityBounds, pourPoint);
        }
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(super.getName(stack).getString() + " [" + getCrackedName(stack) + "]");
    }

}
