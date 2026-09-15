package pl.epsi.gtsacore.common.machine;

import com.gregtechceu.gtceu.api.machine.feature.IMachineFeature;

public interface IHeatProvider extends IMachineFeature {
    int getHeatLevel();

}
