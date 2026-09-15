package pl.epsi.gtsacore.common.data.item;

import com.gregtechceu.gtceu.api.item.tool.GTToolItem;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.IGTToolDefinition;
import com.gregtechceu.gtceu.api.item.tool.MaterialToolTier;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.common.data.item.casting.AbstractCastItem;
import pl.epsi.gtsacore.common.data.item.casting.ICastingTableable;
import pl.epsi.gtsacore.common.data.item.casting.IronBloomItem;

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

    public static final ItemEntry<Item> PRIMITIVE_BRICK = GTSAC_REGISTRATE
            .item("primitive_brick", Item::new)
            .lang("Primitive Brick")
            .register();

    public static final ItemEntry<Item> MOLDED_BRICK_MIXTURE = GTSAC_REGISTRATE
            .item("molded_brick_mixture", Item::new)
            .lang("Molded Brick Mixture")
            .register();

    public static final ItemEntry<Item> BRICK_MIXTURE = GTSAC_REGISTRATE
            .item("brick_mixture", Item::new)
            .lang("Brick Mixture")
            .register();

    public static final ItemEntry<Item> CERAMIC_CLAY_BALL = GTSAC_REGISTRATE
            .item("ceramic_clay_ball", Item::new)
            .lang("Ceramic Clay Ball")
            .register();

    public static final ItemEntry<Item> MOLDED_CERAMIC_CLAY = GTSAC_REGISTRATE
            .item("molded_ceramic_clay", Item::new)
            .lang("Molded Ceramic Clay")
            .register();

    public static final ItemEntry<Item> CERAMIC = GTSAC_REGISTRATE
            .item("ceramic", Item::new)
            .lang("Ceramic")
            .register();

    public static final ItemEntry<Item> WOOD_INGOT = GTSAC_REGISTRATE
            .item("ingot_wood", Item::new)
            .lang("Suspiciously Ingot-Shaped Piece of Wood™")
            .register();

    public static final ItemEntry<Item> WOOD_PLATE = GTSAC_REGISTRATE
            .item("plate_wood", Item::new)
            .lang("Suspiciously Plate-Shaped Piece of Wood™")
            .register();

    public static final ItemEntry<Item> WOOD_ROD = GTSAC_REGISTRATE
            .item("rod_wood", Item::new)
            .lang("Suspiciously Rod-Shaped Piece of Wood™")
            .register();

    public static final ItemEntry<Item> COPPER_HANDLE = GTSAC_REGISTRATE
            .item("copper_handle", Item::new)
            .lang("Copper Handle")
            .register();

    public static final ItemEntry<Item> CERAMIC_CRUCIBLE = GTSAC_REGISTRATE
            .item("ceramic_crucible", Item::new)
            .lang("Ceramic Crucible")
            .register();

    public static final ItemEntry<Item> UNFIRED_INGOT_MOLD = GTSAC_REGISTRATE
            .item("unfired_ceramic_ingot_mold", Item::new)
            .lang("Unfired Ceramic Ingot Mold")
            .register();

    public static final ItemEntry<Item> UNFIRED_PLATE_MOLD = GTSAC_REGISTRATE
            .item("unfired_ceramic_plate_mold", Item::new)
            .lang("Unfired Ceramic Plate Mold")
            .register();

    public static final ItemEntry<Item> UNFIRED_ROD_MOLD = GTSAC_REGISTRATE
            .item("unfired_ceramic_rod_mold", Item::new)
            .lang("Unfired Ceramic Rod Mold")
            .register();

    public static final ItemEntry<Item> GRASS_FIBRE = registerSimpleItem("grass_fibre", "Grass Fibre");

    public static final ItemEntry<? extends AbstractCastItem> INGOT_MOLD = registerMold(GTSAC_REGISTRATE, "Ceramic Ingot Mold", "ceramic_ingot_mold",
            "obj_models/mold/ingot.obj",
            new AABB(-0.09375, 0.125, -0.1875, 0.09375, 0.1875, 0.1875), 0.46875f);
    public static final ItemEntry<? extends AbstractCastItem> PLATE_MOLD = registerMold(GTSAC_REGISTRATE, "Ceramic Plate Mold", "ceramic_plate_mold",
            "obj_models/mold/plate.obj",
            new AABB(-0.125, 0.125, -0.1875, 0.125, 0.1875, 0.1875), 0.5f);
    public static final ItemEntry<? extends AbstractCastItem> ROD_MOLD = registerMold(GTSAC_REGISTRATE, "Ceramic Rod Mold", "ceramic_rod_mold",
            "obj_models/mold/rod.obj",
            new AABB(-0.04375, 0.125, -0.1875, 0.04375, 0.1875, 0.1875), 0.46875f);

    public static ItemEntry<Item> registerSimpleItem(String id, String displayName) {
        return GTSAC_REGISTRATE
                .item(id, Item::new)
                .lang(displayName)
                .register();
    }

    public static ItemEntry<? extends AbstractCastItem> registerMold(GTRegistrate registrate, String lang, String name, String objPath,
                                                                     AABB aabb, float xOffset) {
        return registrate.item(name, (props) -> new AbstractCastItem(props,
                        ICastingTableable.RenderInfo.of(GTSubatomicCore.id(objPath),
                                new Matrix4f().translate(xOffset, 0.9375f, 0.5f),
                                aabb,
                                new Vector3f(0, 0, 0))) {})
                .lang(lang).register();
    }

}
