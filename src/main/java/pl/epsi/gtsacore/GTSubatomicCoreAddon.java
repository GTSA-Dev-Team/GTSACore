package pl.epsi.gtsacore;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

@GTAddon
public class GTSubatomicCoreAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return GTSubatomicCore.GTSAC_REGISTRATE;
    }

    @Override
    public void initializeAddon() {}

    @Override
    public String addonModId() {
        return GTSubatomicCore.MOD_ID;
    }
}
