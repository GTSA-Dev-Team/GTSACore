package pl.epsi.gtsacore.common.data;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderHelper;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;

import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.common.machine.multiblock.ClarifierMachine;
import pl.epsi.gtsacore.common.machine.multiblock.NeutralizationTankMachine;

import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.createWorkableCasingMachineModel;
import static pl.epsi.gtsacore.GTSubatomicCore.GTSAC_REGISTRATE;

public class GTSACMachines {

    public static void init() {};

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
                    .where("D", Predicates.blocks(GTBlocks.CASING_STEEL_PIPE.get()))
                    .where("E", Predicates.blocks(GTBlocks.CASING_STEEL_GEARBOX.get()))
                    .build())
            .modelProperty(GTMachineModelProperties.RECIPE_LOGIC_STATUS, RecipeLogic.Status.IDLE)
            .model(createWorkableCasingMachineModel(
                    GTCEu.id("block/casings/solid/machine_casing_solid_steel"),
                    GTSubatomicCore.id("block/machines/clarifier")))
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
                    GTSubatomicCore.id("block/machines/neutralization_tank")))
            .register();
}
