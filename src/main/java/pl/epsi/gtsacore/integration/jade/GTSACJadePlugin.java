package pl.epsi.gtsacore.integration.jade;

import pl.epsi.gtsacore.common.data.block.casting.CastingTableBlock;
import pl.epsi.gtsacore.common.data.block.casting.CastingTableBlockEntity;
import pl.epsi.gtsacore.common.data.block.casting.CrucibleAssemblyBlock;
import pl.epsi.gtsacore.common.data.block.casting.CrucibleAssemblyBlockEntity;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class GTSACJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new CrucibleAssemblyBlockProvider(), CrucibleAssemblyBlockEntity.class);
        registration.registerBlockDataProvider(new CastingTableBlockProvider(), CastingTableBlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new CrucibleAssemblyBlockProvider(), CrucibleAssemblyBlock.class);
        registration.registerBlockComponent(new CastingTableBlockProvider(), CastingTableBlock.class);
    }

}
