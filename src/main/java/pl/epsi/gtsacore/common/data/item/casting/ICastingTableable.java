package pl.epsi.gtsacore.common.data.item.casting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import pl.epsi.gtsacore.api.model.ObjParser;
import pl.epsi.gtsacore.api.model.ObjVertexFormat;
import pl.epsi.gtsacore.api.renderer.data.StaticVertexBuffer;
import pl.epsi.gtsacore.api.renderer.shader.SACShaderProgram;
import pl.epsi.gtsacore.common.render.ObjRenderer;

import java.io.IOException;
import java.util.Map;

public interface ICastingTableable {

    Map<Integer, Integer> getTextures(ItemStack stack);
    RenderInfo getRenderInfo();

    record RenderInfo(StaticVertexBuffer<ObjVertexFormat> VBO, SACShaderProgram shader, Matrix4f localMat, AABB cavityBounds,
                      Vector3f pourPoint) {
        public static RenderInfo of(ResourceLocation model, Matrix4f localMat, AABB cavityBounds, Vector3f pourPoint) {
            StaticVertexBuffer<ObjVertexFormat> buf;
            try {
                var mesh = ObjParser.load(model);
                buf = new StaticVertexBuffer<>(mesh.vertices(), mesh.indices());
            } catch (IOException e) {
                throw new RuntimeException("Failed to find model: " + model);
            }

            return new RenderInfo(buf, ObjRenderer.getDefaultObjShader(), localMat, cavityBounds, pourPoint);
        }
    }

}
