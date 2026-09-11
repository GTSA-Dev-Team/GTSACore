package pl.epsi.gtsacore.integration.jei.element;

import net.minecraft.client.gui.GuiGraphics;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL45;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.api.renderer.DefaultVertex;
import pl.epsi.gtsacore.api.renderer.consumer.SACVertexConsumer;
import pl.epsi.gtsacore.api.renderer.shader.SACShaderProgram;
import pl.epsi.gtsacore.util.ChemistryUtil;

public class ElementRenderer {

    public final SACVertexConsumer<DefaultVertex> vc = new SACVertexConsumer<>(DefaultVertex.TEMPLATE, new SACShaderProgram(GTSubatomicCore.id("shader/element/base.vsh"), GTSubatomicCore.id("shader/element/base.fsh")));

    public static final ElementRenderer INSTANCE = new ElementRenderer();

    // LD_PRELOAD=/opt/renderdoc/lib/librenderdoc.so
    public void draw(GuiGraphics ctx, ElementInfo el) {
        var mat = ctx.pose().last().pose();
        var proj = new Matrix4f().ortho(0, ctx.guiWidth(), ctx.guiHeight(), 0, 0, 1000);
        int middleX = 176 / 2;
        int middleY = 166 / 2;

        for (int i = 1; i <= el.orbitalCount(); i++) {
            drawOrbital(i, middleX, middleY);
        }

        vc.setUniformSetup((s) -> {
            s.uniformMat4f("localMatrix", mat);
            s.uniformMat4f("projMatrix", proj);
        });

        vc.drawLines();

        int num = el.electronCount();
        for (int i = 1; i <= el.orbitalCount(); i++) {
            num -= drawElectrons(i, num);
        }

        GL45.glDisable(GL45.GL_CULL_FACE);

        vc.drawTriangles();
    }

    private int drawElectrons(int shell, int electronCount) {
        int r = 20 + 10 * shell;
        int maxNum = ChemistryUtil.getOrbitalSlots(shell);

        int electrons = maxNum;

        if (electronCount < maxNum) {
            electrons = electronCount;
        }

        for (int i = 0; i < electrons; i++) {
            float angle = (float) (2.0 * Math.PI * i / electrons);

            float x = (float) Math.cos(angle) * r + (176 / 2);
            float y = (float) Math.sin(angle) * r + (166 / 2);

            drawCircle(x, y, 3);
        }

        return electrons;
    }

    private void drawCircle(float x, float y, int r) {
        vc.begin();

        vc.putVertices(new DefaultVertex(x, y, 0, 0, 0, 1, 1));
        float step = (float) (Math.PI * 2.0 / 10);

        for (int i = 0; i < 10; i++) {
            float a0 = i * step;

            float x0 = (float) (x + Math.cos(a0) * r);
            float y0 = (float) (y + Math.sin(a0) * r);

            vc.putVertices(new DefaultVertex(x0, y0, 0, 0, 0, 1, 1));
            vc.putIndices(0, 1 + i, 1 + (i + 1) % 10);
        }
    }

    private void drawOrbital(int shell, int middleX, int middleY) {
        int r = 20 + 10 * shell;

        float circumference = 2.0f * (float) Math.PI * r;
        float spacing = 4f;

        int segments = Math.max(10, (int) Math.ceil(circumference / spacing));
        float step = 360.0f / segments;

        vc.begin();
        for (int i = 0; i < segments; i++) {
            float angle = (float) Math.toRadians(i * step);

            float x = (float) Math.cos(angle) * r + middleX;
            float y = (float) Math.sin(angle) * r + middleY;

            vc.putVertices(new DefaultVertex(x, y, 0, 0, 0.5f, 1, 1));
            vc.putIndices(i, (i + 1) % segments);
        }
    }

}
