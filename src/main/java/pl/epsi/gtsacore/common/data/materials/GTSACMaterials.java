package pl.epsi.gtsacore.common.data.materials;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.Element;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.BlastProperty;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.ToolProperty;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import net.minecraft.world.item.enchantment.Enchantments;
import pl.epsi.gtsacore.GTSubatomicCore;


public class GTSACMaterials {

    public static Material CLAVIUM;

    public static void register() {
        CLAVIUM = new Material.Builder(GTSubatomicCore.id("clavium"))
                .ingot()
                .liquid(12137)
                .gas(33697)
                .plasma(1337420)
                .blastTemp(10853, BlastProperty.GasTier.HIGHER)
                .ore(2, 2, true)
                .addOreByproducts(GTSACPeriodicTableMaterials.MALZZIUM, GTMaterials.Dysprosium, GTMaterials.Californium)
                .rotorStats(1200, 1150, 10, 56000)
                .color(0xeeff8b)
                .iconSet(MaterialIconSet.SHINY)
                .langValue("Clavium")
                .element(GTSACElements.CLAVIUM)
                .cableProperties(GTValues.VA[GTValues.UEV], 1024, 0, true)
                .flags(MaterialFlags.SOLDER_MATERIAL, MaterialFlags.GENERATE_DENSE, MaterialFlags.GENERATE_BOLT_SCREW)
                .buildAndRegister();
    }


    public static void modifyMaterials() {
        GTMaterials.Stone.setProperty(PropertyKey.TOOL, ToolProperty.Builder.of(0.0F, 1.0F, 128, 1)
                .types(GTToolType.HARD_HAMMER)
                .enchantability(5).ignoreCraftingTools()
                .build());
    }
}
