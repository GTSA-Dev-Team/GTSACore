package pl.epsi.gtsacore;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.PostMaterialEvent;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.epsi.gtsacore.common.data.GTSACBlocks;
import pl.epsi.gtsacore.common.data.GTSACMachines;
import pl.epsi.gtsacore.common.data.GTSACRecipeTypes;
import pl.epsi.gtsacore.common.data.item.GTSACItems;
import pl.epsi.gtsacore.common.data.materials.GTSACMaterials;
import pl.epsi.gtsacore.common.data.materials.GTSACPeriodicTableMaterials;

@Mod(GTSubatomicCore.MOD_ID)
@SuppressWarnings("removal")
public class GTSubatomicCore {

    public static final String MOD_ID = "gtsac";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final GTRegistrate GTSAC_REGISTRATE = GTRegistrate.create(GTSubatomicCore.MOD_ID);

    public static RegistryEntry<CreativeModeTab> GTSAC_CREATIVE_TAB = GTSAC_REGISTRATE
            .defaultCreativeTab(GTSubatomicCore.MOD_ID,
                    builder -> builder
                            .displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator(GTSubatomicCore.MOD_ID,
                                    GTSAC_REGISTRATE))
                            .title(GTSAC_REGISTRATE.addLang("itemGroup", GTSubatomicCore.id("creative_tab"),
                                    "GregTech: Subatomic Age - Core"))
                            .icon(GTSACItems.ZETA_FLUXON::asStack)
                            .build())
            .register();

    public GTSubatomicCore() {
        GTSubatomicCore.init();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addGenericListener(GTRecipeType.class, this::registerRecipeTypes);
        modEventBus.addGenericListener(MachineDefinition.class, this::registerMachines);

        // TODO: FIX THIS
        GTSAC_REGISTRATE.addRawLang("config.jade.plugin_gtsac.crucible_provider", "[GTSAC] Crucible");

        modEventBus.addListener(this::registerMaterials);
        modEventBus.addListener(this::modifyMaterials);

        GTSAC_REGISTRATE.registerRegistrate();

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public static void registerCommand(RegisterCommandsEvent event) {
        //event.getDispatcher().register(literal())
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

    public static void init() {
        GTSACBlocks.init();
        GTSACItems.init();
    }

    private void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        GTSACRecipeTypes.init();
    }

    private void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        GTSACMachines.init();
    }

    private void registerMaterials(MaterialEvent event) {
        GTSACPeriodicTableMaterials.register();
    }

    private void modifyMaterials(PostMaterialEvent event) {
        GTSACPeriodicTableMaterials.modifyMaterials();
        GTSACMaterials.modifyMaterials();
    }




}
