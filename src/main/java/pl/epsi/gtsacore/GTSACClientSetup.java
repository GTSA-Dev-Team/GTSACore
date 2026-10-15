package pl.epsi.gtsacore;

import com.lowdragmc.lowdraglib.gui.graphprocessor.data.parameter.ExposedParameter;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import pl.epsi.gtsacore.common.data.item.GTSACItems;

@Mod.EventBusSubscriber(
        modid = GTSubatomicCore.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.MOD,
        value = Dist.CLIENT
)
public class GTSACClientSetup {

    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ItemProperties.register(
                    GTSACItems.CERAMIC_BLOWPIPE.get(),
                    GTSubatomicCore.id("has_glass"),
                    (stack, level, entity, seed) -> {
                        if (!stack.hasTag()) {
                            return 0.0f;
                        }

                        return stack.getTag().getInt("HasGlass");
                    }
            );
        });
    }
}
