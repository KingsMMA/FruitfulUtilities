package dev.kingrabbit.fruitfulutilities.hud;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;

public class HudRenderer implements HudRenderCallback {

    public static final Color BACKGROUND_COLOUR = new Color(100, 100, 100, 150);

    @Override
    public void onHudRender(MatrixStack matrices, float tickDelta) {
        if (!FruitfulUtilities.getInstance().configManager.enabled()) return;

        LinkedHashMap<HudElement, ElementInfo> elements = FruitfulUtilities.getInstance().hudManager.elementList;
        for (HudElement element : elements.keySet()) {
            List<Object> lines = element.render(tickDelta);
            if (lines.isEmpty()) continue;
            Class<? extends HudElement> elementClass = element.getClass();
            try {
                int x = (int) elementClass.getField("x").get(element);
                int y = (int) elementClass.getField("y").get(element);

                TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                int totalLines = lines.size();
                int maxWidth = 0;
                for (Object line : lines) {
                    if (line instanceof String lineString) {
                        if (lineString.startsWith("<opac>")) {
                            totalLines--;
                            continue;
                        }
                    }
                    int lineWidth = line instanceof String ? textRenderer.getWidth((String) line) : (line instanceof OrderedText ? textRenderer.getWidth((OrderedText) line) : textRenderer.getWidth((Text) line));
                    if (lineWidth > maxWidth) maxWidth = lineWidth;
                }
                DrawableHelper.fill(matrices, x, y, x + 4 + maxWidth, y + 2 + (textRenderer.fontHeight + 2) * totalLines, BACKGROUND_COLOUR.getRGB());
                int opacity = 100;
                for (Object line : lines) {
//                    System.out.println(opacity);
                    if (line instanceof String stringLine) {
                        if (stringLine.startsWith("<opac>")) {
                            try {
                                opacity = Integer.parseInt(stringLine.substring(6, stringLine.length() - 7));
                            } catch (NumberFormatException exception) {
                                exception.printStackTrace();
                            }
                            continue;
                        }
                        DrawableHelper.drawTextWithShadow(matrices, textRenderer, stringLine, x + 2, y + 2, new Color(255, 255, 255, (int) (opacity * 2.55)).getRGB());
                    } else if (line instanceof OrderedText) DrawableHelper.drawTextWithShadow(matrices, textRenderer, (OrderedText) line, x + 2, y + 2, new Color(255, 255, 255, (int) (opacity * 2.55)).getRGB());
                    else if (line instanceof Text) DrawableHelper.drawTextWithShadow(matrices, textRenderer, (Text) line, x + 2, y + 2, new Color(255, 255, 255, (int) (opacity * 2.55)).getRGB());
                    y += 2 + textRenderer.fontHeight;
                    opacity = 100;
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                e.printStackTrace();
                continue;
            }
        }
    }

}
