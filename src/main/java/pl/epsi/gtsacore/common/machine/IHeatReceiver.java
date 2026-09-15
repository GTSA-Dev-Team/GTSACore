package pl.epsi.gtsacore.common.machine;


import org.jetbrains.annotations.Nullable;

public interface IHeatReceiver {
    @Nullable IHeatProvider getHeatSource();
    void setHeatSource(IHeatProvider source);
}
