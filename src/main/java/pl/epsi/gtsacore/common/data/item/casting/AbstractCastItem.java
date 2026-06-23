package pl.epsi.gtsacore.common.data.item.casting;

import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import pl.epsi.gtsacore.GTSubatomicCore;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractCastItem extends Item implements ICastingTableable {

    private static final HashMap<Integer, Integer> REGULAR_CACHE = new HashMap<>();
    private static final HashMap<Integer, Integer> SLIGHTLY_CRACKED_CACHE = new HashMap<>();
    private static final HashMap<Integer, Integer> SEVERELY_CRACKED_CACHE = new HashMap<>();

    private static final String CRACKED_TAG = "Cracked";

    static {
        RenderSystem.recordRenderCall(() -> {
            var texManager = Minecraft.getInstance().getTextureManager();
            REGULAR_CACHE.put(0, texManager.getTexture(GTSubatomicCore.id("textures/block/ceramic_block.png")).getId());
            SLIGHTLY_CRACKED_CACHE.put(0, texManager.getTexture(GTSubatomicCore.id("textures/block/slightly_cracked_ceramic_block.png")).getId());
            SEVERELY_CRACKED_CACHE.put(0, texManager.getTexture(GTSubatomicCore.id("textures/block/severely_cracked_ceramic_block.png")).getId());
        });
    }

    @Getter
    private final RenderInfo renderInfo;

    public AbstractCastItem(Properties properties, RenderInfo renderInfo) {
        super(properties);
        this.renderInfo = renderInfo;
    }

    @Override
    public Map<Integer, Integer> getTextures(ItemStack stack) {
        int cracked = getCracked(stack);
        return switch (cracked) {
            case 1 -> SLIGHTLY_CRACKED_CACHE;
            case 2-> SEVERELY_CRACKED_CACHE;
            default -> REGULAR_CACHE;
        };
    }

    public static int getCracked(ItemStack stack) {
        if (!stack.hasTag()) setCracked(stack, 0);
        return stack.getOrCreateTag().getInt(CRACKED_TAG);
    }

    public static String getCrackedName(ItemStack stack) {
        int cracked = getCracked(stack);
        return switch (cracked) {
            case 1 -> "Slightly Cracked";
            case 2 -> "Severely Cracked";
            default -> "Intact";
        };
    }

    public static void setCracked(ItemStack stack, int cracked) {
        stack.getOrCreateTag().putInt(CRACKED_TAG, cracked);
    }

    public static int onUsed(RandomSource random, ItemStack stack) {
        int current = getCracked(stack);
        double percent = current == 0 ? 0.1 : current == 1 ? 0.2 : 0.35;
        if (random.nextDouble() < percent) {
            if (current == 2) return 1;
            setCracked(stack, current + 1);
            return 2;
        }
        return 0;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(super.getName(stack).getString() + " [" + getCrackedName(stack) + "]");
    }

}
