package dev.kingrabbit.fruitfulutilities.listener;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.Keybinds;
import dev.kingrabbit.fruitfulutilities.config.ConfigManager;
import dev.kingrabbit.fruitfulutilities.config.ConfigScreen;
import dev.kingrabbit.fruitfulutilities.config.categories.*;
import dev.kingrabbit.fruitfulutilities.pathviewer.PathScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public class TickListener implements ClientTickEvents.EndTick {

    public static final HashMap<BlockPos, Integer> clicked = new HashMap<>();
    public static int tick = 0;
    public static int searchingUntil = 0;
    public static final HashMap<Integer, Text> searchingDrops = new HashMap<>();

    @Override
    public void onEndTick(MinecraftClient client) {
        FruitfulUtilities fruitfulUtilities = FruitfulUtilities.getInstance();

        Keybinds keybinds = fruitfulUtilities.keybinds;
        if (keybinds.openConfig.wasPressed()) {
            client.setScreen(new ConfigScreen());
            return;
        }

        ConfigManager configManager = fruitfulUtilities.configManager;
        if (!configManager.enabled()) return;

        if (keybinds.openPathViewer.wasPressed()) {
            client.setScreen(new PathScreen());
            return;
        }

        tick += 1;
        if (tick >= 12_000) {
            tick = 0;
            searchingUntil = 0;
            clicked.clear();
        }

        SearchingTrackerCategory searchingCategory = fruitfulUtilities.configManager.getCategory(SearchingTrackerCategory.class);
        if (searchingCategory.enabled) {
            if (searchingCategory.mode == 1 && (searchingUntil == 0 || searchingUntil - 10 < tick)) {
                MutableText last = Text.empty();
                for (Text drop : searchingDrops.values()) {
                    MutableText message = Text.of("§aSearching: ").copy().append(drop);
                    if (message.getString().equals(last.getString())) continue;
                    client.player.sendMessage(message);
                    last = message;
                }
                searchingDrops.clear();
            }
        }
    }

}

