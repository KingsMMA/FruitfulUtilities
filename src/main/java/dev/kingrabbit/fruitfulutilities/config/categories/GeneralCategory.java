package dev.kingrabbit.fruitfulutilities.config.categories;

import dev.kingrabbit.fruitfulutilities.config.CategoryInfo;
import dev.kingrabbit.fruitfulutilities.config.ConfigCategory;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigBoolean;

@SuppressWarnings("CanBeFinal")
@CategoryInfo(id = "general", display = "General")
public class GeneralCategory extends ConfigCategory {

    @ConfigBoolean(id = "enabled", display = "Enable Mod", description = "Enables the FruitfulUtilities mod.")
    public boolean enabled = true;

    @ConfigBoolean(id = "game_detection", display = "Only Enable on Melon King", description = "Automatically detects if you are on the game, disabling the mod elsewhere.")
    public boolean gameDetection = false;

}
