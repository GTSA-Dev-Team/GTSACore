package pl.epsi.gtsacore.datagen.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class GTSACMaterialLangHandler {
    public static void init(RegistrateLangProvider provider) {
        provider.add("tagprefix.large_spool", "Large %s Spool");

        provider.add("material.gtsac.malzzium", "Malzzium");
        provider.add("material.gtsac.clavium", "Clavium");
    }
}
