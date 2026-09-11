package pl.epsi.gtsacore.datagen.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class GTSACMaterialLangHandler {
    public static void init(RegistrateLangProvider provider) {
        provider.add("tagprefix.large_spool", "Large %s Spool");

        provider.add("material.gtsac.malzzium", "Malzzium");
        provider.add("material.gtsac.clavium", "Clavium");
        provider.add("material.gtsac.slag", "Slag");

        provider.add("emi_info.gtsac.fuel.1", "Total: %d FU");
        provider.add("emi_info.gtsac.fuel.2", "Usage: %d FU/t");

        provider.add("emi_info.gtsac.foundry", "Tolerance: %d lol");
    }
}
