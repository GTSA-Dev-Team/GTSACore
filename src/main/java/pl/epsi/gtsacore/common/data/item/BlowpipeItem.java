package pl.epsi.gtsacore.common.data.item;

import com.gregtechceu.gtceu.common.data.GTMaterials;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import pl.epsi.gtsacore.common.data.block.casting.CrucibleAssemblyBlockEntity;

import java.util.concurrent.atomic.AtomicBoolean;

public class BlowpipeItem extends Item {
    private static final String HAS_GLASS_TAG = "HasGlass";

    public BlowpipeItem(Properties properties) {
        super(properties);
    }

    public static boolean hasGlass(ItemStack stack) {
        if (!stack.hasTag()) setGlass(stack, false);
        return stack.getOrCreateTag().getBoolean(HAS_GLASS_TAG);
    }

    public static void setGlass(ItemStack stack, boolean hasGlass) {
        stack.getOrCreateTag().putBoolean(HAS_GLASS_TAG, hasGlass);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (!level.isClientSide()) {
            ItemStack pipe = player.getItemInHand(usedHand);
            if (hasGlass(pipe)) {
                player.startUsingItem(usedHand);
            }
        }

        return InteractionResultHolder.fail(player.getItemInHand(usedHand));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockEntity be = level.getBlockEntity(pos);
        ItemStack iStack = context.getItemInHand();
        AtomicBoolean took = new AtomicBoolean(false);

        if (be == null) return super.useOn(context);

        if (be instanceof CrucibleAssemblyBlockEntity cbe) {
            if (cbe.getTopFluidStack() != null) {
                if (cbe.getTopFluidStack().getFluid().isSame(GTMaterials.Glass.getFluid()) && cbe.getTopFluidStack().getAmount() >= 144) {
                    cbe.removeFluid(GTMaterials.Glass.getFluid(), 144);
                    setGlass(iStack, true);
                    took.set(true);
                }
            }

        } else {
            LazyOptional<IFluidHandler> fluidHandler = be.getCapability(ForgeCapabilities.FLUID_HANDLER);
            fluidHandler.ifPresent(h -> {
                for (int i = 0; i < h.getTanks(); i++) {
                    FluidStack fStack = h.getFluidInTank(i);
                    if (fStack.getFluid().isSame(GTMaterials.Glass.getFluid()) && fStack.getAmount() >= 144) {
                        h.drain(GTMaterials.Glass.getFluid(144), IFluidHandler.FluidAction.EXECUTE);
                        setGlass(iStack, true);
                        took.set(true);
                        break;
                    }
                }
            });
        }


        return took.get() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }


    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (!(livingEntity instanceof Player player)) return;
        int durationheld = getUseDuration(stack) - timeCharged;
        if (durationheld < 30) return;

        giveGlassAndResetPipe(player, stack);

        if (player.hasEffect(MobEffects.CONFUSION)) {
            int dur = player.getEffect(MobEffects.CONFUSION).getDuration();
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, dur * 4));
        } else {
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100));
        }

    }

    private void giveGlassAndResetPipe(Player player, ItemStack stack) {
        player.getInventory().add(new ItemStack(Items.GLASS, 1));
        setGlass(stack, false);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return super.shouldCauseReequipAnimation(oldStack, newStack, slotChanged) && !(oldStack.getDamageValue() <= newStack.getDamageValue());
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
        if (!level.isClientSide()) {
            if (hasGlass(stack)) {
                stack.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(InteractionHand.MAIN_HAND));
            }
        }
    }



    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
        super.inventoryTick(stack, level, entity, slotId, isSelected);

    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BRUSH;
    }
}
