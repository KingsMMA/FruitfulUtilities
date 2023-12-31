package dev.kingrabbit.fruitfulutilities.config.categories;

import dev.kingrabbit.fruitfulutilities.config.CategoryInfo;
import dev.kingrabbit.fruitfulutilities.config.ConfigCategory;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigBoolean;

@SuppressWarnings("CanBeFinal")
@CategoryInfo(id = "auction_timer", display = "Auction Timer")
public class AuctionTimerCategory extends ConfigCategory {

    @ConfigBoolean(id = "enabled", display = "Enable Timer", description = "Enables the Auction Timer, appearing on your HUD.")
    public boolean enabled = true;

    @ConfigBoolean(id = "alert", display = "Always Send Alert", description = "Always send an alert when auctions start, even if disabled on Melon King.")
    public boolean alert = true;


    @ConfigBoolean(id = "warning", display = "Always Send Warning", description = "Always send a warning 60 seconds before auctions start, even if disabled on Melon King.")
    public boolean warning = true;

}
