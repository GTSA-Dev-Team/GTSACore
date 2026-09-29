package pl.epsi.gtsacore.common.data;

import com.gregtechceu.gtceu.api.data.chemical.material.event.PostMaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.ToolProperty;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.api.item.tool.ToolHelper;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import pl.epsi.gtsacore.common.data.block.IncompleteCraftingTableBlock;

@Mod.EventBusSubscriber
public class GTSACEvents {

    @SubscribeEvent
    public static void rightClicked(PlayerInteractEvent.RightClickBlock event) {
        ItemStack clickedItem = event.getItemStack();
        BlockState clickedBlock = event.getLevel().getBlockState(event.getPos());
        BlockState blockAbove = event.getLevel().getBlockState(event.getPos().above());

        if (ToolHelper.is(clickedItem, GTToolType.KNIFE) && clickedBlock.is(BlockTags.LOGS) && blockAbove.is(Blocks.AIR)) {
            event.getLevel().setBlock(event.getPos(), GTSACBlocks.INCOMPLETE_CRAFTING_TABLE.getDefaultState(), 3);
            clickedItem.hurtAndBreak(5, event.getEntity(), p -> p.broadcastBreakEvent(event.getHand()));
        }
    }


}
