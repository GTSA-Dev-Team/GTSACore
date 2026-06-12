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
}
