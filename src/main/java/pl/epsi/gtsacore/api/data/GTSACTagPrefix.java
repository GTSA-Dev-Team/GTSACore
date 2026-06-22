package pl.epsi.gtsacore.api.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;

import java.util.function.Predicate;

public class GTSACTagPrefix {
    public static TagPrefix largeSpool;

    public static final Predicate<Material> hasWireProp = material -> material.hasProperty(PropertyKey.WIRE);

    public static void initTagPrefixes() {
        largeSpool = new TagPrefix("large_spool")
                .idPattern("large_%s_spool")
                .defaultTagPath("large_spool/%s")
                .unformattedTagPath("large_spool")
                .langValue("Large %s Spool")
                .materialIconType(GTSACMaterialIconType.largeSpool)
                .unificationEnabled(true)
                .generateItem(true)
                .generationCondition(hasWireProp);
    }
}
