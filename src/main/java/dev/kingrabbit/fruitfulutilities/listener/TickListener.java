package dev.kingrabbit.fruitfulutilities.listener;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.Keybinds;
import dev.kingrabbit.fruitfulutilities.config.ConfigManager;
import dev.kingrabbit.fruitfulutilities.config.ConfigScreen;
import dev.kingrabbit.fruitfulutilities.config.categories.SearchingTrackerCategory;
import dev.kingrabbit.fruitfulutilities.pathviewer.PathManager;
import dev.kingrabbit.fruitfulutilities.pathviewer.PathScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.HashMap;

public class TickListener implements ClientTickEvents.EndTick {

    public static final HashMap<BlockPos, Integer> clicked = new HashMap<>();
    public static final HashMap<Integer, Text> searchingDrops = new HashMap<>();
    public static int tick = 0;
    public static int searchingUntil = 0;
    public static int testUndergroundAt = -1;

    @Override
    public void onEndTick(MinecraftClient client) {
        FruitfulUtilities fruitfulUtilities = FruitfulUtilities.getInstance();

        Keybinds keybinds = fruitfulUtilities.keybinds;
        if (keybinds.openConfig.wasPressed()) {
            client.setScreen(new ConfigScreen());
            return;
        } else if (keybinds.openPathViewer.wasPressed()) {
            client.setScreen(new PathScreen());
            return;
        }

        ConfigManager configManager = fruitfulUtilities.configManager;
        if (!configManager.enabled()) return;

        tick += 1;
        if (tick >= 12_000) {
            tick = 0;
            searchingUntil = 0;
            clicked.clear();
        }

        if (tick >= testUndergroundAt && testUndergroundAt != -1) {
            testUndergroundAt = -1;
            if (client.world != null) {
                if (!PathManager.purchasedIds.contains("upgrade_town_farm")) {
                    if (client.world.getBlockState(new BlockPos(-929, 37, -4193)).isOf(Blocks.AIR)) {
                        PathManager.purchasedIds.add("upgrade_town_farm");
                    }
                }
                if (!PathManager.purchasedIds.contains("upgrade_town_second_wall")) {
                    if (client.world.getBlockState(new BlockPos(-938, 37, -4200)).isOf(Blocks.AIR)) {
                        PathManager.purchasedIds.add("upgrade_town_second_wall");
                    }
                }
            }
        }

        SearchingTrackerCategory searchingCategory = fruitfulUtilities.configManager.getCategory(SearchingTrackerCategory.class);
        if (searchingCategory.enabled) {
            if (searchingCategory.mode == 1 && (searchingUntil == 0 || searchingUntil - 10 < tick) && client.player != null) {
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

