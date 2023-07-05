package dev.kingrabbit.fruitfulutilities.hud;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.List;

@SuppressWarnings("CanBeFinal")
public class HudPositionsScreen extends Screen {

    public Screen parent;
    public HudElement moving;
    public double xOffset = 0;
    public double yOffset = 0;

    public HudPositionsScreen(Screen parent) {
        super(Text.of("Edit GUI Locations"));

        this.parent = parent;
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            LinkedHashMap<HudElement, ElementInfo> elements = FruitfulUtilities.getInstance().hudManager.elementList;
            for (HudElement element : elements.keySet()) {
                List<Object> lines = element.render(0);
                if (lines.isEmpty()) continue;
                Class<? extends HudElement> elementClass = element.getClass();
                try {
                    Field xField = elementClass.getField("x");
                    Field yField = elementClass.getField("y");
                    int x1 = (int) xField.get(element);
                    int y1 = (int) yField.get(element);

                    TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
                    int maxWidth = 0;
                    for (Object line : lines) {
                        int lineWidth = line instanceof String ? textRenderer.getWidth((String) line) : textRenderer.getWidth((OrderedText) line);
                        if (lineWidth > maxWidth) maxWidth = lineWidth;
                    }

                    int x2 = x1 + maxWidth + 4;
                    int y2 = y1 + 2 + (2 + textRenderer.fontHeight) * lines.size();

                    if (x1 <= mouseX && mouseX <= x2 &&
                            y1 <= mouseY && mouseY <= y2) {
                        moving = element;
                        xOffset = mouseX - x1;
                        yOffset = mouseY - y1;
                        break;
                    }
                } catch (IllegalAccessException | NoSuchFieldException exception) {
                    FruitfulUtilities.LOGGER.error("An error occurred modifying the position fields of " + elementClass.getName(), exception);
                    continue;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            moving = null;
            xOffset = 0;
            yOffset = 0;
        }

        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0) {
            if (moving != null) {
                Class<? extends HudElement> movingClass = moving.getClass();
                try {
                    movingClass.getField("x").set(moving, (int) (mouseX - xOffset));
                    movingClass.getField("y").set(moving, (int) (mouseY - yOffset));
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }


    @Override
    public void close() {
        FruitfulUtilities.getInstance().hudManager.save();
        if (this.client == null) return;
        this.client.setScreen(parent);
    }

}
