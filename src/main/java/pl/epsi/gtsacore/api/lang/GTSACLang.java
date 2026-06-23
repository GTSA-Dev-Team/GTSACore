package pl.epsi.gtsacore.api.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class GTSACLang {

    public static void init(RegistrateLangProvider provider) {
        provider.add("config.jade.plugin_gtsac.crucible_provider", "[GTSAC] Crucible");
        provider.add("config.jade.plugin_gtsac.casting_table_provider", "[GTSAC] Casting Table");
    }

}
