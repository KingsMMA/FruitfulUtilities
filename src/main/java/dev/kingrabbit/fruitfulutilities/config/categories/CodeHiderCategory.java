package dev.kingrabbit.fruitfulutilities.config.categories;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.CategoryInfo;
import dev.kingrabbit.fruitfulutilities.config.ConfigCategory;
import dev.kingrabbit.fruitfulutilities.config.IncompatibleMod;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigBoolean;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("CanBeFinal")
@IncompatibleMod(modId = "sodium", description = "Code Hider's Hide Non-Plot Blocks may encounter issues when running with Sodium, including but not limited to the use of F3 + A being needed to reload the state of Hide Non-Plot Blocks.")
@CategoryInfo(id = "code_hider", display = "Code Hider")
public class CodeHiderCategory extends ConfigCategory {

    @ConfigBoolean(id = "enabled", display = "Enable Category", description = "Enables the CodeHider category.  This won't improve network performance, however it is designed to improve FPS.")
    public boolean enabled = true;

    @ConfigBoolean(id = "chests", display = "Hide Chests", description = "Hides chests and signs in the code section, significantly improving FPS.")
    public boolean hideChests = true;

    @ConfigBoolean(id = "all", display = "Hide Non-Plot Blocks", description = "Hides all blocks not on the plot.  Only activates when inside the plot")
    public boolean hideAll = true;

    public static boolean shouldHideBlock(BlockPos pos) {
        if (MinecraftClient.getInstance().player == null) return false;
        if (!FruitfulUtilities.getInstance().configManager.enabled()) return false;
        if (FruitfulUtilities.inPlot(pos) || !FruitfulUtilities.inPlot(MinecraftClient.getInstance().player.getBlockPos()))
            return false;

        CodeHiderCategory category = FruitfulUtilities.getInstance().configManager.getCategory(CodeHiderCategory.class);
        return category.enabled && category.hideAll;
    }

}
