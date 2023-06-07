package dev.kingrabbit.fruitfulutilities.mixin;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.ConfigManager;
import dev.kingrabbit.fruitfulutilities.config.categories.GeneralCategory;
import dev.kingrabbit.fruitfulutilities.config.categories.MessageHiderCategory;
import dev.kingrabbit.fruitfulutilities.listener.TickListener;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onScoreboardObjectiveUpdate", at = @At("HEAD"))
    public void onScoreboardObjectiveUpdate(ScoreboardObjectiveUpdateS2CPacket packet, CallbackInfo ci) {
        String scoreboardObjective = packet.getDisplayName().getString();
        GeneralCategory generalCategory = FruitfulUtilities.getInstance().configManager.getCategory(GeneralCategory.class);
        if (!(generalCategory.gameDetection && generalCategory.enabled)) {
            FruitfulUtilities.getInstance().inMelonKing = generalCategory.enabled;
            return;
        }
        if (!scoreboardObjective.isBlank())
            FruitfulUtilities.getInstance().inMelonKing = scoreboardObjective.equals("< Melon King >");
    }

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    public void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        ConfigManager configManager = FruitfulUtilities.getInstance().configManager;
        String message = packet.content().getString();
        if (message.matches("^> [a-zA-Z_0-9]{1,16} is the new (king|queen|monarch)!$")) {
            String monarch = message.split(" ")[1];
            System.out.println("New monarch detected: " + monarch);
            FruitfulUtilities.getInstance().monarchNametag = monarch;
        }

        MessageHiderCategory category = configManager.getCategory(MessageHiderCategory.class);
        if (category.enabled) {
            if (category.noSuperMelons && message.equals("> You don't have any Super Enchanted Melons. Get them by cooking four Enchanted Melon Slices, which are gotten by cooking four Melon Slices.")) {
                ci.cancel();
            } else if (category.increasedSecurity && (message.matches("^> Suspicious activity detected from [a-zA-Z_0-9]{1,16}. They have grenades and/or flashbangs!$") || message.matches("^> Suspicious activity detected from [a-zA-Z_0-9]{1,16}. They are entering the black market!$"))) {
                ci.cancel();
            } else if (category.monarchUnderAttack && message.matches("^> The (king|queen|monarch) is under attack!$")) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "onPlaySound", at = @At("HEAD"))
    public void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        if (packet.getCategory().equals(SoundCategory.MASTER) && packet.getPitch() == 2 && packet.getVolume() == 2) {
            Optional<RegistryKey<SoundEvent>> key = packet.getSound().getKey();
            boolean present = key.isPresent();
            if (present) {
                RegistryKey<SoundEvent> soundEventRegistryKey = key.get();
                if (soundEventRegistryKey.getValue().toString().equals("minecraft:entity.player.levelup")) {
                    if (TickListener.searchingUntil - 5 > TickListener.tick) return;
                    TickListener.searchingUntil = TickListener.tick + 20;
                }
            }
        }
    }

}
