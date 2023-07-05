package dev.kingrabbit.fruitfulutilities.config.categories;

import dev.kingrabbit.fruitfulutilities.config.CategoryInfo;
import dev.kingrabbit.fruitfulutilities.config.ConfigCategory;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigBoolean;

@SuppressWarnings("CanBeFinal")
@CategoryInfo(id = "code_hider", display = "Code Hider")
public class CodeHiderCategory extends ConfigCategory {

    @ConfigBoolean(id = "enabled", display = "Enable Category", description = "Enables the CodeHider category.  This won't improve network performance, however it is designed to improve FPS.")
    public boolean enabled = true;

    @ConfigBoolean(id = "chests", display = "Hide Chests", description = "Hides chests and signs in the code section, significantly improving FPS.")
    public boolean hideChests = true;

    @ConfigBoolean(id = "all", display = "Hide Non-Plot Blocks", description = "Hides all blocks not on the plot.  Only activates when inside the plot")
    public boolean hideAll = true;

}
