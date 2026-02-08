package pl.epsi.gtsacore;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.epsi.gtsacore.common.data.GTSACMachines;
import pl.epsi.gtsacore.common.data.GTSACRecipeTypes;

@Mod(GTSubatomicCore.MOD_ID)
@SuppressWarnings("removal")
public class GTSubatomicCore {

    public static final String MOD_ID = "gtsac";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final GTRegistrate GTSAC_REGISTRATE = GTRegistrate.create(GTSubatomicCore.MOD_ID);

    public GTSubatomicCore() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addGenericListener(GTRecipeType.class, this::registerRecipeTypes);
        modEventBus.addGenericListener(MachineDefinition.class, this::registerMachines);
        GTSAC_REGISTRATE.registerRegistrate();

        MinecraftForge.EVENT_BUS.register(this);
    }

    /**
     * Create a ResourceLocation in the format "modid:path"
     *
     * @param path
     * @return ResourceLocation with the namespace of your mod
     */
    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        GTSACRecipeTypes.init();
    }

    private void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        GTSACMachines.init();
    }
}
