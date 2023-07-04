package dev.kingrabbit.fruitfulutilities.config;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigBoolean;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigButton;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigDropdown;
import dev.kingrabbit.fruitfulutilities.util.SoundUtils;
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
    public static final Identifier BUTTON = new Identifier("fruitfulutilities", "textures/gui/button.png");
    public static final int HOVER_OVERLAY = new Color(255, 255, 255, 50).getRGB();
    public static final int WINDOW_BACKGROUND = new Color(40, 40, 70).getRGB();
    public static final int WINDOW_BACKGROUND_SHADOW = new Color(30, 30, 60).getRGB();
    public static final int HEADER_BACKGROUND = new Color(30, 30, 60).getRGB();
    public static final int HEADER_BACKGROUND_SHADOW = new Color(20, 20, 40).getRGB();
    public static final int SECTION_BACKGROUND = new Color(30, 30, 60).getRGB();
    public static final int SECTION_BACKGROUND_SHADOW = new Color(20, 20, 40).getRGB();
    public static final int DROP_OPTION_SEPARATOR = new Color(25, 25, 50).getRGB();
    public static final int OPTION_FOREGROUND = new Color(40, 40, 70).getRGB();
    public static final int DESCRIPTION_TEXT_COLOUR = new Color(170, 170, 170).getRGB();

    public static String selected_section = "general";
    public static String selected_element = "";

    public ConfigScreen() {
        super(Text.empty());
    }

    @Override
    protected void init() {
        super.init();

        selected_element = "";
    }

    @Override
    public void close() {
        FruitfulUtilities.getInstance().configManager.save();
        FruitfulUtilities.getInstance().hudManager.save();

        super.close();
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);

        // Background
        DrawableHelper.fill(matrices, width / 2 - 200 - 4, height / 2 - 150 + 4, width / 2 + 200 - 4, height / 2 + 150 + 4, WINDOW_BACKGROUND_SHADOW);
        DrawableHelper.fill(matrices, width / 2 - 200, height / 2 - 150, width / 2 + 200, height / 2 + 150, WINDOW_BACKGROUND);

        // Header Bevel
        DrawableHelper.fill(matrices, width / 2 - 200 + 10, height / 2 - 150 + 10, width / 2 + 200 - 10, height / 2 - 150 + 50, HEADER_BACKGROUND_SHADOW);
        DrawableHelper.fill(matrices, width / 2 - 200 + 10 + 2, height / 2 - 150 + 10 + 2, width / 2 + 200 - 10, height / 2 - 150 + 50, HEADER_BACKGROUND);

        matrices.push();
        matrices.scale(2f, 2f, 2f);
        DrawableHelper.drawCenteredTextWithShadow(matrices, client.textRenderer, "FruitfulUtilities", (width / 2) / 2, (height / 2 - 150 + 24) / 2, 0xFFFFFF);
        matrices.pop();

        // Categories Bevel
        DrawableHelper.fill(matrices, width / 2 - 200 + 10, height / 2 - 150 + 60, width / 2 - 200 + 120, height / 2 + 150 - 10, SECTION_BACKGROUND_SHADOW);
        DrawableHelper.fill(matrices, width / 2 - 200 + 10 + 2, height / 2 - 150 + 60 + 2, width / 2 - 200 + 120, height / 2 + 150 - 10, SECTION_BACKGROUND);

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
                DrawableHelper.fill(matrices, x1, y1, x2, y2, HOVER_OVERLAY);
            }

            DrawableHelper.drawTextWithShadow(matrices, client.textRenderer, categoryInfo.display(), x + 1, currentY + 3, 0xFFFFFF);
            DrawableHelper.drawHorizontalLine(matrices, x1, x + 105, y2, SECTION_BACKGROUND_SHADOW);
        });

        // Section Bevel
        DrawableHelper.fill(matrices, width / 2 - 200 + 130, height / 2 - 150 + 60, width / 2 + 200 - 10, height / 2 + 150 - 10, SECTION_BACKGROUND_SHADOW);
        DrawableHelper.fill(matrices, width / 2 - 200 + 130 + 2, height / 2 - 150 + 60 + 2, width / 2 + 200 - 10, height / 2 + 150 - 10, SECTION_BACKGROUND);

        ConfigCategory selectedCategory = FruitfulUtilities.getInstance().configManager.categoryIds.get(selected_section);
        Field[] fields = selectedCategory.getClass().getFields();
        int propertyY = height / 2 - 150 + 65;
        int x1 = width / 2 - 200 + 130 + 2;
        int x2 = width / 2 + 200 - 11;
        for (Field field : fields) {
            boolean isConfigBoolean = field.isAnnotationPresent(ConfigBoolean.class);
            boolean isConfigDropdown = field.isAnnotationPresent(ConfigDropdown.class);
            boolean isConfigButton = field.isAnnotationPresent(ConfigButton.class);

            if ((isConfigBoolean && isConfigDropdown) || (isConfigBoolean && isConfigButton) || (isConfigDropdown && isConfigButton))
                throw new UnsupportedOperationException("Found field with non-one value of attributes: " + field.getName() + " (" + selectedCategory.getClass().getName() + ")");

            ConfigBoolean configBoolean = null;
            ConfigDropdown configDropdown = null;
            ConfigButton configButton = null;
            if (isConfigBoolean || isConfigDropdown || isConfigButton) {
                String display;
                String description;

                if (isConfigBoolean) {
                    configBoolean = field.getAnnotation(ConfigBoolean.class);

                    display = configBoolean.display();
                    description = configBoolean.description();
                } else if (isConfigDropdown) {
                    configDropdown = field.getAnnotation(ConfigDropdown.class);

                    display = configDropdown.display();
                    description = configDropdown.description();
                } else {
                    configButton = field.getAnnotation(ConfigButton.class);

                    display = configButton.display();
                    description = configButton.description();;
                }

                DrawableHelper.fill(matrices, x1 + 5, propertyY, x2 - 5, propertyY + 50, SECTION_BACKGROUND_SHADOW);
                DrawableHelper.fill(matrices, x1 + 5, propertyY, x2 - 5 - 2, propertyY + 50 - 2, OPTION_FOREGROUND);

                DrawableHelper.drawTextWithShadow(matrices, client.textRenderer, display, x1 + 8, propertyY + 3, 0xFFFFFF);

                matrices.push();
                matrices.scale(0.8f, 0.8f, 0.8f);
                List<OrderedText> descriptionLines = textRenderer.wrapLines(StringVisitable.plain(description), 220);
                int offset = -9;
                for (OrderedText line : descriptionLines) {
                    DrawableHelper.drawTextWithShadow(matrices, client.textRenderer, line, (int) ((x1 + 8) / 0.8f), (int) ((propertyY + 14 + (offset += 9)) / 0.8f), DESCRIPTION_TEXT_COLOUR);
                }
                matrices.pop();
            }

            if (isConfigBoolean) {
                matrices.push();
                matrices.scale(1.5f, 1.5f, 1.5f);
                try {
                    RenderSystem.setShaderTexture(0, (boolean) field.get(selectedCategory) ? SWITCH_ENABLED : SWITCH_DISABLED);
                } catch (IllegalAccessException e) {
                    RenderSystem.setShaderTexture(0, SWITCH_UNKNOWN);
                    e.printStackTrace();
                }
                DrawableHelper.drawTexture(matrices, (int) ((x2 - 64) / 1.5f), (int) ((propertyY + 10) / 1.5f), 0, 0, 0, 32, 16, 32, 16);
                matrices.pop();

//                DrawableHelper.drawHorizontalLine(matrices, x1, x2, propertyY + 39, new Color(20, 20, 40).getRGB());
            } else if (isConfigDropdown) {
                DrawableHelper.fill(matrices, x2 - 64, propertyY + 13, x2 - 16, propertyY + 31, SECTION_BACKGROUND_SHADOW);
                DrawableHelper.fill(matrices, x2 - 62, propertyY + 15, x2 - 16, propertyY + 31, SECTION_BACKGROUND);

                try {
                    String selectedOption = configDropdown.options()[(int) field.get(selectedCategory)];

                    DrawableHelper.drawTextWithShadow(matrices, client.textRenderer, selectedOption, x2 - 59, propertyY + 18, 0xFFFFFF);
                    if (selected_element.equals(configDropdown.id())) {
                        DrawableHelper.fill(matrices, x2 - 64, propertyY + 31, x2 - 16, propertyY + 31 + (configDropdown.options().length * 16) + 1, SECTION_BACKGROUND_SHADOW);
                        DrawableHelper.fill(matrices, x2 - 62, propertyY + 31, x2 - 16, propertyY + 31 + (configDropdown.options().length * 16) + 1, SECTION_BACKGROUND);

                        int propertyOptionsY = propertyY + 31;
                        DrawableHelper.drawHorizontalLine(matrices, x2 - 64, x2 - 17, propertyOptionsY - 1, SECTION_BACKGROUND_SHADOW);
                        DrawableHelper.drawHorizontalLine(matrices, x2 - 64, x2 - 17, propertyOptionsY, SECTION_BACKGROUND_SHADOW);
                        for (String option : configDropdown.options()) {
                            DrawableHelper.drawTextWithShadow(matrices, client.textRenderer, option, x2 - 59, propertyOptionsY + 4, DESCRIPTION_TEXT_COLOUR);

                            if (x2 - 62 <= mouseX && mouseX <= x2 - 16 &&
                                    propertyOptionsY + 1 <= mouseY && mouseY <= propertyOptionsY + 15) {
                                DrawableHelper.fill(matrices, x2 - 62, propertyOptionsY + 1, x2 - 16, propertyOptionsY + 15, HOVER_OVERLAY);
                            }

                            propertyOptionsY += 16;
                            DrawableHelper.drawHorizontalLine(matrices, x2 - 62, x2 - 17, propertyOptionsY - 1, DROP_OPTION_SEPARATOR);
                            DrawableHelper.drawHorizontalLine(matrices, x2 - 62, x2 - 17, propertyOptionsY, DROP_OPTION_SEPARATOR);
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else if (isConfigButton) {
                matrices.push();
                matrices.scale(2.5f, 2.5f, 2.5f);
                RenderSystem.setShaderTexture(0, BUTTON);
                DrawableHelper.drawTexture(matrices, (int) ((x2 - 92) / 2.5f), (int) ((propertyY + 5) / 2.5f), 0, 0, 0, 32, 16, 32, 16);
                matrices.pop();
                matrices.push();
                matrices.scale(1.5f, 1.5f, 1.5f);
                textRenderer.drawWithShadow(matrices, configButton.buttonText(), ((x2 - 92 + 32 - textRenderer.getWidth(configButton.buttonText()) / 2f) / 1.5f), ((propertyY + 16) / 1.5f), 0xFFFFFF);
                matrices.pop();
            }
            propertyY += 55;
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
                        selected_element = "";

                        SoundUtils.clickSound();
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
                                    SoundUtils.clickSound();
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        } else if (field.isAnnotationPresent(ConfigDropdown.class)) {
                            ConfigDropdown configDropdown = field.getAnnotation(ConfigDropdown.class);
                            if (x2 - 64 <= mouseX && mouseX <= x2 - 16 &&
                                    propertyY + 13 <= mouseY && mouseY <= propertyY + 31) {
                                SoundUtils.clickSound();

                                if (selected_element.equals(configDropdown.id())) {
                                    selected_element = "";
                                } else {
                                    selected_element = configDropdown.id();
                                }
                                return;
                            } else {
                                if (selected_element.equals(configDropdown.id())) {
                                    int propertyOptionsY = propertyY + 31;
                                    int i = 0;
                                    for (String option : configDropdown.options()) {
                                        if (x2 - 62 <= mouseX && mouseX <= x2 - 16 &&
                                                propertyOptionsY + 1 <= mouseY && mouseY <= propertyOptionsY + 15) {
                                            try {
                                                field.set(configCategory, i);
                                                selected_element = "";
                                                SoundUtils.clickSound();
                                                return;
                                            } catch (IllegalAccessException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }

                                        i += 1;
                                        propertyOptionsY += 16;
                                    }
                                }
                            }
                        } else if (field.isAnnotationPresent(ConfigButton.class)) {
                            if (x2 - 92 <= mouseX && mouseX <= x2 - 92 + 48 &&
                                    propertyY + 5 <= mouseY && mouseY <= propertyY + 5 + 24) {
                                try {
                                    ((Runnable) field.get(configCategory)).run();
                                    SoundUtils.clickSound();
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }

                        propertyY += 55;
                    }
                }

            });
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

}
