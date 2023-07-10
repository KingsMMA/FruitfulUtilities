package dev.kingrabbit.fruitfulutilities.config.categories;

import dev.kingrabbit.fruitfulutilities.config.CategoryInfo;
import dev.kingrabbit.fruitfulutilities.config.ConfigCategory;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigBoolean;

@SuppressWarnings("CanBeFinal")
@CategoryInfo(id = "scoreboard", display = "Fancy Scoreboard")
public class FancyScoreboardCategory extends ConfigCategory {

    @ConfigBoolean(id = "fancy", display = "Balance Commas", description = "Adds commas to balances in the scoreboard, improving readability.")
    public boolean fancy = true;


    @ConfigBoolean(id = "numbers", display = "Hide Numbers", description = "Hides the red numbers on the side of the scoreboard.")
    public boolean numbers = true;

}
