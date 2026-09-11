package pl.epsi.gtsacore.common.machine;


import org.jetbrains.annotations.Nullable;

public interface IHeatSubmissive {
    @Nullable IHeatDominant getHeatSource();
    void setHeatSource(IHeatDominant source);
}
