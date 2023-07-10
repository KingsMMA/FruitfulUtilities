package dev.kingrabbit.fruitfulutilities.mixin.compatibility.sodium;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.ConfigManager;
import dev.kingrabbit.fruitfulutilities.config.categories.CodeHiderCategory;
import me.jellysquid.mods.sodium.client.render.occlusion.BlockOcclusionCache;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(BlockOcclusionCache.class)
public class BlockOcclusionCacheMixin {

    @Inject(method = "shouldDrawSide", at = @At("RETURN"), cancellable = true)
    public void shouldDrawSide(BlockState selfState, BlockView view, BlockPos pos, Direction facing, CallbackInfoReturnable<Boolean> cir) {
        if (CodeHiderCategory.shouldHideBlock(pos)) {
            cir.setReturnValue(false);
        }

        ConfigManager configManager = FruitfulUtilities.getInstance().configManager;
        CodeHiderCategory category = configManager.getCategory(CodeHiderCategory.class);
        if (configManager.enabled() && category.enabled && category.hideAll) {
            if (pos.getZ() == -4270 && -675 >= pos.getX() && pos.getX() >= -975 && facing == Direction.NORTH) cir.setReturnValue(true);
            else if (pos.getX() == -675 && -3970 >= pos.getZ() && pos.getZ() >= -4270 && facing == Direction.EAST) cir.setReturnValue(true);
            else if (pos.getZ() == -3970 && -3970 >= pos.getX() && pos.getX() >= -975 && facing == Direction.SOUTH) cir.setReturnValue(true);
            else if (pos.getX() == -975 && -3970 >= pos.getZ() && pos.getZ() >= -4270 && facing == Direction.WEST) cir.setReturnValue(true);
        }
    }

}
