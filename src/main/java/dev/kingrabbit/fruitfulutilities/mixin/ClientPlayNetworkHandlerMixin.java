package dev.kingrabbit.fruitfulutilities.mixin;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.ConfigManager;
import dev.kingrabbit.fruitfulutilities.config.categories.MessageHiderCategory;
import dev.kingrabbit.fruitfulutilities.listener.TickListener;
import dev.kingrabbit.fruitfulutilities.pathviewer.PathManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    private static final Pattern UPGRADE_PURCHASED_PATTERN = Pattern.compile("^> The (king|queen|monarch|city) has purchased the (.*) (upgrade|renovation)( for [0-9]{1,16} .*)?\\.$");

    @Inject(method = "onGameMessage", at = @At("HEAD"), cancellable = true)
    public void onGameMessage(GameMessageS2CPacket packet, CallbackInfo ci) {
        ConfigManager configManager = FruitfulUtilities.getInstance().configManager;
        String message = packet.content().getString();
        if (message.matches("^> [a-zA-Z_0-9]{1,16} is the new (king|queen|monarch)!$")) {
            FruitfulUtilities.getInstance().restartRun();
            return;
        }

        if (!configManager.enabled()) return;

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

        if (message.matches("^> The (king|queen|monarch|city) has purchased the (.*) (major upgrade|upgrade|renovation)( for [0-9]{1,16} .*)?\\.$")) {
            Matcher matcher = UPGRADE_PURCHASED_PATTERN.matcher(message);
            while (matcher.find()) {
                String upgradeName = matcher.group(2);
                if (upgradeName.endsWith(" major")) upgradeName = upgradeName.substring(0, upgradeName.length() - 6);
                PathManager.unlocked(upgradeName);
            }
        }

        if (message.equals("» Joined game: < Melon King > (4.0) by DeepSeaBlue.")) {
            FruitfulUtilities.getInstance().restartRun();
        } else if (message.matches("^The (king|queen|monarch) has [1-5][0-9] trophies! \\(Hover to view buffs\\)$")) {
            PathManager.unlocked("Economics Room");
        }
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Inject(method = "onPlaySound", at = @At("HEAD"))
    public void onPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        ConfigManager configManager = FruitfulUtilities.getInstance().configManager;
        if (!configManager.enabled()) return;

        if (packet.getCategory().equals(SoundCategory.MASTER) && packet.getPitch() == 2 && packet.getVolume() == 2) {
            Optional<RegistryKey<SoundEvent>> key = packet.getSound().getKey();
            boolean present = key.isPresent();
            if (present) {
                RegistryKey<SoundEvent> soundEventRegistryKey = key.get();
                if (soundEventRegistryKey.getValue().toString().equals("minecraft:entity.player.levelup")) {
                    if (TickListener.searchingUntil - 5 > TickListener.tick) return;

                    MinecraftClient client = MinecraftClient.getInstance();
                    if (client.player == null) return;
                    ItemStack mainHandStack = client.player.getMainHandStack();
                    if (mainHandStack.hasNbt()) {
                        NbtCompound nbt = mainHandStack.getNbt();
                        if (nbt.contains("PublicBukkitValues", NbtElement.COMPOUND_TYPE)) {
                            NbtCompound publicBukkitValues = nbt.getCompound("PublicBukkitValues");
                            if (publicBukkitValues.contains("hypercube:searching")) {
                                double searchingValue = publicBukkitValues.getDouble("hypercube:searching");
                                if (searchingValue < 1) return;
                            }
                        }
                    }

                    TickListener.searchingUntil = TickListener.tick + 20;
                }
            }
        }
    }

}
