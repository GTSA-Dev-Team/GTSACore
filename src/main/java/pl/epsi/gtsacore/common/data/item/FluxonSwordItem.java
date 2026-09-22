package pl.epsi.gtsacore.common.data.item;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.common.data.GTMachines;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Random;

public class FluxonSwordItem extends SwordItem {

    public FluxonSwordItem(Tier tier, int attackDamageModifier, float attackSpeedModifier, Properties properties) {
        super(tier, attackDamageModifier * 2, attackSpeedModifier * 2, properties);
    }

    @Override
    public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (!attacker.level().isClientSide()) {
            int tier = (int) (Math.random() * 13) + 1;
            System.out.println(tier);
            BlockState machineState = GTMachines.BENDER[tier].defaultBlockState();

            FallingBlockEntity fallingMachine = FallingBlockEntity.fall(
                    attacker.level(),
                    target.getOnPos().above(20),
                    machineState);

            attacker.level().addFreshEntity(fallingMachine);
        }

        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide()) {
            int tier = (int) (Math.random() * 13) + 1;
            System.out.println(tier);
            BlockState machineState = GTMachines.BENDER[tier].defaultBlockState();

            FallingBlockEntity fallingMachine = FallingBlockEntity.fall(
                    level,
                    player.getOnPos().above(),
                    machineState);

            fallingMachine.setDeltaMovement(player.getLookAngle().multiply(1, 1, 1));

            level.addFreshEntity(fallingMachine);
        }

        return InteractionResultHolder.success(player.getItemInHand(usedHand));
    }
}
