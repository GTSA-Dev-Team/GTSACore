package pl.epsi.gtsacore.datagen;

import com.gregtechceu.gtceu.api.registry.registrate.SoundEntryBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import pl.epsi.gtsacore.GTSubatomicCore;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class GTSACDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        var registries = event.getLookupProvider();

        boolean server = event.includeServer();

        // generator.addProvider(server, new CosmicTinkersMaterials(packOutput));
        // generator.addProvider(server, new CosmicMaterialStats(packOutput));
        // generator.addProvider(server, new CosmicMaterialTraits(packOutput));

        if (event.includeClient()) {
            generator.addProvider(true, new SoundEntryBuilder.SoundEntryProvider(packOutput, GTSubatomicCore.MOD_ID));
        }
    }
}
