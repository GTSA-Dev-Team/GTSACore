package pl.epsi.gtsacore.datagen.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class GTSACLang {

    public static void init(RegistrateLangProvider provider) {
        //////////////////////////
        ////       JADE       ////
        //////////////////////////

        provider.add("config.jade.plugin_gtsac.crucible_provider", "[GTSAC] Crucible");
        provider.add("config.jade.plugin_gtsac.casting_table_provider", "[GTSAC] Casting Table");
        provider.add("config.jade.plugin_gtsac.incomplete_crafting_table_provider", "[GTSAC] Crafting Table [Incomplete]");

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
    }

}
