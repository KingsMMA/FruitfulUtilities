package dev.kingrabbit.fruitfulutilities.config.categories;

import dev.kingrabbit.fruitfulutilities.config.CategoryInfo;
import dev.kingrabbit.fruitfulutilities.config.ConfigCategory;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigBoolean;

@SuppressWarnings("CanBeFinal")
@CategoryInfo(id = "upgrade_beacons", display = "Upgrade Beacons")
public class UpgradeBeaconCategory extends ConfigCategory {

    @ConfigBoolean(id = "highlight", display = "Highlight Tracked Upgrades", description = "Places a highlight around all tracked upgrades, visible through all other blocks.")
    public boolean highlight = true;

    @ConfigBoolean(id = "beacon", display = "Upgrade Beacons", description = "Renders a beacon on all tracked upgrades, disappearing if you move into range.")
    public boolean beacon = true;

}
