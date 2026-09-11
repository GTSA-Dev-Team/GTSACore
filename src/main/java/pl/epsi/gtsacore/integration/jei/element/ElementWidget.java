package pl.epsi.gtsacore.integration.jei.element;

import dev.emi.emi.api.widget.Bounds;
import dev.emi.emi.api.widget.Widget;
import net.minecraft.client.gui.GuiGraphics;

public class ElementWidget extends Widget {

    private final ElementInfo el;

    public ElementWidget(ElementInfo el) {
        this.el = el;
    }

    @Override
    public Bounds getBounds() {
        return new Bounds(0, 0, 176, 166);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int i, int i1, float v) {
        ElementRenderer.INSTANCE.draw(guiGraphics, el);
    }
}
