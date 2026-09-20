package pl.epsi.gtsacore.common.machine.multiblock;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.BlockPattern;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.api.pattern.TraceabilityPredicate;
import com.gregtechceu.gtceu.api.recipe.ActionResult;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;
import com.mojang.datafixers.util.Either;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import pl.epsi.gtsacore.common.data.GTSACBlocks;
import pl.epsi.gtsacore.common.data.GTSACRecipeTypes;
import pl.epsi.gtsacore.common.data.block.casting.CastingTableBlockEntity;
import pl.epsi.gtsacore.common.data.item.GTSACItems;
import pl.epsi.gtsacore.common.data.item.casting.AbstractCastItem;
import pl.epsi.gtsacore.common.machine.WorkablePrimitiveMultiblockMachine;
import pl.epsi.gtsacore.util.SACUtils;

import java.util.*;

import static com.gregtechceu.gtceu.api.pattern.Predicates.blocks;

public class StrandCasterMachine extends WorkablePrimitiveMultiblockMachine {
    private static final Map<AbstractCastItem, Integer> moldAmountMap = Map.of(
            GTSACItems.INGOT_MOLD.get(), 144,
            GTSACItems.PLATE_MOLD.get(), 72,
            GTSACItems.ROD_MOLD.get(), 72
    );

    private static final Map<AbstractCastItem, TagPrefix> moldTagPrefixMap = Map.of(
            GTSACItems.INGOT_MOLD.get(), TagPrefix.ingot,
            GTSACItems.PLATE_MOLD.get(), TagPrefix.plate,
            GTSACItems.ROD_MOLD.get(), TagPrefix.rod
    );

    private Collection<CastingTableBlockEntity> castingTables = new HashSet<>();

    public StrandCasterMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    protected InteractionResult onHardHammerClick(Player playerIn, InteractionHand hand, Direction gridSide, BlockHitResult hitResult) {
        System.out.println(castingTables.size());
        return super.onHardHammerClick(playerIn, hand, gridSide, hitResult);
    }

    @Override
    protected @NotNull RecipeLogic createRecipeLogic(Object @NotNull ... args) {
        return new StrandCasterLogic(this);
    }

    @Override
    public @NotNull StrandCasterLogic getRecipeLogic() {
        return (StrandCasterLogic) super.getRecipeLogic();
    }

    @Override
    public void onStructureFormed() {
        /*castingTableCount = 0;

        Object2IntOpenHashMap<SimplePredicate> blockMap = this.getMultiblockState().getGlobalCount();
        System.out.println(blockMap.size());

        for (SimplePredicate predicate : blockMap.keySet()) {
            System.out.println("looking next");
            if (!predicate.getCandidates().isEmpty()) {
                System.out.println(predicate.getCandidates().get(0).getItem().getDescription());
                System.out.println(predicate.getCandidates().get(0).getCount());
            }

            List<Item> predItems = predicate.getCandidates().stream().map(ItemStack::getItem).collect(Collectors.toUnmodifiableList());
            System.out.println(predItems);

            if (predItems.contains(GTSACBlocks.CASTING_TABLE.asItem())) {
                System.out.println("has casting tabel");
                castingTableCount++;
                if (predicate.test(this.getMultiblockState())) {
                    System.out.println("tested");
                    castingTableCount++;
                }
            }

        }

        System.out.println("casting tables: " + castingTableCount);*/

        super.onStructureFormed();

        this.castingTables = null;

        Set<CastingTableBlockEntity> tables = this.getMultiblockState().getMatchContext().getOrCreate("castingTable", Sets::newHashSet);

        this.castingTables = ImmutableSet.copyOf(tables);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        this.castingTables = null;
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        var moldMap = getMoldMap();
        textList.add(Component.literal(String.valueOf(castingTables.size())));

        for (AbstractCastItem castItem : moldMap.keySet()) {
            String moldStr = castItem.getDescriptionId() + ": ";
            String countStr = String.valueOf(moldMap.get(castItem));
            textList.add(Component.literal(moldStr + countStr));
        }


        super.addDisplayText(textList);

    }

    protected @NotNull TraceabilityPredicate innerPredicate() {
        return new TraceabilityPredicate(blockWorldState -> {
            Set<CastingTableBlockEntity> targets = blockWorldState.getMatchContext().getOrCreate("castingTable", Sets::newHashSet);
            BlockEntity blockEntity = blockWorldState.getTileEntity();

            if (blockEntity instanceof CastingTableBlockEntity cbe) {
                targets.add(cbe);
            }

            return true;
        }, null) {
            public boolean isAny() {
                return true;
            }

            public boolean addCache() {
                return true;
            }
        };
    }

    @Override
    public BlockPattern getPattern() {
        return FactoryBlockPattern.start()
                .aisle("BOOOB", "BBBBB", " BHB ")
                .aisle("BDDDB", " C C ", " FPF ")
                .aisle("BDBDB", " C C ", " FPF ")
                .aisle("BDBDB", " C C ", " FPF ")
                .aisle("BDBDB", " C C ", " FPF ")
                .aisle("BDBDB", " C C ", " FPF ")
                .aisle("BBBBB", "BB@BB", " BHB ")
                .where(" ", Predicates.any())
                .where("@", Predicates.controller(Predicates.blocks(this.getDefinition().get())))
                .where("B", blocks(GTSACBlocks.BRONZE_PLATED_BRICKS.get()))
                .where("D", blocks(GTSACBlocks.CASING_BRONZE_DUCT.get()))
                .where("C", innerPredicate())
                .where("F", blocks(GTSACBlocks.FAUCET.get()))
                .where("P", blocks(GTBlocks.CASING_BRONZE_PIPE.get()))
                .where("H", blocks(GTSACBlocks.BRONZE_PLATED_BRICKS.get())
                        .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS)))
                .where("O", blocks(GTSACBlocks.BRONZE_PLATED_BRICKS.get())
                        .or(Predicates.abilities(PartAbility.EXPORT_ITEMS)))
                .where("P", blocks(GTBlocks.CASING_BRONZE_PIPE.get()))
                .build();
    }

    private HashMap<AbstractCastItem, Integer> getMoldMap() {
        HashMap<AbstractCastItem, Integer> moldMap = new HashMap<>();

        for (CastingTableBlockEntity table : castingTables) {
            ItemStack moldStack = table.getMoldItem();
            Item moldItem = moldStack.getItem();

            if (moldStack != ItemStack.EMPTY && moldItem instanceof AbstractCastItem castItem) {
                if (moldMap.containsKey(castItem)) {
                    moldMap.compute(castItem, (k, count) -> count + 1);
                } else {
                    moldMap.put(castItem, 1);
                }
            }
        }

        return moldMap;
    }




    private List<ItemStack> getMoldItemsOfMat(Material material) {
        ArrayList<ItemStack> itemStacks = new ArrayList<>();

        for (AbstractCastItem mold : getMoldMap().keySet()) {
            TagPrefix tagPrefix = moldTagPrefixMap.get(mold);
            ItemStack materialStack = ChemicalHelper.get(tagPrefix, material);

            if (materialStack != null) {
                itemStacks.add(new ItemStack(materialStack.getItem(), getMoldMap().get(mold)));
            }
        }

        return itemStacks;
    }

    private int getMoldCountSum() {
        return getMoldMap().keySet().stream().mapToInt(m -> getMoldMap().containsKey(m) ? getMoldMap().get(m) : 0).sum();
    }

    private int getMoldAmountSum() {
        return getMoldMap().keySet().stream().mapToInt(m -> moldAmountMap.get(m)).sum();
    }

    public class StrandCasterLogic extends RecipeLogic {

        public StrandCasterLogic(IRecipeLogicMachine machine) {
            super(machine);
        }

        @Override
        public @NotNull Iterator<GTRecipe> searchRecipe() {
            return this.machine.getRecipeType().searchRecipe(this.machine, r -> {
                return true;
            });
        }

        private GTRecipe getActualRecipeOfCastingRecipe(GTRecipe recipe) {
            if (recipe.recipeType != GTSACRecipeTypes.CASTING_RECIPES) return recipe;
            Fluid inputFluid = RecipeHelper.getInputFluids(recipe).get(0).getFluid();
            FluidStack actualFluidInput = new FluidStack(inputFluid, getMoldAmountSum());
            List<ItemStack> actualItemOutputs = getMoldItemsOfMat(ChemicalHelper.getMaterial(inputFluid));

            GTRecipe actualRecipe = GTRecipeBuilder.ofRaw().inputFluids(actualFluidInput).outputItems(actualItemOutputs).duration(6 * actualItemOutputs.size() / 2).buildRawRecipe();

            return actualRecipe;
        }

        @Override
        protected ActionResult matchRecipe(GTRecipe recipe) {
            if (recipe.recipeType != GTSACRecipeTypes.CASTING_RECIPES) return super.matchRecipe(recipe);
            return super.matchRecipe(getActualRecipeOfCastingRecipe(recipe));
        }

        @Override
        protected ActionResult handleRecipeIO(GTRecipe recipe, IO io) {
            if (recipe.recipeType != GTSACRecipeTypes.CASTING_RECIPES) return super.handleRecipeIO(recipe, io);
            Fluid inputFluid = RecipeHelper.getInputFluids(recipe).get(0).getFluid();
            FluidStack actualFluidInput = new FluidStack(inputFluid, getMoldAmountSum());
            List<ItemStack> actualItemOutputs = getMoldItemsOfMat(ChemicalHelper.getMaterial(inputFluid));

            GTRecipe actualRecipe = GTRecipeBuilder.ofRaw().inputFluids(actualFluidInput).outputItems(actualItemOutputs).duration(6 * actualItemOutputs.size() / 2).buildRawRecipe();

            return super.handleRecipeIO(actualRecipe, io);
        }
    }
}
