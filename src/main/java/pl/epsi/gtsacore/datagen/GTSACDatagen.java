package pl.epsi.gtsacore.datagen;

import com.tterrag.registrate.providers.ProviderType;
import pl.epsi.gtsacore.api.lang.GTSACLang;
import pl.epsi.gtsacore.datagen.lang.GTSACMaterialLangHandler;

import static pl.epsi.gtsacore.GTSubatomicCore.GTSAC_REGISTRATE;

public class GTSACDatagen {
    public static void init() {
        GTSAC_REGISTRATE.addDataGenerator(ProviderType.LANG, GTSACMaterialLangHandler::init);
        GTSAC_REGISTRATE.addDataGenerator(ProviderType.LANG, GTSACLang::init);
    }
}
