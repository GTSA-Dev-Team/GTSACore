package pl.epsi.gtsacore.common.machine;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IDisplayUIMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

public class WorkableFueledMultiblockMachine extends WorkableMultiblockMachine implements IDisplayUIMachine {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            WorkableFueledMultiblockMachine.class, WorkableMultiblockMachine.MANAGED_FIELD_HOLDER);



    public WorkableFueledMultiblockMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }
}
