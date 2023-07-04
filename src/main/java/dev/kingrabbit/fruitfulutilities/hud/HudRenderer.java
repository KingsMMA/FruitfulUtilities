package dev.kingrabbit.fruitfulutilities.hud;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;

public class HudRenderer implements HudRenderCallback {

    public static final Color BACKGROUND_COLOUR = new Color(100, 100, 100, 150);

    @Override
    public void onHudRender(MatrixStack matrices, float tickDelta) {
        LinkedHashMap<HudElement, ElementInfo> elements = FruitfulUtilities.getInstance().hudManager.elementList;
        for (HudElement element : elements.keySet()) {
            List<Object> lines = element.render(tickDelta);
            if (lines.isEmpty()) continue;
            Class<? extends HudElement> elementClass = element.getClass();
            try {
                int x = (int) elementClass.getField("x").get(element);
                int y = (int) elementClass.getField("y").get(element);

                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                int maxWidth = 0;
                for (Object line : lines) {
                    int lineWidth = line instanceof String ? textRenderer.getWidth((String) line) : textRenderer.getWidth((OrderedText) line);
                    if (lineWidth > maxWidth) maxWidth = lineWidth;
                }
                DrawableHelper.fill(matrices, x, y, x + 4 + maxWidth, y + 2 + (textRenderer.fontHeight + 2) * lines.size(), BACKGROUND_COLOUR.getRGB());
                for (Object line : lines) {
                    if (line instanceof String) DrawableHelper.drawTextWithShadow(matrices, textRenderer, (String) line, x + 2, y + 2, 0xFFFFFF);
                    else if (line instanceof OrderedText) DrawableHelper.drawTextWithShadow(matrices, textRenderer, (OrderedText) line, x + 2, y + 2, 0xFFFFFF);
                    y += 2 + textRenderer.fontHeight;
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

}
