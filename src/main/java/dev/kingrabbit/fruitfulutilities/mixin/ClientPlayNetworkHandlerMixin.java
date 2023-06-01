package dev.kingrabbit.fruitfulutilities.mixin;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.categories.GeneralCategory;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.ScoreboardObjectiveUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ScoreboardPlayerUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @Inject(method = "onScoreboardPlayerUpdate", at = @At("HEAD"))
    public void onScoreboardPlayerUpdate(ScoreboardPlayerUpdateS2CPacket packet, CallbackInfo ci) {
        System.out.println(packet.getPlayerName());
    }

}
