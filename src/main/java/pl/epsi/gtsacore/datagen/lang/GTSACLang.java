package pl.epsi.gtsacore.datagen.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.network.chat.Component;

public class GTSACLang {

    public static void init(RegistrateLangProvider provider) {
        //////////////////////////
        ////       JADE       ////
        //////////////////////////

        provider.add("config.jade.plugin_gtsac.crucible_provider", "[GTSAC] Crucible");
        provider.add("config.jade.plugin_gtsac.casting_table_provider", "[GTSAC] Casting Table");
        provider.add("config.jade.plugin_gtsac.incomplete_crafting_table_provider", "[GTSAC] Crafting Table [Incomplete]");

        provider.add("gtsac.jade.casting_table.hits", "%s / %s Hits");
        provider.add("gtsac.jade.casting_table.filling", "Filling");
        provider.add("gtsac.jade.casting_table.solidifying", "Solidifying");
        provider.add("gtsac.jade.ict", " Chops");

        //////////////////////////
        ////       TAGS       ////
        //////////////////////////

        provider.add("tagprefix.large_spool", "Large %s Spool");

        //////////////////////////
        ////       MATS       ////
        //////////////////////////

        provider.add("material.gtsac.malzzium", "Malzzium");
        provider.add("material.gtsac.clavium", "Clavium");
        provider.add("material.gtsac.slag", "Slag");

        //////////////////////////
        ////        EMI       ////
        //////////////////////////

        provider.add("emi_info.gtsac.fuel.1", "Total: %d FU");
        provider.add("emi_info.gtsac.fuel.2", "Usage: %d FU/t");
        provider.add("emi_info.gtsac.foundry", "Tolerance: %d");

        //////////////////////////
        ////     TOOLTIPS     ////
        //////////////////////////

        provider.add("gtsac.machine.primitive_input_bus.tooltip", "Low-Tech Item Input for Multiblocks");
        provider.add("gtsac.machine.primitive_output_bus.tooltip", "Low-Tech Item Output for Multiblocks");
        provider.add("gtsac.machine.primitive_input_hatch.tooltip", "Low-Tech Fluid Input for Multiblocks");
        provider.add("gtsac.machine.primitive_output_hatch.tooltip", "Low-Tech Fluid Output for Multiblocks");
        provider.add("gtsac.machine.fuel_hatch.tooltip", "Low-Tech Burnable Fuel for Multiblocks");

        //////////////////////////
        ////     MACHINES AND WHAT NOT     ////
        //////////////////////////

        provider.add("gtsac.machine.foundry.tank_space", "Tank space: ");
        provider.add("gtsac.machine.foundry.pour", "Pour");
        provider.add("gtsac.machine.foundry.alloy", "Alloy");
        provider.add("gtsac.machine.foundry.tank_fail_output", "Insufficient tank space!");
        provider.add("gtsac.machine.foundry.tank_fail_input", "Insufficient inputs!");
        provider.add("gtsac.machine.fuel_hatch.multi", "Fuel: ");
        provider.add("gtsac.machine.workable_fueled.fuel_fail", "Not enough fuel! (Maybe feed it some Oh-How-Delicous Coal?)");
    }

}
