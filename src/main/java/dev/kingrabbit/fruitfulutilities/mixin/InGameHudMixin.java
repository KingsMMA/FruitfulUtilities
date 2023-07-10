package dev.kingrabbit.fruitfulutilities.mixin;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.ConfigManager;
import dev.kingrabbit.fruitfulutilities.config.categories.FancyScoreboardCategory;
import dev.kingrabbit.fruitfulutilities.util.NumberUtils;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(InGameHud.class)
public class InGameHudMixin {

    @ModifyArg(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    public Text fancyScoreboardNumbers(Text text) {
        String textString = text.getString();
        if (!(textString.startsWith("Coins: ") || textString.startsWith("Bank Gold: "))) return text;
        ConfigManager configManager = FruitfulUtilities.getInstance().configManager;
        if (!(configManager.enabled() && configManager.getCategory(FancyScoreboardCategory.class).fancy)) return text;

        List<Text> siblings = text.getSiblings();

        if (siblings.size() >= 1) {
            List<Text> innerSiblings = siblings.get(0).getSiblings();
            if (innerSiblings.size() >= 2) {
                Text costText = innerSiblings.get(1);
                Text newCostText = Text.literal(NumberUtils.toFancyNumber(Float.parseFloat(costText.getString().replaceAll(",", "")))).setStyle(costText.getStyle());
                innerSiblings.set(1, newCostText);
            }
        }
        return text;
    }

    @ModifyArg(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;getWidth(Ljava/lang/String;)I", ordinal = 1))
    public String removeScoreboardNumbersFromWidth(String text) {
        ConfigManager configManager = FruitfulUtilities.getInstance().configManager;
        return configManager.enabled() && configManager.getCategory(FancyScoreboardCategory.class).numbers ? "" : text;
    }

    @Redirect(method = "renderScoreboardSidebar", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;draw(Lnet/minecraft/client/util/math/MatrixStack;Ljava/lang/String;FFI)I"))
    public int removeScoreboardNumbers(TextRenderer instance, MatrixStack matrices, String text, float x, float y, int color) {
        ConfigManager configManager = FruitfulUtilities.getInstance().configManager;
        return configManager.enabled() && configManager.getCategory(FancyScoreboardCategory.class).numbers ? 0 : instance.draw(matrices, text, x, y, color);
    }

}