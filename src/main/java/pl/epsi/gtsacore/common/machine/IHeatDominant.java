package pl.epsi.gtsacore.common.machine;

import com.gregtechceu.gtceu.api.machine.feature.IMachineFeature;

public interface IHeatDominant extends IMachineFeature {
    int getHeatLevel();

}
