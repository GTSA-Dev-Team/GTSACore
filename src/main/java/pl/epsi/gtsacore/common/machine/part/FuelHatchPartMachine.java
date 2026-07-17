package pl.epsi.gtsacore.common.machine.part;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredIOPartMachine;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import pl.epsi.gtsacore.api.ingredient.fuel.NotifiableFuelHandler;

public class FuelHatchPartMachine extends TieredIOPartMachine {

    @Persisted
    public NotifiableFuelHandler fuelHandler;

    public FuelHatchPartMachine(IMachineBlockEntity holder, int tier, IO io) {
        super(holder, tier, io);
        this.fuelHandler = new NotifiableFuelHandler(this, io);
    }

    @Override
    protected InteractionResult onHardHammerClick(Player playerIn, InteractionHand hand, Direction gridSide, BlockHitResult hitResult) {
        if(isRemote()) return InteractionResult.SUCCESS;
        if(fuelHandler.addFuel(1, false)){
            playerIn.sendSystemMessage(Component.literal("Bonk! Total fuel stored: " + fuelHandler.getFuel()));
            return InteractionResult.CONSUME;
        }
        return super.onHardHammerClick(playerIn, hand, gridSide, hitResult);
    }
}
