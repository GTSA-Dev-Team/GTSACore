package pl.epsi.gtsacore.datagen;

import com.gregtechceu.gtceu.api.registry.registrate.SoundEntryBuilder;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.common.world.feature.GTSACConfiguredFeatures;
import pl.epsi.gtsacore.common.world.feature.GTSACPlacedFeatures;

import java.util.Set;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GTSACDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        var registries = event.getLookupProvider();

        if (event.includeClient()) {
            generator.addProvider(true, new SoundEntryBuilder.SoundEntryProvider(packOutput, GTSubatomicCore.MOD_ID));
        }
        if (event.includeServer()) {
            var set = Set.of(GTSubatomicCore.MOD_ID);
            DatapackBuiltinEntriesProvider provider = generator.addProvider(true, new DatapackBuiltinEntriesProvider(
                    packOutput, registries, new RegistrySetBuilder()

                    .add(Registries.CONFIGURED_FEATURE, GTSACConfiguredFeatures::bootstrap)
                    .add(Registries.PLACED_FEATURE, GTSACPlacedFeatures::bootstrap),
                    set));
            //generator.addProvider(true, new GTSACWorldGenProvider(packOutput, event.getLookupProvider()));
        }


    }
}
