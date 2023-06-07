package dev.kingrabbit.fruitfulutilities.mixin;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.categories.SearchingTrackerCategory;
import dev.kingrabbit.fruitfulutilities.listener.TickListener;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    @Inject(method = "setStack", at = @At("HEAD"))
    public void setStack(int slot, ItemStack stack, CallbackInfo ci) {
        SearchingTrackerCategory category = FruitfulUtilities.getInstance().configManager.getCategory(SearchingTrackerCategory.class);
        if (!category.enabled) return;
        String itemName = stack.getItem().getName().getString();
        if (itemName.equals("Melon Slice") || itemName.equals("Air")) return;
        if (TickListener.searchingUntil > TickListener.tick) {
            TickListener.searchingUntil = 0;
            for (int i = 0; i < 5; i++) {
                if (TickListener.searchingDrops.containsKey(TickListener.tick - i)) return;
            }
            TickListener.searchingDrops.put(TickListener.tick, stack.getName());
        }
    }

}
