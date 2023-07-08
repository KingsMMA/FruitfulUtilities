package dev.kingrabbit.fruitfulutilities.config.categories;

import dev.kingrabbit.fruitfulutilities.config.CategoryInfo;
import dev.kingrabbit.fruitfulutilities.config.ConfigCategory;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigBoolean;

@SuppressWarnings("CanBeFinal")
@CategoryInfo(id = "path_viewer", display = "Path Viewer")
public class PathViewerCategory extends ConfigCategory {

    @ConfigBoolean(id = "hud", display = "Show on HUD", description = "Shows all currently tracked upgrades on the HUD.")
    public boolean hud = true;

    @ConfigBoolean(id = "cumulative", display = "Cumulative Cost", description = "Includes the cost of all required upgrades to the cost of the selected upgrade.")
    public boolean cumulative = true;

    @ConfigBoolean(id = "hide_if_unlocked", display = "Hide if Purchased", description = "Hides tracked upgrades if they have already been purchased.")
    public boolean hideIfUnlocked = false;

}
