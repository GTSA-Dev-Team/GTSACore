package pl.epsi.gtsacore.common.data.materials;

import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlag;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;

public class GTSACMaterialFlags {
    public static final MaterialFlag GENERATE_LARGE_SPOOL = new MaterialFlag.Builder("generate_large_spool")
            .requireProps(PropertyKey.WIRE)
            .build();

    public static void init() {}
}
