package dev.kingrabbit.fruitfulutilities.config;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigBoolean;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigButton;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigDropdown;
import dev.kingrabbit.fruitfulutilities.pathviewer.PathManager;
import dev.kingrabbit.fruitfulutilities.util.SoundUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@SuppressWarnings("DuplicatedCode")
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
    public static final int DESCRIPTION_TEXT_COLOR = new Color(170, 170, 170).getRGB();

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
        PathManager.clearCache();

        super.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (client == null) return;

        renderBackground(context);

        // Background
        context.fill(width / 2 - 200 - 4, height / 2 - 150 + 4, width / 2 + 200 - 4, height / 2 + 150 + 4, WINDOW_BACKGROUND_SHADOW);
        context.fill(width / 2 - 200, height / 2 - 150, width / 2 + 200, height / 2 + 150, WINDOW_BACKGROUND);

        // Header Bevel
        context.fill(width / 2 - 200 + 10, height / 2 - 150 + 10, width / 2 + 200 - 10, height / 2 - 150 + 50, HEADER_BACKGROUND_SHADOW);
        context.fill(width / 2 - 200 + 10 + 2, height / 2 - 150 + 10 + 2, width / 2 + 200 - 10, height / 2 - 150 + 50, HEADER_BACKGROUND);

        MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.scale(2f, 2f, 2f);
        context.drawCenteredTextWithShadow(client.textRenderer, "FruitfulUtilities", (width / 2) / 2, (height / 2 - 150 + 24) / 2, 0xFFFFFF);
        matrices.pop();

        // Categories Bevel
        context.fill(width / 2 - 200 + 10, height / 2 - 150 + 60, width / 2 - 200 + 120, height / 2 + 150 - 10, SECTION_BACKGROUND_SHADOW);
        context.fill(width / 2 - 200 + 10 + 2, height / 2 - 150 + 60 + 2, width / 2 - 200 + 120, height / 2 + 150 - 10, SECTION_BACKGROUND);

        int x = width / 2 - 200 + 14;
        AtomicInteger y = new AtomicInteger(height / 2 - 150 + 60 + 2);
        FruitfulUtilities.getInstance().configManager.categoryList.forEach((configCategory, categoryInfo) -> {
            int currentY = y.getAndAdd(16);

            int x1 = x - 2;
            int x2 = x + 106;
            int y2 = y.get() - 1;
            if (selected_section.equals(categoryInfo.id())) {
                context.fill(x1, currentY, x2, y2, new Color(40, 40, 80).getRGB());
            }
            if (x1 <= mouseX && mouseX <= x2 &&
                    currentY <= mouseY && mouseY <= y2) {
                context.fill(x1, currentY, x2, y2, HOVER_OVERLAY);
            }

            context.drawTextWithShadow(client.textRenderer, categoryInfo.display(), x + 1, currentY + 3, 0xFFFFFF);
            context.drawHorizontalLine(x1, x + 105, y2, SECTION_BACKGROUND_SHADOW);
        });

        // Section Bevel
        context.fill(width / 2 - 200 + 130, height / 2 - 150 + 60, width / 2 + 200 - 10, height / 2 + 150 - 10, SECTION_BACKGROUND_SHADOW);
        context.fill(width / 2 - 200 + 130 + 2, height / 2 - 150 + 60 + 2, width / 2 + 200 - 10, height / 2 + 150 - 10, SECTION_BACKGROUND);

        ConfigCategory selectedCategory = FruitfulUtilities.getInstance().configManager.categoryIds.get(selected_section);
        Class<? extends ConfigCategory> selectedCategoryClass = selectedCategory.getClass();

        Field[] fields = selectedCategoryClass.getFields();
        int propertyY = height / 2 - 150 + 65;
        int x1 = width / 2 - 200 + 130 + 2;
        int x2 = width / 2 + 200 - 11;

        if (selectedCategoryClass.isAnnotationPresent(IncompatibleMod.class)) {
            IncompatibleMod incompatibleMod = selectedCategoryClass.getAnnotation(IncompatibleMod.class);

            if (FabricLoader.getInstance().isModLoaded(incompatibleMod.modId())) {
                List<OrderedText> lines = textRenderer.wrapLines(StringVisitable.plain(incompatibleMod.description()), (int) ((x2 - x1 - 20) / 0.8f));
                propertyY += 2;
                matrices.push();
                matrices.scale(0.8f, 0.8f, 0.8f);
                int centerX = (int) ((x1 + (x2 - x1) / 2f) / 0.8);
                for (OrderedText line : lines) {
                    context.drawCenteredTextWithShadow(textRenderer, line, centerX, (int) (propertyY / 0.8f), 0xEF1515);
                    propertyY += (textRenderer.fontHeight + 2) * 0.8f;
                }
                propertyY += (textRenderer.fontHeight + 2) * 0.8f;
                matrices.pop();
            }
        }

        for (Field field : fields) {
            boolean isConfigBoolean = field.isAnnotationPresent(ConfigBoolean.class);
            boolean isConfigDropdown = field.isAnnotationPresent(ConfigDropdown.class);
            boolean isConfigButton = field.isAnnotationPresent(ConfigButton.class);

            if ((isConfigBoolean && isConfigDropdown) || (isConfigBoolean && isConfigButton) || (isConfigDropdown && isConfigButton))
                throw new UnsupportedOperationException("Found field with non-one value of attributes: " + field.getName() + " (" + selectedCategoryClass.getName() + ")");

            ConfigBoolean configBoolean;
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
                    description = configButton.description();
                }

                context.fill(x1 + 5, propertyY, x2 - 5, propertyY + 50, SECTION_BACKGROUND_SHADOW);
                context.fill(x1 + 5, propertyY, x2 - 5 - 2, propertyY + 50 - 2, OPTION_FOREGROUND);

                context.drawTextWithShadow(client.textRenderer, display, x1 + 8, propertyY + 3, 0xFFFFFF);

                matrices.push();
                matrices.scale(0.8f, 0.8f, 0.8f);
                List<OrderedText> descriptionLines = textRenderer.wrapLines(StringVisitable.plain(description), 220);
                int offset = -9;
                for (OrderedText line : descriptionLines) {
                    context.drawTextWithShadow(client.textRenderer, line, (int) ((x1 + 8) / 0.8f), (int) ((propertyY + 14 + (offset += 9)) / 0.8f), DESCRIPTION_TEXT_COLOR);
                }
                matrices.pop();
            }

            if (isConfigBoolean) {
                matrices.push();
                matrices.scale(1.5f, 1.5f, 1.5f);

                Identifier textureId;
                try {
                    textureId = (boolean) field.get(selectedCategory) ? SWITCH_ENABLED : SWITCH_DISABLED;
                } catch (IllegalAccessException exception) {
                    textureId = SWITCH_UNKNOWN;
                    FruitfulUtilities.LOGGER.error("An error occurred accessing the value of " + field.getName() + " in " + selectedCategoryClass.getName(), exception);
                }
                context.drawTexture(textureId, (int) ((x2 - 64) / 1.5f), (int) ((propertyY + 10) / 1.5f), 0, 0, 0, 32, 16, 32, 16);
                matrices.pop();
            } else if (isConfigDropdown) {
                context.fill(x2 - 64, propertyY + 13, x2 - 16, propertyY + 31, SECTION_BACKGROUND_SHADOW);
                context.fill(x2 - 62, propertyY + 15, x2 - 16, propertyY + 31, SECTION_BACKGROUND);

                try {
                    String selectedOption = configDropdown.options()[(int) field.get(selectedCategory)];

                    context.drawTextWithShadow(client.textRenderer, selectedOption, x2 - 59, propertyY + 18, 0xFFFFFF);
                    if (selected_element.equals(configDropdown.id())) {
                        context.fill(x2 - 64, propertyY + 31, x2 - 16, propertyY + 31 + (configDropdown.options().length * 16) + 1, SECTION_BACKGROUND_SHADOW);
                        context.fill(x2 - 62, propertyY + 31, x2 - 16, propertyY + 31 + (configDropdown.options().length * 16) + 1, SECTION_BACKGROUND);

                        int propertyOptionsY = propertyY + 31;
                        context.drawHorizontalLine(x2 - 64, x2 - 17, propertyOptionsY - 1, SECTION_BACKGROUND_SHADOW);
                        context.drawHorizontalLine(x2 - 64, x2 - 17, propertyOptionsY, SECTION_BACKGROUND_SHADOW);
                        for (String option : configDropdown.options()) {
                            context.drawTextWithShadow(client.textRenderer, option, x2 - 59, propertyOptionsY + 4, DESCRIPTION_TEXT_COLOR);

                            if (x2 - 62 <= mouseX && mouseX <= x2 - 16 &&
                                    propertyOptionsY + 1 <= mouseY && mouseY <= propertyOptionsY + 15) {
                                context.fill(x2 - 62, propertyOptionsY + 1, x2 - 16, propertyOptionsY + 15, HOVER_OVERLAY);
                            }

                            propertyOptionsY += 16;
                            context.drawHorizontalLine(x2 - 62, x2 - 17, propertyOptionsY - 1, DROP_OPTION_SEPARATOR);
                            context.drawHorizontalLine(x2 - 62, x2 - 17, propertyOptionsY, DROP_OPTION_SEPARATOR);
                        }
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            } else if (isConfigButton) {
                matrices.push();
                matrices.scale(2.5f, 2.5f, 2.5f);
                context.drawTexture(BUTTON, (int) ((x2 - 92) / 2.5f), (int) ((propertyY + 5) / 2.5f), 0, 0, 0, 32, 16, 32, 16);
                matrices.pop();
                matrices.push();
                matrices.scale(1.5f, 1.5f, 1.5f);
                context.drawTextWithShadow(textRenderer, configButton.buttonText(), (int) ((x2 - 92 + 32 - textRenderer.getWidth(configButton.buttonText()) / 2f) / 1.5f), (int) ((propertyY + 16) / 1.5f), 0xFFFFFF);
                matrices.pop();
            }
            propertyY += 55;
        }

        super.render(context, mouseX, mouseY, delta);
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
                    int x2 = x + 106;
                    int y2 = y.get() - 1;

                    if (x1 <= mouseX && mouseX <= x2 &&
                            currentY <= mouseY && mouseY <= y2) {
                        selected_section = categoryInfo.id();
                        selected_element = "";

                        SoundUtils.clickSound();
                    }
                }

                if (categoryInfo.id().equals(selected_section)) {
                    Class<? extends ConfigCategory> selectedCategoryClass = configCategory.getClass();
                    Field[] fields = selectedCategoryClass.getFields();
                    int propertyY = height / 2 - 150 + 65;
                    int x1 = width / 2 - 200 + 130 + 2;
                    int x2 = width / 2 + 200 - 11;
                    if (selectedCategoryClass.isAnnotationPresent(IncompatibleMod.class)) {
                        IncompatibleMod incompatibleMod = selectedCategoryClass.getAnnotation(IncompatibleMod.class);

                        if (FabricLoader.getInstance().isModLoaded(incompatibleMod.modId())) {
                            List<OrderedText> lines = textRenderer.wrapLines(StringVisitable.plain(incompatibleMod.description()), (int) ((x2 - x1 - 20) / 0.8f));
                            propertyY += 2 + ((textRenderer.fontHeight + 2) * 0.8f) * (lines.size() + 1);
                        }
                    }

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
                                    for (int i = 0; i < configDropdown.options().length; i++) {
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
