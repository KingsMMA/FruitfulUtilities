package dev.kingrabbit.fruitfulutilities.hud.elements;

import com.google.gson.JsonObject;
import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.categories.PathViewerCategory;
import dev.kingrabbit.fruitfulutilities.hud.ElementInfo;
import dev.kingrabbit.fruitfulutilities.hud.HudElement;
import dev.kingrabbit.fruitfulutilities.hud.Serializable;
import dev.kingrabbit.fruitfulutilities.pathviewer.PathManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.StringVisitable;

import java.util.*;

@ElementInfo(id = "tracked_upgrades")
public class TrackedUpgradesElement extends HudElement {

    @Serializable(id = "x")
    public int x = 5;

    @Serializable(id = "y")
    public int y = 25;


    @Override
    public List<Object> render(float tickDelta) {
        PathViewerCategory category = FruitfulUtilities.getInstance().configManager.getCategory(PathViewerCategory.class);
        if (!category.hud) return Collections.emptyList();

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        ArrayList<Object> result = new ArrayList<>();

        result.add("§2Tracked Upgrades:");

        if (PathManager.tracking.isEmpty()) {
            result.addAll(textRenderer.wrapLines(StringVisitable.plain("Track an upgrade to view its waypoint and track your progress towards it."), 150));
        } else {
            for (JsonObject tracked : PathManager.tracking) {
                StringBuilder information = new StringBuilder("    §7• §a" + tracked.get("display").getAsString() + "§7: ");
                HashMap<String, Integer> cost = PathManager.cumulativePrice(PathManager.requiredToUnlock(tracked));
                if (cost.isEmpty()) {
                    if (category.hideIfUnlocked) continue;
                    else information.append("§eUnlocked!");
                    result.add(information.toString());
                } else {
                    for (String currency : cost.keySet()) {
                        char[] colors = PathManager.currencyColors(currency);
                        int price = cost.get(currency);
                        information.append("§").append(colors[1]).append(price).append(" ").append(currency).append("§7, ");
                    }
                    information = new StringBuilder(information.substring(0, information.length() - 2));
                    result.add(information.toString());
                }
            }
        }

        return result;
    }

}
