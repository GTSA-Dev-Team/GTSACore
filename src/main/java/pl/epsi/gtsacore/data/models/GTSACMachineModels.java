package pl.epsi.gtsacore.data.models;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import pl.epsi.gtsacore.GTSubatomicCore;

import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.casingTextures;

public class GTSACMachineModels {
    public static MachineBuilder.ModelInitializer createOverlayCasingMachineModel(String overlayName,
                                                                                  ResourceLocation casingTexturePath) {
        return (ctx, prov, builder) -> {
            builder.forAllStatesModels(state -> {
                BlockModelBuilder model = prov.models().nested()
                        .parent(prov.models().getExistingFile(GTCEu.id("block/overlay/2_layer/front_emissive")));
                casingTextures(model, casingTexturePath);

                model.texture("overlay", GTSubatomicCore.id("block/overlay/machine/" + overlayName + "_base"));
                model.texture("overlay_emissive",
                        GTSubatomicCore.id("block/overlay/machine/" + overlayName + "_emissive"));
                return model;
            });

            builder.addReplaceableTextures("bottom", "top", "side");
        };
    }

}
