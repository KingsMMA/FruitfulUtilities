package dev.kingrabbit.fruitfulutilities.mixin;

import dev.kingrabbit.fruitfulutilities.config.categories.CodeHiderCategory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.chunk.ChunkRendererRegion;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkRendererRegion.class)
public class ChunkRendererRegionMixin {

    @Inject(method = "getBlockState", at = @At("HEAD"), cancellable = true)
    public void getBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        if (CodeHiderCategory.shouldHideBlock(pos)) cir.setReturnValue(Blocks.VOID_AIR.getDefaultState());;
    }

}
