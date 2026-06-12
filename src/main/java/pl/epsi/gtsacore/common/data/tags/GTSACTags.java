package pl.epsi.gtsacore.common.data.tags;

import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import pl.epsi.gtsacore.GTSubatomicCore;

public class GTSACTags {
    public static class Items {

        public static final TagKey<Item> FUEL_HATCH_FUELS = tag("fuel_hatch_fuels");

        private static TagKey<Item> tag(String name) {
            return ItemTags.create(GTSubatomicCore.id(name));
        }
    }
}
