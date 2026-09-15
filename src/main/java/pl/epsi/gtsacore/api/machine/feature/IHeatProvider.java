package pl.epsi.gtsacore.api.machine.feature;

import com.gregtechceu.gtceu.api.machine.feature.IMachineFeature;

public interface IHeatProvider extends IMachineFeature {
    int getHeatLevel();
}
