package pl.epsi.gtsacore.api.condition;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeCondition;
import com.gregtechceu.gtceu.api.recipe.condition.RecipeConditionType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import pl.epsi.gtsacore.GTSubatomicCore;
import pl.epsi.gtsacore.api.machine.feature.IHeatReceiver;

public class HeatCondition extends RecipeCondition<HeatCondition> {

    public static final Codec<HeatCondition> CODEC = RecordCodecBuilder.create(instance -> RecipeCondition.isReverse(instance)
            .and(Codec.INT.fieldOf("heat").forGetter(val -> val.heat)
    ).apply(instance, HeatCondition::new));

    public int heat;

    public HeatCondition(boolean isReverse, int heat) {
        this.isReverse = isReverse;
        this.heat = heat;
    }

    public HeatCondition(int heat) {
        this(false, heat);
    }

    public HeatCondition() {
        this(false, 0);
    }

    @Override
    public RecipeConditionType<HeatCondition> getType() {
        return GTSubatomicCore.HEAT_CONDITION;
    }

    @Override
    public Component getTooltips() {
        return Component.literal(Component.translatable("gtsac.condition.heat").getString().formatted(heat/10));
    }

    @Override
    protected boolean testCondition(@NotNull GTRecipe gtRecipe, @NotNull RecipeLogic recipeLogic) {
        if (!(recipeLogic.getMachine() instanceof IHeatReceiver heatMachine)) return false;
        if (heatMachine.getHeatSource() == null) return false;
        return heatMachine.getHeatSource().getHeatLevel() > heat;
    }

    @Override
    public HeatCondition createTemplate() {
        return new HeatCondition(0);
    }
}
