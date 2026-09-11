package pl.epsi.gtsacore.api.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import pl.epsi.gtsacore.common.machine.IHeatSubmissive;

public class GTSACCapability {
    public static final Capability<IHeatSubmissive> CAPABILITY_HEAT_SUBMISSIVE = CapabilityManager.get(new CapabilityToken<>() {
    });

    public GTSACCapability() {}

    public static void register(RegisterCapabilitiesEvent event) {
        event.register(IHeatSubmissive.class);
    }

}
