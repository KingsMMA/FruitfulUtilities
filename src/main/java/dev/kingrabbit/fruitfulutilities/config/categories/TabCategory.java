package dev.kingrabbit.fruitfulutilities.config.categories;

import dev.kingrabbit.fruitfulutilities.config.CategoryInfo;
import dev.kingrabbit.fruitfulutilities.config.ConfigCategory;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigBoolean;

@CategoryInfo(id = "tab", display = "Better Player List")
public class TabCategory extends ConfigCategory {

    @ConfigBoolean(id = "enabled", display = "Enable Category", description = "Enables the Better Player List category, improving the look and feel of the player list.")
    public boolean enabled = true;

    @ConfigBoolean(id = "monarch_gold", display = "Display Monarch as Gold", description = "Display the Monarch's name as gold.")
    public boolean monarchGold = true;

}
