package pl.epsi.gtsacore.util;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.joml.Matrix4f;

public class SACRenderUtil {

    public static float[] getUV(TextureAtlasSprite sprite, float width, float height) {
        float u0 = sprite.getU0();
        float v0 = sprite.getV0();

        float u1 = u0 + (sprite.getU1() - u0) * (width / 16.0f);
        float v1 = v0 + (sprite.getV1() - v0) * (height / 16.0f);

        return new float[] {u0, v0, u1, v1};
    }

    private static void vertex(
            VertexConsumer consumer,
            Matrix4f mat,
            int color,
            int overlay,
            int packedLight,
            float x, float y, float z,
            float u, float v,
            float nx, float ny, float nz
    ) {
        consumer.vertex(mat, x, y, z)
                .color(color)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(packedLight)
                .normal(nx, ny, nz)
                .endVertex();
    }

    public static void renderPrism(VertexConsumer consumer, Matrix4f mat, TextureAtlasSprite sprite, int color, int overlay,
                                   int packedLight, float width, float height, float depth) {
        float x0 = -width * 0.5f;
        float x1 =  width * 0.5f;

        float z0 = -depth * 0.5f;
        float z1 =  depth * 0.5f;

        float y0 = 0.0f;
        float y1 = height;

        // UV helpers (block-space -> texture-space)
        float topU0 = (x0 + 0.5f) * 16.0f;
        float topU1 = (x1 + 0.5f) * 16.0f;
        float topV0 = (z0 + 0.5f) * 16.0f;
        float topV1 = (z1 + 0.5f) * 16.0f;

        float sideU0X = (x0 + 0.5f) * 16.0f;
        float sideU1X = (x1 + 0.5f) * 16.0f;

        float sideU0Z = (z0 + 0.5f) * 16.0f;
        float sideU1Z = (z1 + 0.5f) * 16.0f;

        float sideV0 = 0.0f;
        float sideV1 = y1 * 16.0f;

        // =========================
        // TOP (+Y) CCW from above
        // =========================

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y1, z0,
                sprite.getU(topU0), sprite.getV(topV0),
                0, 1, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y1, z1,
                sprite.getU(topU0), sprite.getV(topV1),
                0, 1, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y1, z1,
                sprite.getU(topU1), sprite.getV(topV1),
                0, 1, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y1, z0,
                sprite.getU(topU1), sprite.getV(topV0),
                0, 1, 0);

        // =========================
        // NORTH (-Z)
        // =========================

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y0, z0,
                sprite.getU(sideU1X), sprite.getV(sideV0),
                0, 0, -1);

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y0, z0,
                sprite.getU(sideU0X), sprite.getV(sideV0),
                0, 0, -1);

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y1, z0,
                sprite.getU(sideU0X), sprite.getV(sideV1),
                0, 0, -1);

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y1, z0,
                sprite.getU(sideU1X), sprite.getV(sideV1),
                0, 0, -1);

        // =========================
        // SOUTH (+Z)
        // =========================

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y0, z1,
                sprite.getU(sideU0X), sprite.getV(sideV0),
                0, 0, 1);

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y0, z1,
                sprite.getU(sideU1X), sprite.getV(sideV0),
                0, 0, 1);

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y1, z1,
                sprite.getU(sideU1X), sprite.getV(sideV1),
                0, 0, 1);

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y1, z1,
                sprite.getU(sideU0X), sprite.getV(sideV1),
                0, 0, 1);

        // =========================
        // WEST (-X)
        // =========================

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y0, z0,
                sprite.getU(sideU0Z), sprite.getV(sideV0),
                -1, 0, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y0, z1,
                sprite.getU(sideU1Z), sprite.getV(sideV0),
                -1, 0, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y1, z1,
                sprite.getU(sideU1Z), sprite.getV(sideV1),
                -1, 0, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x0, y1, z0,
                sprite.getU(sideU0Z), sprite.getV(sideV1),
                -1, 0, 0);

        // =========================
        // EAST (+X)
        // =========================

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y0, z1,
                sprite.getU(sideU1Z), sprite.getV(sideV0),
                1, 0, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y0, z0,
                sprite.getU(sideU0Z), sprite.getV(sideV0),
                1, 0, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y1, z0,
                sprite.getU(sideU0Z), sprite.getV(sideV1),
                1, 0, 0);

        vertex(consumer, mat, color, overlay, packedLight,
                x1, y1, z1,
                sprite.getU(sideU1Z), sprite.getV(sideV1),
                1, 0, 0);
    }

}
