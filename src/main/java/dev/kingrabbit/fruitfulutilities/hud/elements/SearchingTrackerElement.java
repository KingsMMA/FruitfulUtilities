package dev.kingrabbit.fruitfulutilities.hud.elements;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.categories.SearchingTrackerCategory;
import dev.kingrabbit.fruitfulutilities.hud.ElementInfo;
import dev.kingrabbit.fruitfulutilities.hud.HudElement;
import dev.kingrabbit.fruitfulutilities.hud.Serializable;
import dev.kingrabbit.fruitfulutilities.listener.TickListener;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
@ElementInfo(id = "searching_tracker")
public class SearchingTrackerElement extends HudElement {

    @Serializable(id = "x")
    public int x = 5;

    @Serializable(id = "y")
    public int y = 85;

    @Override
    public List<Object> render(float tickDelta) {
        List<Object> result = new ArrayList<>();

        SearchingTrackerCategory category = FruitfulUtilities.getInstance().configManager.getCategory(SearchingTrackerCategory.class);
        if (!(category.enabled && category.mode == 0)) return result;

        HashMap<Integer, Text> drops = TickListener.searchingDrops;
        if (drops.isEmpty() && category.hideIfNoDrops) return result;

        result.add("§aSearching:");

        List<Integer> keysToRemove = new ArrayList<>();
        for (Integer tick : drops.keySet()) {
            int timeSince = Math.abs(TickListener.tick - tick);
            if (timeSince >= 100) {
                keysToRemove.add(tick);
                continue;
            }
            if (timeSince >= 90)
                result.add("<opac>" + ((int) ((100 - timeSince) / 10f * 100f)) + "</opac>");
            result.add(drops.get(tick));
        }
        for (Integer key : keysToRemove) {
            drops.remove(key);
        }

        return result;
    }

}
