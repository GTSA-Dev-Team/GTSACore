package pl.epsi.gtsacore.api.lang;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class GTSACLang extends LanguageProvider {

    public GTSACLang(PackOutput output, String modId, String locale) {
        super(output, modId, locale);
    }

    @Override
    protected void addTranslations() {
        add("config.jade.plugin_gtsac.crucible_provider", "[GTSAC] Crucible");
    }

}
