package pl.epsi.gtsacore.common.data.item;

import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.world.item.Item;
import org.joml.Matrix4f;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.api.model.ObjMesh;
import pl.epsi.gtsacore.api.model.ObjParser;
import pl.epsi.gtsacore.api.model.ObjVertexFormat;
import pl.epsi.gtsacore.api.renderer.data.StaticVertexBuffer;
import pl.epsi.gtsacore.common.data.item.casting.AbstractCastItem;
import pl.epsi.gtsacore.common.render.ObjRenderer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static pl.epsi.gtsacore.GTSubatomicCore.GTSAC_CREATIVE_TAB;
import static pl.epsi.gtsacore.GTSubatomicCore.GTSAC_REGISTRATE;

public class GTSACItems {

    static {
        GTSAC_REGISTRATE.creativeModeTab(() -> GTSAC_CREATIVE_TAB);
    }

    public static void init() {}

    public static final ItemEntry<Item> ZETA_FLUXON = GTSAC_REGISTRATE
            .item("zeta_fluxon", Item::new)
            .lang("Zeta Fluxon")
            .register();

    public static final ItemEntry<? extends AbstractCastItem> INGOT_MOLD = registerMold(GTSAC_REGISTRATE, "Ceramic Ingot Mold", "ceramic_ingot_mold", "obj_models/mold/ingot.obj", 0.46875f);
    public static final ItemEntry<? extends AbstractCastItem> PLATE_MOLD = registerMold(GTSAC_REGISTRATE, "Ceramic Plate Mold", "ceramic_plate_mold", "obj_models/mold/plate.obj", 0.5f);
    public static final ItemEntry<? extends AbstractCastItem> ROD_MOLD = registerMold(GTSAC_REGISTRATE, "Ceramic Rod Mold", "ceramic_rod_mold", "obj_models/mold/rod.obj", 0.46875f);

    public static ItemEntry<? extends AbstractCastItem> registerMold(GTRegistrate registrate, String lang, String name, String objPath, float xOffset) {
        StaticVertexBuffer<ObjVertexFormat> buf = null;
        try {
            var mesh = ObjParser.load(GTSubatomicCore.id(objPath));
            buf = new StaticVertexBuffer<>(mesh.vertices(), mesh.indices());
        } catch (IOException e) {
            throw new RuntimeException("Failed to find model: " + objPath);
        }

        StaticVertexBuffer<ObjVertexFormat> finalBuf = buf;
        return registrate.item(name, (props) -> new AbstractCastItem(props, ObjRenderer.getDefaultObjShader(), finalBuf, new Matrix4f().translate(xOffset, 0.9375f, 0.5f), () -> {
            AbstractTexture tex =
                    Minecraft.getInstance()
                            .getTextureManager()
                            .getTexture(GTSubatomicCore.id("textures/block/ceramic_block.png"));

            return Map.of(0, tex.getId());
        }) {}).lang(lang).register();
    }

}
