package pl.epsi.gtsacore.common.data.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.ToolProperty;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import net.minecraft.world.item.enchantment.Enchantments;


public class GTSACMaterials {

    public static void register() {

    }


    public static void modifyMaterials() {
        GTMaterials.Stone.setProperty(PropertyKey.TOOL, ToolProperty.Builder.of(0.0F, 1.0F, 128, 1)
                .types(GTToolType.HARD_HAMMER)
                .enchantability(5).ignoreCraftingTools()
                .build());
    }
}
