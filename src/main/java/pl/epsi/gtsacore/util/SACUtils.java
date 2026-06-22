package pl.epsi.gtsacore.util;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.ChemicalHelper;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.stack.MaterialStack;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SACUtils {

    public static final ResourceLocation EMPTY_IDENTIFIER = ResourceLocation.fromNamespaceAndPath("minecraft", "empty");

    public static InputStream asInputStream(ResourceLocation rl) {
        return SACUtils.class.getResourceAsStream("/assets/" + rl.getNamespace() + "/" + rl.getPath());
    }

    public static String readFile(InputStream in) throws IOException {
        if (in == null) throw new IllegalArgumentException("Input stream is null when retrieving file!");

        final StringBuilder sBuffer = new StringBuilder();
        final BufferedReader br = new BufferedReader(new InputStreamReader(in));
        final char[] buffer = new char[1024];

        int cnt;
        while ((cnt = br.read(buffer, 0, buffer.length)) > -1) {
            sBuffer.append(buffer, 0, cnt);
        }
        br.close();
        in.close();
        return sBuffer.toString();
    }

    public static VoxelShape rotateShape(VoxelShape shape) {
        VoxelShape[] buffer = new VoxelShape[] {
                shape,
                Shapes.empty()
        };

        buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            buffer[1] = Shapes.or(buffer[1], Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX));
        });

        return buffer[1];
    }

    public static @Nullable FluidStack getFluidForItem(ItemStack item) {
        MaterialStack materialStack = ChemicalHelper.getMaterialStack(item);
        if (materialStack.isEmpty()) return null;

        Material material = materialStack.material();
        long amount = (int) (144 * materialStack.amount() / GTValues.M);

        return material.getFluid((int) amount);
    }

}