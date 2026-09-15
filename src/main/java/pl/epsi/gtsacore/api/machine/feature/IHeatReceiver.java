package pl.epsi.gtsacore.api.machine.feature;

import org.jetbrains.annotations.Nullable;

public interface IHeatReceiver {
    @Nullable IHeatProvider getHeatSource();
    void setHeatSource(IHeatProvider source);
}
