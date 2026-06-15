package pl.epsi.gtsacore.common.data.item;

import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.item.Item;

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

    public static final ItemEntry<Item> UNFIRED_CERAMIC_INGOT_MOLD = registerCeramicMold("Ingot", "Unfired");
    public static final ItemEntry<Item> UNFIRED_CERAMIC_PLATE_MOLD = registerCeramicMold("Plate", "Unfired");
    public static final ItemEntry<Item> UNFIRED_CERAMIC_ROD_MOLD = registerCeramicMold("Rod", "Unfired");

    public static final ItemEntry<Item> CERAMIC_INGOT_MOLD = registerCeramicMold("Ingot", "");
    public static final ItemEntry<Item> CERAMIC_PLATE_MOLD = registerCeramicMold("Plate", "");
    public static final ItemEntry<Item> CERAMIC_ROD_MOLD = registerCeramicMold("Rod", "");


    private static ItemEntry<Item> registerCeramicMold(String type, String fired) {
        return GTSAC_REGISTRATE
                .item(fired.toLowerCase() + (fired.isEmpty() ? "" : "_") + "ceramic_" + type.toLowerCase() + "_mold", Item::new)
                .lang(fired + " Ceramic " + type + " Mold")
                .register();
    }

}
