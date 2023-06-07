package dev.kingrabbit.fruitfulutilities.config.categories;

import dev.kingrabbit.fruitfulutilities.config.CategoryInfo;
import dev.kingrabbit.fruitfulutilities.config.ConfigCategory;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigBoolean;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigDropdown;

@CategoryInfo(id = "searching", display = "Searching Tracker")
public class SearchingTrackerCategory extends ConfigCategory {

    @ConfigBoolean(id = "enabled", display = "Enable Category", description = "Enables the Searching Tracker category.")
    public boolean enabled = true;

    @ConfigDropdown(id = "mode", display = "Tracker Mode", description = "Determines where the tracker will be displayed.", options = {"HUD", "Chat"})
    public int mode = 0;

}
