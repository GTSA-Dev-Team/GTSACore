package pl.epsi.gtsacore.common.data.item.casting;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import org.joml.Matrix4f;
import pl.epsi.gtsacore.GTSubatomicCore;

import java.util.HashMap;
import java.util.Map;

public class IronBloomItem extends BlockItem implements ICastingTableable {

    @Getter
    private final RenderInfo renderInfo;

    private final Map<Integer, Integer> textures = new HashMap<>();

    public IronBloomItem(Block block, Item.Properties properties) {
        super(block, properties);
        this.renderInfo = RenderInfo.of(GTSubatomicCore.id("obj_models/iron_bloom.obj"),
                new Matrix4f().translate(0.5f, 0.9375f, 0.5f).scale(0.75f, 0.75f, 0.75f), new AABB(0, 0, 0, 0, 0, 0),
                null);

        RenderSystem.recordRenderCall(() -> {
            textures.put(0, Minecraft.getInstance().getTextureManager().getTexture(GTSubatomicCore.id("textures/block/iron_bloom.png")).getId());
        });
    }

    @Override
    public Map<Integer, Integer> getTextures(ItemStack stack) {
        return textures;
    }

}
