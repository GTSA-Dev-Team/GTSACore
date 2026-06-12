package pl.epsi.gtsacore.common.machine.part;


import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;

import com.gregtechceu.gtceu.common.machine.multiblock.part.ItemBusPartMachine;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class PrimitiveFuelHatchPartMachine extends ItemBusPartMachine {

    public PrimitiveFuelHatchPartMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.ULV, IO.IN, new ItemStackHandler(1));
    }


    @Override
    protected @NotNull NotifiableItemStackHandler createInventory(Object @NotNull ... args) {
        return super.createInventory(args).setFilter(itemStack -> (itemStack.is(Items.COAL) || itemStack.getBurnTime(RecipeType.SMELTING) > 0));
    }

    @Override
    public boolean swapIO() {
        return false;
    }

    @Override
    public boolean canShared() {
        return false;
    }
}
