package pl.epsi.gtsacore.mixin;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.core.MixinHelpers;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(MixinHelpers.class)
public class MixinMixinHelpers {

    @ModifyArg(
            method = "lambda$generateGTDynamicLoot$33",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/data/loot/packs/VanillaBlockLoot.createSingleItemTable (Lnet/minecraft/world/level/ItemLike;Lnet/minecraft/world/level/storage/loot/providers/number/NumberProvider;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;"
            ),
            index = 0,
            remap = false
    )
    private static ItemLike modifyLootItem(ItemLike original, @Local(argsOnly = true) Material material) {
        return ChemicalHelper.get(TagPrefix.surfaceRock, material).getItem();
    }

    @ModifyArg(
            method = "lambda$generateGTDynamicLoot$33",
            at = @At(
                    value = "INVOKE",
                    target = "net/minecraft/data/loot/packs/VanillaBlockLoot.createSingleItemTable (Lnet/minecraft/world/level/ItemLike;Lnet/minecraft/world/level/storage/loot/providers/number/NumberProvider;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;"
            ),
            index = 1,
            remap = false
    )
    private static NumberProvider modifyLootCount(NumberProvider par2) {
        return ConstantValue.exactly(1);
    }

}
