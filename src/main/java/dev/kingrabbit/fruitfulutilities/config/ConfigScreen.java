package dev.kingrabbit.fruitfulutilities.config;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigBoolean;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ConfigScreen extends Screen {

    public static final Identifier SWITCH_UNKNOWN = new Identifier("fruitfulutilities", "textures/gui/switch_unknown.png");
    public static final Identifier SWITCH_DISABLED = new Identifier("fruitfulutilities", "textures/gui/switch_disabled.png");
    public static final Identifier SWITCH_ENABLED = new Identifier("fruitfulutilities", "textures/gui/switch_enabled.png");

    public static String selected_section = "general";

    public ConfigScreen() {
        super(Text.of("test"));
    }

    @Override
    protected void init() {
        super.init();

        System.out.println("init");

//        this.addDrawableChild(
//                FruitfulUtilities.getInstance().configManager.BETTER_PLAYER_LIST
//        );
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);

        // Background
        DrawableHelper.fill(matrices, width / 2 - 200 - 4, height / 2 - 150 + 4, width / 2 + 200 - 4, height / 2 + 150 + 4, new Color(30, 30, 60).getRGB());
        DrawableHelper.fill(matrices, width / 2 - 200, height / 2 - 150, width / 2 + 200, height / 2 + 150, new Color(40, 40, 70).getRGB());

        // Header Bevel
        DrawableHelper.fill(matrices, width / 2 - 200 + 10, height / 2 - 150 + 10, width / 2 + 200 - 10, height / 2 - 150 + 50, new Color(20, 20, 40).getRGB());
        DrawableHelper.fill(matrices, width / 2 - 200 + 10 + 2, height / 2 - 150 + 10 + 2, width / 2 + 200 - 10, height / 2 - 150 + 50, new Color(30, 30, 60).getRGB());

        matrices.push();
        matrices.scale(2f, 2f, 2f);
        DrawableHelper.drawCenteredTextWithShadow(matrices, client.textRenderer, "FruitUtilities", (width / 2) / 2, (height / 2 - 150 + 24) / 2, 0xFFFFFF);
        matrices.pop();

        // Categories Bevel
        DrawableHelper.fill(matrices, width / 2 - 200 + 10, height / 2 - 150 + 60, width / 2 - 200 + 120, height / 2 + 150 - 10, new Color(20, 20, 40).getRGB());
        DrawableHelper.fill(matrices, width / 2 - 200 + 10 + 2, height / 2 - 150 + 60 + 2, width / 2 - 200 + 120, height / 2 + 150 - 10, new Color(30, 30, 60).getRGB());

        int x = width / 2 - 200 + 14;
        AtomicInteger y = new AtomicInteger(height / 2 - 150 + 60 + 2);
        FruitfulUtilities.getInstance().configManager.categoryList.forEach((configCategory, categoryInfo) -> {
            int currentY = y.getAndAdd(16);

            int x1 = x - 2;
            int y1 = currentY;
            int x2 = x + 106;
            int y2 = y.get() - 1;
            if (selected_section.equals(categoryInfo.id())) {
                DrawableHelper.fill(matrices, x1, y1, x2, y2, new Color(40, 40, 80).getRGB());
            }
            if (x1 <= mouseX && mouseX <= x2 &&
                    y1 <= mouseY && mouseY <= y2) {
                DrawableHelper.fill(matrices, x1, y1, x2, y2, new Color(255, 255, 255, 50).getRGB());
            }

            DrawableHelper.drawTextWithShadow(matrices, client.textRenderer, categoryInfo.display(), x + 1, currentY + 3, 0xFFFFFF);
            DrawableHelper.drawHorizontalLine(matrices, x1, x + 105, y2, new Color(20, 20, 40).getRGB());
        });

        // Section Bevel
        DrawableHelper.fill(matrices, width / 2 - 200 + 130, height / 2 - 150 + 60, width / 2 + 200 - 10, height / 2 + 150 - 10, new Color(20, 20, 40).getRGB());
        DrawableHelper.fill(matrices, width / 2 - 200 + 130 + 2, height / 2 - 150 + 60 + 2, width / 2 + 200 - 10, height / 2 + 150 - 10, new Color(30, 30, 60).getRGB());

        ConfigCategory selectedCategory = FruitfulUtilities.getInstance().configManager.categoryIds.get(selected_section);
        Field[] fields = selectedCategory.getClass().getFields();
        int propertyY = height / 2 - 150 + 65;
        int x1 = width / 2 - 200 + 130 + 2;
        int x2 = width / 2 + 200 - 11;
        for (Field field : fields) {
            if (field.isAnnotationPresent(ConfigBoolean.class)) {
                ConfigBoolean configBoolean = field.getAnnotation(ConfigBoolean.class);

                DrawableHelper.fill(matrices, x1 + 5, propertyY, x2 - 5, propertyY + 50, new Color(20, 20, 40).getRGB());
                DrawableHelper.fill(matrices, x1 + 5, propertyY, x2 - 5 - 2, propertyY + 50 - 2, new Color(40, 40, 70).getRGB());

                DrawableHelper.drawTextWithShadow(matrices, client.textRenderer, configBoolean.display(), x1 + 8, propertyY + 3, 0xFFFFFF);

                matrices.push();
                matrices.scale(0.8f, 0.8f, 0.8f);
                List<OrderedText> descriptionLines = textRenderer.wrapLines(StringVisitable.plain(configBoolean.description()), 220);
                int offset = -9;
                for (OrderedText line : descriptionLines) {
                    DrawableHelper.drawTextWithShadow(matrices, client.textRenderer, line, (int) ((x1 + 8) / 0.8f), (int) ((propertyY + 14 + (offset += 9)) / 0.8f), new Color(170, 170, 170).getRGB());
                }
                matrices.pop();

                matrices.push();
                matrices.scale(1.5f, 1.5f, 1.5f);
                try {
                    RenderSystem.setShaderTexture(0, (boolean) field.get(selectedCategory) ? SWITCH_ENABLED : SWITCH_DISABLED);
                } catch (IllegalAccessException e) {
                    RenderSystem.setShaderTexture(0, SWITCH_UNKNOWN);
                    throw new RuntimeException(e);
                }
                DrawableHelper.drawTexture(matrices, (int) ((x2 - 64) / 1.5f), (int) ((propertyY + 10) / 1.5f), 0, 0, 0, 32, 16, 32, 16);
                matrices.pop();

//                DrawableHelper.drawHorizontalLine(matrices, x1, x2, propertyY + 39, new Color(20, 20, 40).getRGB());
                propertyY += 55;

            }
        }

        super.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (button == 0) {
            int x = width / 2 - 200 + 14;
            AtomicInteger y = new AtomicInteger(height / 2 - 150 + 60 + 2);

            FruitfulUtilities.getInstance().configManager.categoryList.forEach((configCategory, categoryInfo) -> {
                int currentY = y.getAndAdd(16);
                if (!selected_section.equals(categoryInfo.id())) {

                    int x1 = x - 2;
                    int y1 = currentY;
                    int x2 = x + 106;
                    int y2 = y.get() - 1;

                    if (x1 <= mouseX && mouseX <= x2 &&
                            y1 <= mouseY && mouseY <= y2) {
                        selected_section = categoryInfo.id();

                        clickSound();
                    }
                }

                if (categoryInfo.id().equals(selected_section)) {
                    Field[] fields = configCategory.getClass().getFields();
                    int propertyY = height / 2 - 150 + 65;
                    int x1 = width / 2 - 200 + 130 + 2;
                    int x2 = width / 2 + 200 - 11;
                    for (Field field : fields) {
                        if (field.isAnnotationPresent(ConfigBoolean.class)) {
                            if (x2 - 64 <= mouseX && mouseX <= x2 - 16 &&
                                    propertyY + 10 <= mouseY && mouseY <= propertyY + 10 + 24) {
                                try {
                                    field.set(configCategory, !(boolean) field.get(configCategory));
                                    clickSound();
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            propertyY += 55;

                        }
                    }
                }

            });
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void clickSound() {
        client.player.playSound(SoundEvents.UI_BUTTON_CLICK.value(), SoundCategory.MASTER, 0.4f, 1.0f);
    }

}
