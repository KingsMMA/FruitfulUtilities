package dev.kingrabbit.fruitfulutilities.pathviewer;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.kingrabbit.fruitfulutilities.util.Region;
import dev.kingrabbit.fruitfulutilities.util.SoundUtils;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.OrderedText;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.*;
import java.util.List;

public class PathScreen extends Screen {

    public static final String[] section_order = new String[]{
            "beginnings",
            "religion",
            "urban",
            "true_urban"
    };
    public static final HashMap<String, ItemGroup> section_icons = new HashMap<>();
    public static final Identifier PATH_ICON = new Identifier("fruitfulutilities", "textures/gui/path_icon.png");
    public static final Identifier HOVERED_PATH_ICON = new Identifier("fruitfulutilities", "textures/gui/path_icon_hovered.png");
    public static final Identifier SELECTED_PATH_ICON = new Identifier("fruitfulutilities", "textures/gui/path_icon_selected.png");
    public static final Identifier HOVERED_SELECTED_PATH_ICON = new Identifier("fruitfulutilities", "textures/gui/path_icon_selected_hovered.png");
    public static final Identifier UNLOCKED_PATH_ICON = new Identifier("fruitfulutilities", "textures/gui/path_icon_unlocked.png");
    public static final Identifier HOVERED_UNLOCKED_PATH_ICON = new Identifier("fruitfulutilities", "textures/gui/path_icon_unlocked_hovered.png");
    public static final Identifier LOCKED_PATH_ICON = new Identifier("fruitfulutilities", "textures/gui/path_icon_locked.png");
    public static final Identifier HOVERED_LOCKED_PATH_ICON = new Identifier("fruitfulutilities", "textures/gui/path_icon_locked_hovered.png");
    public static final Identifier BLOCKED_PATH_ICON = new Identifier("fruitfulutilities", "textures/gui/path_icon_blocked.png");
    public static final Identifier HOVERED_BLOCKED_PATH_ICON = new Identifier("fruitfulutilities", "textures/gui/path_icon_blocked_hovered.png");
    public static final int PRIMARY = new Color(70, 70, 70).getRGB();
    public static final int SECONDARY = new Color(50, 50, 50).getRGB();
    public static final int HEADER_COLOUR = new Color(70, 215, 10).getRGB();
    private static final Identifier TAB_TEXTURE = new Identifier("fruitfulutilities", "textures/gui/tabs.png");
    public static String section = "beginnings";
    public static HashMap<String, float[]> sections = new HashMap<>();
    //    public static float xOffset = -19284;
//    public static float yOffset = -64;  // TODO change to -64
//    public static float zoom = 1;
    public static HashMap<String, JsonObject> selectedElement = new HashMap<>();

    public final HashMap<Region, JsonObject> drawnElements = new HashMap<>();
    public List<OrderedText> tooltip = null;

    public PathScreen() {
        super(Text.empty());
    }

    @Override
    protected void init() {
        super.init();

        if (section_icons.isEmpty()) {
            section_icons.put("beginnings", ItemGroup.create(ItemGroup.Row.TOP, 0).displayName(Text.of("§aBeginnings")).icon(() -> new ItemStack(Items.GRASS_BLOCK)).build());
            section_icons.put("religion", ItemGroup.create(ItemGroup.Row.TOP, 0).displayName(Text.of("§aReligion")).icon(() -> new ItemStack(Items.ENCHANTING_TABLE)).build());
            section_icons.put("urban", ItemGroup.create(ItemGroup.Row.TOP, 0).displayName(Text.of("§aUrban")).icon(() -> new ItemStack(Items.IRON_BLOCK)).build());
            section_icons.put("true_urban", ItemGroup.create(ItemGroup.Row.TOP, 0).displayName(Text.of("§aTrue Urban")).icon(() -> new ItemStack(Items.GOLD_BLOCK)).build());
        }

        if (sections.isEmpty()) {
            sections.put("beginnings", new float[]{-19284, -64, 1});
        }

        if (xOffset() == -19284) {
            xOffset(width / 5f - 64 + 32 * 1.5f);
            yOffset(-64);
        }
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        clearChildren();
        drawnElements.clear();
        tooltip = null;

        renderBackground(matrices);

        // Path Viewer
        matrices.push();
        float _zoom = zoom();
        matrices.scale(_zoom, _zoom, _zoom);

//        DrawableHelper.drawTexture(matrices, (int) xOffset + 12, (int) yOffset + 12, 0, 0, 0, 32, 32, 32, 32);

        if (section.equals("beginnings")) {
            JsonObject beginnings = PathManager.paths.get("beginnings");
            renderUpgrade(matrices, beginnings.getAsJsonObject("faster_melon_spawn_rate"), 1, 1, mouseX, mouseY);
            renderUpgrade(matrices, beginnings.getAsJsonObject("hearth"), 2, 1, mouseX, mouseY);
            renderUpgrade(matrices, beginnings.getAsJsonObject("stronger_front_door"), 3, 1, mouseX, mouseY);
            renderUpgrade(matrices, beginnings.getAsJsonObject("better_jailors"), 4, 1, mouseX, mouseY);
            renderUpgrade(matrices, beginnings.getAsJsonObject("lock_and_key"), 5, 1, mouseX, mouseY);
            renderUpgrade(matrices, beginnings.getAsJsonObject("item_removal_procedures"), 6, 1, mouseX, mouseY);

            renderUpgrade(matrices, beginnings.getAsJsonObject("economics_room"), 1, 3, mouseX, mouseY);
            renderUpgrade(matrices, beginnings.getAsJsonObject("faster_coin_generation"), 2, 2, mouseX, mouseY);
            renderUpgrade(matrices, beginnings.getAsJsonObject("better_sell_deals"), 2, 3, mouseX, mouseY);
            renderUpgrade(matrices, beginnings.getAsJsonObject("increased_guard_limit"), 2, 4, mouseX, mouseY);
            connectUpgrades(1, 3, 2, 2);
            connectUpgrades(1, 3, 2, 3);
            connectUpgrades(1, 3, 2, 4);

            renderUpgrade(matrices, beginnings.getAsJsonObject("private_merchant"), 3, 2, mouseX, mouseY);
            renderUpgrade(matrices, beginnings.getAsJsonObject("personal_greenhouse"), 4, 2, mouseX, mouseY);
            connectUpgrades(3, 2, 4, 2);

            renderUpgrade(matrices, beginnings.getAsJsonObject("castle_ladder"), 3, 3, mouseX, mouseY);
            renderUpgrade(matrices, beginnings.getAsJsonObject("castle_roof"), 4, 3, mouseX, mouseY);

            renderUpgrade(matrices, beginnings.getAsJsonObject("castle_backyard"), 1, 5, mouseX, mouseY);
            renderUpgrade(matrices, beginnings.getAsJsonObject("castle_basement"), 2, 5, mouseX, mouseY);
            renderUpgrade(matrices, beginnings.getAsJsonObject("urban_start"), 3, 5, mouseX, mouseY);
            connectUpgrades(1, 5, 2, 5);
            connectUpgrades(2, 5, 3, 5);
        } else if (section.equals("urban")) {
            JsonObject urban = PathManager.paths.get("urban");
            renderUpgrade(matrices, urban.getAsJsonObject("melon_fertilizer"), 1, 1, mouseX, mouseY);
            renderUpgrade(matrices, urban.getAsJsonObject("significantly_melonier_melons"), 2, 1, mouseX, mouseY);
            renderUpgrade(matrices, urban.getAsJsonObject("faster_cooking"), 3, 1, mouseX, mouseY);
            renderUpgrade(matrices, urban.getAsJsonObject("melon_teleporter"), 4, 1, mouseX, mouseY);

            renderUpgrade(matrices, urban.getAsJsonObject("intensive_research"), 1, 3, mouseX, mouseY);
            renderUpgrade(matrices, urban.getAsJsonObject("melon_harvesting_technology"), 2, 2, mouseX, mouseY);
            renderUpgrade(matrices, urban.getAsJsonObject("better_return_rates"), 2, 3, mouseX, mouseY);
            renderUpgrade(matrices, urban.getAsJsonObject("extreme_economy"), 2, 4, mouseX, mouseY);
            connectUpgrades(1, 3, 2, 2);
            connectUpgrades(1, 3, 2, 3);
            connectUpgrades(1, 3, 2, 4);

            renderUpgrade(matrices, urban.getAsJsonObject("science_start"), 3, 2, mouseX, mouseY);
            renderUpgrade(matrices, urban.getAsJsonObject("democracy_start"), 3, 3, mouseX, mouseY);
            renderUpgrade(matrices, urban.getAsJsonObject("true_urban_start"), 3, 4, mouseX, mouseY);
        } else if (section.equals("true_urban")) {
            JsonObject trueUrban = PathManager.paths.get("true_urban");
            renderUpgrade(matrices, trueUrban.getAsJsonObject("a_dollar_a_dime"), 1, 1, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("doubled_city_funding"), 2, 1, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("guaranteed_returns"), 3, 1, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("better_farmers"), 4, 1, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("incredibly_fast_growth"), 1, 2, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("blast_protection"), 2, 2, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("overclock_teleporter"), 3, 2, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("strict_trade_laws"), 4, 2, mouseX, mouseY);

            renderUpgrade(matrices, trueUrban.getAsJsonObject("bigger_bunker"), 1, 4, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("load_cannons"), 2, 3, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("floating_islands"), 2, 5, mouseX, mouseY);
            connectUpgrades(1, 4, 2, 3);
            connectUpgrades(1, 4, 2, 5);

            renderUpgrade(matrices, trueUrban.getAsJsonObject("faster_selling"), 3, 3, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("morale_boost"), 3, 4, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("farming_island"), 3, 5, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("lower_shipping_taxes"), 3, 6, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("even_better_harvesting"), 3, 7, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("island_skylights"), 2, 7, mouseX, mouseY);
            connectUpgrades(2, 5, 3, 3);
            connectUpgrades(2, 5, 3, 4);
            connectUpgrades(2, 5, 3, 5);
            connectUpgrades(2, 5, 3, 6);
            connectUpgrades(2, 5, 3, 7);
            connectUpgrades(2, 5, 2, 7);

            renderUpgrade(matrices, trueUrban.getAsJsonObject("quality_control"), 4, 4, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("better_filters"), 4, 3, mouseX, mouseY);
            connectUpgrades(4, 3, 4, 4);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("defender_island"), 4, 5, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("deluxe_sky_farm"), 4, 6, mouseX, mouseY);
            connectUpgrades(3, 5, 4, 4);
            connectUpgrades(3, 5, 4, 5);
            connectUpgrades(3, 5, 4, 6);

            renderUpgrade(matrices, trueUrban.getAsJsonObject("public_executions"), 5, 3, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("iron_skin"), 5, 4, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("the_grand_finale"), 5, 5, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("projectile_protection"), 5, 6, mouseX, mouseY);
            renderUpgrade(matrices, trueUrban.getAsJsonObject("pricey_gunpowder"), 5, 7, mouseX, mouseY);
            connectUpgrades(4, 5, 5, 3);
            connectUpgrades(4, 5, 5, 4);
            connectUpgrades(4, 5, 5, 5);
            connectUpgrades(4, 5, 5, 6);
            connectUpgrades(4, 5, 5, 7);
        }

        matrices.pop();

        // UI
        DrawableHelper.fill(matrices, 0, 0, width / 5, height, PRIMARY);
        DrawableHelper.fill(matrices, width / 5, 0, width / 5 + 6, height, SECONDARY);

        DrawableHelper.drawCenteredTextWithShadow(matrices, textRenderer, "Path Viewer", width / 10, 20, HEADER_COLOUR);
        int y = 40;
        if (!selectedElement.containsKey(section)) {
            for (OrderedText line : textRenderer.wrapLines(StringVisitable.plain("Click on an upgrade to view information about it."), width / 5 - 20)) {
                DrawableHelper.drawTextWithShadow(matrices, textRenderer, line, 10, y, 0xFFFFFF);
                y += textRenderer.fontHeight + 2;
            }
        } else {
            JsonObject _selectedElement = selectedElement.get(section);
            String display = _selectedElement.get("display").getAsString();
            for (OrderedText line : textRenderer.wrapLines(StringVisitable.plain(
                    "§6Name: §f" + display + "\n" +
                            "§6Description: §f" + _selectedElement.get("description").getAsString() + "\n" +
                            "§6Price: §f" + _selectedElement.get("price").getAsInt() + " " + _selectedElement.get("currency").getAsString() + "\n" +
                            "§6Location: §f" + _selectedElement.get("location").getAsString().replaceAll(",", ", ")
            ), width / 5 - 20)) {
                DrawableHelper.drawTextWithShadow(matrices, textRenderer, line, 10, y, 0xFFFFFF);
                y += textRenderer.fontHeight + 2;
            }

            boolean isTracking = PathManager.tracking.contains(_selectedElement);
            boolean isUnlocked = PathManager.purchased.contains(display);
            ButtonWidget track = ButtonWidget.builder(Text.of(isTracking ? "Untrack" : "Track"), widget -> {
                if (isTracking) PathManager.tracking.remove(_selectedElement);
                else PathManager.tracking.add(_selectedElement);
            }).dimensions(20, y + 20, width / 10 - 10 - 20, 30).build();
            ButtonWidget unlock = ButtonWidget.builder(Text.of(isUnlocked ? "Lock" : "Unlock"), widget -> {
                if (isUnlocked) PathManager.purchased.remove(display);
                else PathManager.purchased.add(display);
            }).dimensions(width / 10 + 10, y + 20, width / 10 - 10 - 20, 30).build();
            addSelectableChild(track);//.render(matrices, mouseX, mouseY, delta);n
            addSelectableChild(unlock);//.render(matrices, mouseX, mouseY, delta);
            track.render(matrices, mouseX, mouseY, delta);
            unlock.render(matrices, mouseX, mouseY, delta);

            y += 60;
        }

        y += 50;

        DrawableHelper.drawCenteredTextWithShadow(matrices, textRenderer, "Tracked Upgrades", width / 10, y, HEADER_COLOUR);
        y += 20;

        if (PathManager.tracking.isEmpty()) {
            for (OrderedText line : textRenderer.wrapLines(StringVisitable.plain("Track an upgrade to view its waypoint and track your progress towards it."), width / 5 - 20)) {
                DrawableHelper.drawTextWithShadow(matrices, textRenderer, line, 10, y, 0xFFFFFF);
                y += textRenderer.fontHeight + 2;
            }
        } else {
            StringBuilder information = new StringBuilder("Currently tracking the following upgrades:");
            for (JsonObject tracked : PathManager.tracking) {
                information.append("\n    §7• §a").append(tracked.get("display").getAsString()).append("§7: ");
                HashMap<String, Integer> cost = PathManager.getPrice(tracked);
                if (cost.isEmpty()) information.append("§eUnlocked!");
                else {
                    for (String currency : cost.keySet()) {
                        String /*a, */b;
                        if (currency.equals("coins") || currency.equals("gold")) {
//                        a = "6";
                            b = "e";
                        } else if (currency.contains("shard")) {
//                        a = "3";
                            b = "b";
                        } else {
//                        a = "8";
                            b = "7";
                        }
                        int price = cost.get(currency);
                        information.append("§").append(b).append(price).append(" ").append(currency).append("§7, ");
                    }
                    information = new StringBuilder(information.substring(0, information.length() - 2));
                }
            }
            for (OrderedText line : textRenderer.wrapLines(StringVisitable.plain(information.toString()), width / 5 - 20)) {
                DrawableHelper.drawTextWithShadow(matrices, textRenderer, line, 10, y, 0xFFFFFF);
                y += textRenderer.fontHeight + 2;
            }

        }

        // Item tabs
        matrices.push();
        matrices.scale(1.5f, 1.5f, 1.5f);
        int tabX = (int) ((width / 5 + 6) / 1.5f);
        int tabY = (int) (20 / 1.5f);
        for (String _section : section_order) {
            if (sections.containsKey(_section)) {
                ItemGroup icon = section_icons.get(_section);
                renderTabIcon(matrices, icon, section.equals(_section), tabX, tabY);
                if (tabX <= mouseX / 1.5f && mouseX / 1.5f <= tabX + 32 &&
                        tabY <= mouseY / 1.5f && mouseY / 1.5f <= tabY + 26) {
                    tooltip = Collections.singletonList(icon.getDisplayName().asOrderedText());
                }
                tabY += 27;
            }
        }
        matrices.pop();

        // Tooltip
        if (tooltip != null) {
            renderOrderedTooltip(matrices, tooltip, mouseX, mouseY);
        }
    }

    public void renderTabIcon(MatrixStack matrices, ItemGroup group, boolean selected, int tabX, int tabY) {
        RenderSystem.setShaderTexture(0, TAB_TEXTURE);
        DrawableHelper.drawTexture(matrices, tabX, tabY, 0, selected ? 26 : 0, 32, 26, 32, 52);
        matrices.push();
        matrices.translate(0.0f, 0.0f, 100.0f);
        ItemStack itemStack = group.getIcon();
        this.itemRenderer.renderInGuiWithOverrides(matrices, itemStack, tabX += 8, tabY += 5);
        this.itemRenderer.renderGuiItemOverlay(matrices, this.textRenderer, itemStack, tabX, tabY);
        matrices.pop();
    }

    public void connectUpgrades(float x1, float y1, float x2, float y2) {
        float _xOffset = xOffset();
        float _yOffset = yOffset();
        float _zoom = zoom();

        x1 = 32 + x1 * 64;
        y1 = 32 + y1 * 64;
        x2 = 32 + x2 * 64;
        y2 = 32 + y2 * 64;

        if (x1 == x2) {
            // AAAAAAAAAAAAAAA
            // TODO fix.
            GlStateManager._depthMask(false);
            GlStateManager._disableCull();
            RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
            Tessellator tessellator = RenderSystem.renderThreadTesselator();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            RenderSystem.lineWidth(10 * _zoom);
            bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
            bufferBuilder.vertex((int) ((int) (x1 + _xOffset - 2 + 17.5) * _zoom), (int) ((int) (y1 + _yOffset + 32) * _zoom), 0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex((int) ((int) (x2 + _xOffset + 17.5) * _zoom), (int) ((int) (y1 + _yOffset + 32) * _zoom), 0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex((int) ((int) (x2 + _xOffset + 17.5) * _zoom), (int) ((int) (y2 + _yOffset + 0.5) * _zoom), 0).color(0, 0, 0, 255).next();
            bufferBuilder.vertex((int) ((int) (x1 + _xOffset - 2 + 17.5) * _zoom), (int) ((int) (y2 + _yOffset + 0.5) * _zoom), 0).color(0, 0, 0, 255).next();
            tessellator.draw();
            RenderSystem.lineWidth(1.0f);
            GlStateManager._enableCull();
            GlStateManager._depthMask(true);
            return;
        }

        GlStateManager._depthMask(false);
        GlStateManager._disableCull();
        RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram);
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.lineWidth((float) (Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2)) * 0.1f) * _zoom);
        bufferBuilder.begin(VertexFormat.DrawMode.LINES, VertexFormats.LINES);
        bufferBuilder.vertex((x1 + 29 + _xOffset) * _zoom, (y1 + 16 + _yOffset) * _zoom, 0).color(0, 0, 0, 255).normal(1.0f, 0.0f, 0.0f).next();
        bufferBuilder.vertex((x2 + 4 + _xOffset) * _zoom, (y2 + 16 + _yOffset) * _zoom, 0).color(0, 0, 0, 255).normal(1.0f, 0.0f, 0.0f).next();
        tessellator.draw();
        RenderSystem.lineWidth(1.0f);
        GlStateManager._enableCull();
        GlStateManager._depthMask(true);
    }

    public void renderUpgrade(MatrixStack matrices, JsonObject upgrade, float x, float y, int mouseX, int mouseY) {
        float _xOffset = xOffset();
        float _yOffset = yOffset();
        float _zoom = zoom();

        x = 32 + x * 64;
        y = 32 + y * 64;

        drawnElements.put(new Region((x + _xOffset) * _zoom, (y + _yOffset) * _zoom, (x + 32 + _xOffset) * _zoom, (y + 32 + _yOffset) * _zoom), upgrade);

        boolean unlocked = PathManager.purchased.contains(upgrade.get("display").getAsString());
        boolean hovered = x + _xOffset <= mouseX / _zoom && mouseX / _zoom <= x + _xOffset + 32 &&
                y + _yOffset <= mouseY / _zoom && mouseY / _zoom <= y + _yOffset + 32;

        RenderSystem.setShaderTexture(0, Objects.equals(upgrade, selectedElement.get(section)) ?
                (hovered ? HOVERED_SELECTED_PATH_ICON : SELECTED_PATH_ICON) :
                (
                        unlocked
                                ? (hovered ? HOVERED_UNLOCKED_PATH_ICON : UNLOCKED_PATH_ICON)
                                : (hovered ? HOVERED_PATH_ICON : PATH_ICON)
                )
        );
        DrawableHelper.drawTexture(matrices, (int) (_xOffset + x), (int) (_yOffset + y), 0, 0, 0, 32, 32, 32, 32);
        if (hovered) {
            if (mouseX < width / 5 + 6) return;
            List<OrderedText> lines = textRenderer.wrapLines(StringVisitable.plain("§7" + upgrade.get("description").getAsString()), 150);
            List<OrderedText> newLines = new ArrayList<>();
            newLines.add(Text.literal("§a" + upgrade.get("display").getAsString()).asOrderedText());
            newLines.addAll(lines);
            String currency = upgrade.get("currency").getAsString();
            String a, b;
            if (currency.equals("coins") || currency.equals("gold")) {
                a = "6";
                b = "e";
            } else if (currency.contains("shard")) {
                a = "3";
                b = "b";
            } else {
                a = "8";
                b = "7";
            }
            newLines.add(Text.literal("§" + a + "Price: §" + b + upgrade.get("price").getAsString() + " " + currency).asOrderedText());

            tooltip = newLines;
        }
    }

    // TODO Make zooming keep the mouse's relative position remain constant (i.e. such that the pixel underneath the cursor shall not change)
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        zoom(zoom() + amount * 0.2f);
        if (zoom() <= 0) zoom(0.2f);
        else if (zoom() >= 10) zoom(10f);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0) {
            xOffset(xOffset() + deltaX / zoom());
            yOffset(yOffset() + deltaY / zoom());
        }
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_R) {
            xOffset(width / 5f - 64 + 32 * 1.5f);
            yOffset(-64);
            zoom(1);

            SoundUtils.clickSound();
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {

        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        if (mouseX < width / 5f + 6) return super.mouseClicked(mouseX, mouseY, button);

        int tabX = (int) ((width / 5 + 6) / 1.5f);
        int tabY = (int) (20 / 1.5f);
        for (String _section : section_order) {
            if (sections.containsKey(_section)) {
                if (tabX <= mouseX / 1.5f && mouseX / 1.5f <= tabX + 32 &&
                        tabY <= mouseY / 1.5f && mouseY / 1.5f <= tabY + 26) {
                    if (!section.equals(_section)) {
                        section = _section;
                        SoundUtils.clickSound();

                        if (xOffset() == -19284) {
                            xOffset(width / 5f - 64 + 32 * 1.5f);
                            yOffset(-64);
                        }
                    }
                    return super.mouseClicked(mouseX, mouseY, button);
                }
                tabY += 27;
            }
        }

        for (Region region : drawnElements.keySet()) {
            if (region.contains(mouseX, mouseY)) {
                JsonObject upgradeData = drawnElements.get(region);
                if (Objects.equals(upgradeData, selectedElement.get(section))) {
                    selectedElement.remove(section);
                } else {
                    selectedElement.put(section, upgradeData);
                }
                SoundUtils.clickSound();
                return super.mouseClicked(mouseX, mouseY, button);
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    // region Offset and zoom getters and setters
    public float xOffset() {
        return sections.get(section)[0];
    }

    public void xOffset(double xOffset) {
        xOffset((float) xOffset);
    }

    public void xOffset(float xOffset) {
        sections.get(section)[0] = xOffset;
    }

    public float yOffset() {
        return sections.get(section)[1];
    }

    public void yOffset(double yOffset) {
        yOffset((float) yOffset);
    }

    public void yOffset(float yOffset) {
        sections.get(section)[1] = yOffset;
    }

    public float zoom() {
        return sections.get(section)[2];
    }

    public void zoom(double zoom) {
        zoom((float) zoom);
    }

    public void zoom(float zoom) {
        sections.get(section)[2] = zoom;
    }
    // endregion

}
