package pl.epsi.gtsacore.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.client.model.machine.MachineModel;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderHelper;
import com.gregtechceu.gtceu.common.block.BoilerFireboxType;
import com.gregtechceu.gtceu.common.data.*;

import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;
import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Blocks;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.api.renderer.machine.CustomObjDynamicMultiRenderer;
import com.gregtechceu.gtceu.common.machine.multiblock.steam.SteamParallelMultiblockMachine;
import com.tterrag.registrate.util.entry.BlockEntry;
import mezz.jei.api.constants.RecipeTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.common.machine.multiblock.*;
import pl.epsi.gtsacore.common.machine.multiblock.ClarifierMachine;
import pl.epsi.gtsacore.common.machine.multiblock.LargePrimitiveSmelterMachine;
import pl.epsi.gtsacore.common.machine.multiblock.NeutralizationTankMachine;
import pl.epsi.gtsacore.common.machine.multiblock.SteelAugmentedPBFMachine;
import pl.epsi.gtsacore.common.machine.part.PrimitiveFuelHatchPartMachine;
import pl.epsi.gtsacore.data.models.GTSACMachineModels;

import static com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties.IS_FORMED;
import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.*;

import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;
import static pl.epsi.gtsacore.GTSubatomicCore.GTSAC_REGISTRATE;

public class GTSACMachines {

    public static void init() {}

    public static final MachineDefinition STEAM_FLUID_IMPORT_HATCH = GTSAC_REGISTRATE
            .machine("steam_input_hatch", (holder) ->
                    new FluidHatchPartMachine(holder, 0, IO.IN, 4000, 1))
            .rotationState(RotationState.ALL)
            .abilities(GTSACPartAbilities.STEAM_IMPORT_FLUIDS)
            .modelProperty(IS_FORMED, false)
            .colorOverlaySteamHullModel(GTCEu.id("block/overlay/machine/" + OVERLAY_FLUID_HATCH_INPUT),
                    GTCEu.id("block/overlay/machine/overlay_pipe"), GTCEu.id("block/overlay/machine/overlay_pipe_in_emissive"))
            .langValue("Steam Input Hatch")
            .tooltips(Component.translatable("gtceu.machine.fluid_hatch.import.tooltip"),
                    Component.translatable("gtceu.machine.steam_bus.tooltip"),
                    Component.translatable("gtceu.universal.tooltip.fluid_storage_capacity", 4000))
            .allowCoverOnFront(true)
            .register();
    public static final MachineDefinition STEAM_FLUID_EXPORT_HATCH = GTSAC_REGISTRATE
            .machine("steam_output_hatch", (holder) ->
                    new FluidHatchPartMachine(holder, 0, IO.OUT, 4000, 1))
            .rotationState(RotationState.ALL)
            .abilities(GTSACPartAbilities.STEAM_EXPORT_FLUIDS)
            .modelProperty(IS_FORMED, false)
            .colorOverlaySteamHullModel(GTCEu.id("block/overlay/machine/" + OVERLAY_FLUID_HATCH_OUTPUT),
                    GTCEu.id("block/overlay/machine/overlay_pipe"), GTCEu.id("block/overlay/machine/overlay_pipe_out_emissive"))
            .langValue("Steam Output Hatch")
            .tooltips(Component.translatable("gtceu.machine.fluid_hatch.export.tooltip"),
                    Component.translatable("gtceu.machine.steam_bus.tooltip"),
                    Component.translatable("gtceu.universal.tooltip.fluid_storage_capacity", 4000))
            .allowCoverOnFront(true)
            .register();

    public static final MultiblockMachineDefinition CLARIFIER = GTSAC_REGISTRATE
            .multiblock("clarifier", ClarifierMachine::new)
            .langValue("Clarifier")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTSACRecipeTypes.CLARIFIER_RECIPES)
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .recipeModifiers(
                    GTRecipeModifiers.ELECTRIC_OVERCLOCK.apply(OverclockingLogic.NON_PERFECT_OVERCLOCK_SUBTICK),
                    GTRecipeModifiers.BATCH_MODE)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AAACCCCCAAA", "AAABBBBBAAA", "AAABBBBBAAA", "AAABBBBBAAA")
                    .aisle("AACBBBBBCAA", "AABAAAAABAA", "AABAADAABAA", "AABAADAABAA")
                    .aisle("ACBBBBBBBCA", "ABAAAAAAABA", "ABAAAAAAABA", "ABAAADAAABA")
                    .aisle("CBBBBBBBBBC", "BAAAAAAAAAB", "BAAAADAAAAB", "BAAAADAAAAB")
                    .aisle("CBBBBBBBBBC", "BAAAAEAAAAB", "BAAAAEAAAAB", "BAAAADAAAAB")
                    .aisle("CBBBBBBBBBC", "BAAAEEEAAAB", "BDADEEEDADB", "BDDDDDDDDDB")
                    .aisle("CBBBBBBBBBC", "BAAAAEAAAAB", "BAAAAEAAAAB", "BAAAADAAAAB")
                    .aisle("CBBBBBBBBBC", "BAAAAAAAAAB", "BAAAADAAAAB", "BAAAADAAAAB")
                    .aisle("ACBBBBBBBCA", "ABAAAAAAABA", "ABAAAAAAABA", "ABAAADAAABA")
                    .aisle("AACBBBBBCAA", "AABAAAAABAA", "AABAADAABAA", "AABAADAABAA")
                    .aisle("AAACCCCCAAA", "AAABBBBBAAA", "AAABBFBBAAA", "AAABBBBBAAA")
                    .where("A", Predicates.any())
                    .where("C", Predicates.blocks(GTBlocks.FIREBOX_STEEL.get()))
                    .where("F", Predicates.controller(Predicates.blocks(definition.get())))
                    .where("B", Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS))
                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS))
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS))
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                    .where("D", Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get(), Blocks.BARRIER))
                    .where("E", Predicates.blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                    .build())
            .hasBER(true)
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTSubatomicCore.id("block/machines/clarifier"))
                    .andThen(b -> {
                        b.addDynamicRenderer(DynamicRenderHelper::makeRecipeFluidAreaRender);
                    })
                    .andThen(b -> {
                        b.addDynamicRenderer(() -> CustomObjDynamicMultiRenderer.makeObjRenderer(
                                GTSubatomicCore.id("obj_models/clarifier_arms.obj"),
                                GTCEu.id("textures/block/casings/pipe/machine_casing_pipe_steel.png"),
                                true));
                    }))
            .register();

    public static final MultiblockMachineDefinition NEUTRALIZATION_TANK = GTSAC_REGISTRATE
            .multiblock("neutralization_tank", NeutralizationTankMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeType(GTSACRecipeTypes.NEUTRALIZATION_TANK_RECIPES)
            .appearanceBlock(GTBlocks.CASING_STEEL_SOLID)
            .recipeModifiers(
                    GTRecipeModifiers.ELECTRIC_OVERCLOCK.apply(OverclockingLogic.NON_PERFECT_OVERCLOCK_SUBTICK),
                    GTRecipeModifiers.BATCH_MODE)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("AAAAABAAABAAAAA", "AAAAABAAABAAAAA", "AAAAABAAABAAAAA", "AAAAACCCCCAAAAA",
                            "AAAAADDDDDAAAAA", "AAAAADEEEDAAAAA", "AAAAADEEEDAAAAA", "AAAAADEEEDAAAAA",
                            "AAAAADDDDDAAAAA", "AAAAAAABAAAAAAA")
                    .aisle("AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAACCDDDDDCCAAA",
                            "AAADDAAFAADDAAA", "AAADDAAFAADDAAA", "AAADDAAFAADDAAA", "AAADDAAAAADDAAA",
                            "AAADDAAAAADDAAA", "AAAAAAABAAAAAAA")
                    .aisle("AABAAAAAAAAABAA", "AABAAAAAAAAABAA", "AABAAAAAAAAABAA", "AACDDDDDDDDDCAA",
                            "AADAAAAAAAAADAA", "AADAAAAAAAAADAA", "AADAAAAFAAAADAA", "AADAAAAAAAAADAA",
                            "AADAAAAAAAAADAA", "AAAAAAABAAAAAAA")
                    .aisle("AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "ACDDDDDDDDDDDCA",
                            "ADAAAAAAAAAAADA", "ADAAAAAAAAAAADA", "ADAFAAAFAAAFADA", "ADAAAAAAAAAAADA",
                            "ADAAAAAAAAAAADA", "AAAAAAABAAAAAAA")
                    .aisle("AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "ACDDDDDDDDDDDCA",
                            "ADAAFAAFAAFAADA", "ADAAFAAFAAFAADA", "ADAAFAAFAAFAADA", "ADAAAAAAAAAAADA",
                            "ADAAAAAAAAAAADA", "AAAAAAABAAAAAAA")
                    .aisle("BAAAAAAAAAAAAAB", "BAAAAAAAAAAAAAB", "BAAAAAAAAAAAAAB", "CDDDDDDDDDDDDDC",
                            "DAAAAAAAAAAAAAD", "DAAAAAAAAAAAAAD", "DAAAAFAFAFAAAAD", "DAAAAAAAAAAAAAD",
                            "DAAAAAAAAAAAAAD", "AAAAAAABAAAAAAA")
                    .aisle("AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "CDDDDDDDDDDDDDC",
                            "DAAAAAFAFAAAAAD", "EAAAAAFAFAAAAAE", "EAAAAAFFFAAAAAE", "EAAAAAAAAAAAAAE",
                            "DAAAAAAAAAAAAAD", "AAAAAAABAAAAAAA")
                    .aisle("AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "CDDDDDDDDDDDDDC",
                            "DFAAFAAGAAFAAFD", "EFAAFAAGAAFAAFE", "EFFFFFFGFFFFFFE", "EAAAAAAGAAAAAAE",
                            "DAAAAAAGAAAAAAD", "BBBBBBBGBBBBBBB")
                    .aisle("AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "CDDDDDDDDDDDDDC",
                            "DAAAAAFAFAAAAAD", "EAAAAAFAFAAAAAE", "EAAAAAFFFAAAAAE", "EAAAAAAAAAAAAAE",
                            "DAAAAAAAAAAAAAD", "AAAAAAABAAAAAAA")
                    .aisle("BAAAAAAAAAAAAAB", "BAAAAAAAAAAAAAB", "BAAAAAAAAAAAAAB", "CDDDDDDDDDDDDDC",
                            "DAAAAAAAAAAAAAD", "DAAAAAAAAAAAAAD", "DAAAAFAFAFAAAAD", "DAAAAAAAAAAAAAD",
                            "DAAAAAAAAAAAAAD", "AAAAAAABAAAAAAA")
                    .aisle("AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "ACDDDDDDDDDDDCA",
                            "ADAAFAAFAAFAADA", "ADAAFAAFAAFAADA", "ADAAFAAFAAFAADA", "ADAAAAAAAAAAADA",
                            "ADAAAAAAAAAAADA", "AAAAAAABAAAAAAA")
                    .aisle("AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "ACDDDDDDDDDDDCA",
                            "ADAAAAAAAAAAADA", "ADAAAAAAAAAAADA", "ADAFAAAFAAAFADA", "ADAAAAAAAAAAADA",
                            "ADAAAAAAAAAAADA", "AAAAAAABAAAAAAA")
                    .aisle("AABAAAAAAAAABAA", "AABAAAAAAAAABAA", "AABAAAAAAAAABAA", "AACDDDDDDDDDCAA",
                            "AADAAAAAAAAADAA", "AADAAAAAAAAADAA", "AADAAAAFAAAADAA", "AADAAAAAAAAADAA",
                            "AADAAAAAAAAADAA", "AAAAAAABAAAAAAA")
                    .aisle("AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAAAAAAAAAAAAAA", "AAACCDDDDDCCAAA",
                            "AAADDAAFAADDAAA", "AAADDAAFAADDAAA", "AAADDAAFAADDAAA", "AAADDAAAAADDAAA",
                            "AAADDAAAAADDAAA", "AAAAAAABAAAAAAA")
                    .aisle("AAAAABAAABAAAAA", "AAAAABAAABAAAAA", "AAAAABAAABAAAAA", "AAAAACCCCCAAAAA",
                            "AAAAADDIDDAAAAA", "AAAAADEEEDAAAAA", "AAAAADEEEDAAAAA", "AAAAADEEEDAAAAA",
                            "AAAAADDDDDAAAAA", "AAAAAAABAAAAAAA")
                    .where("A", Predicates.any())
                    .where("B",
                            Predicates.blocks(ChemicalHelper.getBlock(TagPrefix.frameGt,
                                    GTCEuAPI.materialManager.getMaterial("gtceu:steel"))))
                    .where("C", Predicates.blocks(GTBlocks.FIREBOX_STEEL.get()))
                    .where("D", Predicates.blocks(GTBlocks.CASING_STEEL_SOLID.get())
                            .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS))
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS))
                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS))
                            .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS))
                            .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2))
                            .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                    .where("E", Predicates.blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
                    .where("F", Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                    .where("G", Predicates.blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                    .where("I", Predicates.controller(Predicates.blocks(definition.get())))
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTCEu.id("block/machines/chemical_bath"))
                    .andThen(b -> b.addDynamicRenderer(DynamicRenderHelper::makeRecipeFluidAreaRender)))
            .register();

    public static final MultiblockMachineDefinition STEEL_AUGMENTED_PBF = GTSAC_REGISTRATE
            .multiblock("steel_augmented_pbf", SteelAugmentedPBFMachine::new)
            .langValue("Steel-Augmented Bricked (Up) Blast Furnace")
            .rotationState(RotationState.ALL)
            .recipeType(GTRecipeTypes.PRIMITIVE_BLAST_FURNACE_RECIPES)
            .appearanceBlock(GTBlocks.CASING_PRIMITIVE_BRICKS)
            .recipeModifiers(true, SteelAugmentedPBFMachine::recipeModifier, GTRecipeModifiers.BATCH_MODE)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("FBF", "BBB", "BBB", " B ", " B ")
                    .aisle("FBF", "B B", "B B", "B B", "B B")
                    .aisle("FBF", "B@B", "BBB", " B ", " B ")
                    .where(" ", Predicates.any())
                    .where("F", Predicates.blocks(GTBlocks.FIREBOX_STEEL.get()))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .where("B", Predicates.blocks(GTBlocks.CASING_PRIMITIVE_BRICKS.get()).setMinGlobalLimited(20)
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS))
                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS)))
                    .build())
            .hasBER(true)
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/solid/machine_primitive_bricks"),
                    GTCEu.id("block/multiblock/primitive_blast_furnace"))
                    .andThen(b -> b.addDynamicRenderer(
                            () -> DynamicRenderHelper.makeBoilerPartRender(
                                    BoilerFireboxType.STEEL_FIREBOX, GTBlocks.CASING_PRIMITIVE_BRICKS)))
                    .andThen(b -> b.addDynamicRenderer(DynamicRenderHelper::createPBFLavaRender)))
            .register();

    public static final MultiblockMachineDefinition LARGE_PRIMITIVE_SMELTER = GTSAC_REGISTRATE
            .multiblock("large_primitive_smelter", LargePrimitiveSmelterMachine::new)
            .langValue("Large Primitive Smelter")
            .rotationState(RotationState.ALL)
            .recipeTypes(GTSACRecipeTypes.PRIMITIVE_SMELTER_RECIPES)
            .appearanceBlock(() -> Blocks.NETHER_BRICKS)
            .recipeModifiers(true, LargePrimitiveSmelterMachine::recipeModifier, GTRecipeModifiers.BATCH_MODE)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("BBB", "BFB", "SSS")
                    .aisle("BBB", "F F", "B B")
                    .aisle("BBB", "B@B", "SSS")
                    .where(" ", Predicates.any())
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .where("F", Predicates.blocks(GTSACBlocks.PRIMITIVE_BRICK_FENCE.get()))
                    .where("S", Predicates.blocks(GTSACBlocks.PRIMITIVE_BRICK_STAIRS.get()))
                    .where("B", Predicates.blocks(GTSACBlocks.PRIMITIVE_BRICKS.get()).setMinGlobalLimited(12)
                            .or(Predicates.abilities(PartAbility.IMPORT_ITEMS))
                            .or(Predicates.abilities(PartAbility.EXPORT_ITEMS))
                            .or(Predicates.machines(GTSACMachines.PRIMITIVE_FUEL_HATCH).setExactLimit(1)))
                    .build())
            .model(createWorkableCasingMachineModel(
                    GTSubatomicCore.id("block/primitive_bricks"),
                    GTCEu.id("block/machines/electric_furnace")))
            .register();

    public static final MultiblockMachineDefinition LARGE_BRONZE_FIREBOX = GTSAC_REGISTRATE
            .multiblock("large_bronze_firebox", LargeBronzeFireboxMachine::new)
            .langValue("Large Bronze Firebox (WIP)")
            .rotationState(RotationState.ALL)
            .recipeType(GTSACRecipeTypes.PRIMITIVE_SMELTER_RECIPES)
            .appearanceBlock(GTBlocks.FIREBOX_BRONZE)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("BBBBB", "BFFFB", "BBBBB")
                    .aisle("BBBBB", "FBBBF", "B   B")
                    .aisle("BBBBB", "FBBBF", "B   B")
                    .aisle("BBBBB", "FBBBF", "B   B")
                    .aisle("BBBBB", "BF@FB", "BBBBB")
                    .where(" ", Predicates.any())
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .where("F", Predicates.blocks(GTBlocks.FIREBOX_BRONZE.get()))
                    .where("B", Predicates.blocks(GTBlocks.CASING_BRONZE_BRICKS.get()).setMinGlobalLimited(50)
                            .or(Predicates.machines(GTSACMachines.PRIMITIVE_FUEL_HATCH).setExactLimit(1)))
                    .build())
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/solid/machine_casing_bronze_plated_bricks"),
                    GTCEu.id("block/multiblock/primitive_blast_furnace")))
            .register();

    public static final MultiblockMachineDefinition LARGE_CRUCIBLE = GTSAC_REGISTRATE
            .multiblock("large_crucible", LargeBronzeFireboxMachine::new)
            .langValue("Large Crucible (WIP)")
            .rotationState(RotationState.ALL)
            .recipeType(GTSACRecipeTypes.PRIMITIVE_SMELTER_RECIPES)
            .appearanceBlock(GTBlocks.CASING_PRIMITIVE_BRICKS)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("BBBBB", "BFFFB", "BBBBB")
                    .aisle("BBBBB", "F   F", "BBBBB")
                    .aisle("BBBBB", "F   F", "BBBBB")
                    .aisle("BBBBB", "F   F", "BBBBB")
                    .aisle("BBBBB", "BF@FB", "BBBBB")
                    .where(" ", Predicates.any())
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .where("F", Predicates.blocks(GTBlocks.FIREBOX_BRONZE.get()))
                    .where("B", Predicates.blocks(GTBlocks.CASING_BRONZE_BRICKS.get()).setMinGlobalLimited(50)
                            .or(Predicates.machines(GTSACMachines.PRIMITIVE_FUEL_HATCH).setExactLimit(1)))
                    .build())
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/solid/machine_casing_bronze_plated_bricks"),
                    GTCEu.id("block/multiblock/primitive_blast_furnace")))
            .register();

    public static final MachineDefinition PRIMITIVE_FUEL_HATCH = GTSAC_REGISTRATE
            .machine("primitive_fuel_hatch", PrimitiveFuelHatchPartMachine::new)
            .langValue("§7Primitive Fuel Hatch")
            .tooltips(Component.literal("Low-Tech Fuel Input for Multiblocks"))
            .rotationState(RotationState.ALL)
            .modelProperty(IS_FORMED, false)
            .model(GTSACMachineModels.createOverlayCasingMachineModel("primitive_fuel_hatch", GTSubatomicCore.id("block/casings/primitive_bricks")))
            .tier(GTValues.ULV)
            .register();

    public static final MachineDefinition PRIMITIVE_ITEM_IMPORT_HATCH = GTSAC_REGISTRATE
            .machine("primitive_input_bus", (holder) ->
                    new ItemBusPartMachine(holder, GTValues.ULV, IO.IN))
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.IMPORT_ITEMS)
            .modelProperty(IS_FORMED, false)
            .colorOverlayTieredHullModel(GTCEu.id("block/overlay/machine/" + OVERLAY_ITEM_HATCH_INPUT),
                    GTCEu.id("block/overlay/machine/overlay_pipe"), GTCEu.id("block/overlay/machine/overlay_pipe_in_emissive"))
            .langValue("§7Primitive Input Bus")
            .tooltips(Component.literal("Low-Tech Item Input for Multiblocks"),
                    Component.translatable("gtceu.universal.tooltip.item_storage_capacity",
                            1))
            .allowCoverOnFront(true)
            .register();
    public static final MachineDefinition PRIMITIVE_ITEM_EXPORT_HATCH = GTSAC_REGISTRATE
            .machine("primitive_output_bus", (holder) ->
                    new ItemBusPartMachine(holder, GTValues.ULV, IO.OUT))
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.EXPORT_ITEMS)
            .modelProperty(IS_FORMED, false)
            .colorOverlayTieredHullModel(GTCEu.id("block/overlay/machine/" + OVERLAY_ITEM_HATCH_OUTPUT),
                    GTCEu.id("block/overlay/machine/overlay_pipe"), GTCEu.id("block/overlay/machine/overlay_pipe_out_emissive"))
            .langValue("§7Primitive Output Bus")
            .tooltips(Component.literal("Low-Tech Item Output for Multiblocks"),
                    Component.translatable("gtceu.universal.tooltip.item_storage_capacity",
                            1))
            .allowCoverOnFront(true)
            .register();


}
