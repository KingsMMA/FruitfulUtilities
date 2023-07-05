package dev.kingrabbit.fruitfulutilities.config.categories;

import dev.kingrabbit.fruitfulutilities.config.CategoryInfo;
import dev.kingrabbit.fruitfulutilities.config.ConfigCategory;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigBoolean;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigDropdown;

@SuppressWarnings("CanBeFinal")
@CategoryInfo(id = "searching", display = "Searching Tracker")
public class SearchingTrackerCategory extends ConfigCategory {

    @ConfigBoolean(id = "enabled", display = "Enable Category", description = "Enables the Searching Tracker category.")
    public boolean enabled = true;

    @ConfigBoolean(id = "hide", display = "Hide if No Drops", description = "[HUD] If there are currently no drops to display, hide the tracker")
    public boolean hideIfNoDrops = false;

    @ConfigDropdown(id = "mode", display = "Tracker Mode", description = "Determines where the tracker will be displayed.", options = {"HUD", "Chat"})
    public int mode = 0;

}
