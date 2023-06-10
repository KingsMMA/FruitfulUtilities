package dev.kingrabbit.fruitfulutilities.mixin;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.ConfigManager;
import dev.kingrabbit.fruitfulutilities.config.categories.TabCategory;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {

    @Shadow
    public abstract Text getPlayerName(PlayerListEntry entry);

    @Inject(method = "collectPlayerEntries", at = @At("RETURN"), cancellable = true)
    public void collectPlayerEntries(CallbackInfoReturnable<List<PlayerListEntry>> cir) {
        ConfigManager configManager = FruitfulUtilities.getInstance().configManager;
        if (!configManager.enabled()) return;

        if (FruitfulUtilities.getInstance().inMelonKing && FruitfulUtilities.getInstance().configManager.getCategory(TabCategory.class).enabled) {
            List<AbstractClientPlayerEntity> players = MinecraftClient.getInstance().world.getPlayers();
            List<PlayerListEntry> returnValue = new ArrayList<>();
            for (PlayerListEntry entry : cir.getReturnValue()) {
                String name = getPlayerName(entry).getString().replaceAll("§.", "");
                for (AbstractClientPlayerEntity abstractClientPlayerEntity : players) {
                    if (abstractClientPlayerEntity.getName().getString().equals(FruitfulUtilities.getInstance().monarchNametag)) {
                        entry.setDisplayName(Text.of("§6" + name));
                        returnValue.add(entry);
                    }
                    if (abstractClientPlayerEntity.getUuid().equals(entry.getProfile().getId())) {
//                        entry.setDisplayName(Text.of("§a" + name));
                        if (!returnValue.contains(entry)) returnValue.add(entry);
                    }
                }
            }
            cir.setReturnValue(returnValue);
        } else {
            List<PlayerListEntry> returnValue = new ArrayList<>();
            for (PlayerListEntry entry : cir.getReturnValue()) {
                String name = getPlayerName(entry).getString();
                if (name.startsWith("§a")) {
                    name = name.substring(2);
                    entry.setDisplayName(Text.of(name));
                }
                returnValue.add(entry);
            }
            cir.setReturnValue(returnValue);
        }
    }


}
