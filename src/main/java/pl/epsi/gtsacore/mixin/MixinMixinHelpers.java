package pl.epsi.gtsacore.mixin;

import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.core.MixinHelpers;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Debug(export = true)
@Mixin(MixinHelpers.class)
public class MixinMixinHelpers {

    @Dynamic("Targeting synthetic lambda in generateGTDynamicLoot")
    @WrapOperation(
            method = "lambda$generateGTDynamicLoot$33(Ljava/util/Map;Lcom/gregtechceu/gtceu/api/data/chemical/material/Material;Lcom/tterrag/registrate/util/entry/BlockEntry;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/data/loot/packs/VanillaBlockLoot;createSingleItemTable(Lnet/minecraft/world/level/ItemLike;Lnet/minecraft/world/level/storage/loot/providers/number/NumberProvider;)Lnet/minecraft/world/level/storage/loot/LootTable$Builder;"
            )
    )
    private static LootTable.Builder modifyDynamicLootTable(
            VanillaBlockLoot blockLoot,
            ItemLike originalItem,
            NumberProvider originalCount,
            Operation<LootTable.Builder> original,
            @Local(argsOnly = true) Material material) {

        ItemLike newItem = ChemicalHelper.get(TagPrefix.surfaceRock, material).getItem();
        NumberProvider newCount = ConstantValue.exactly(1);

        return original.call(blockLoot, newItem, newCount);
    }

}
