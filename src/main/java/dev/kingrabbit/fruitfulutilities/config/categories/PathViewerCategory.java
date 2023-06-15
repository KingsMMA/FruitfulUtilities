package dev.kingrabbit.fruitfulutilities.config.categories;

import dev.kingrabbit.fruitfulutilities.config.CategoryInfo;
import dev.kingrabbit.fruitfulutilities.config.ConfigCategory;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigBoolean;

@CategoryInfo(id = "path_viewer", display = "Path Viewer")
public class PathViewerCategory extends ConfigCategory {

    @ConfigBoolean(id = "cumulative", display = "Cumulative Cost", description = "Includes the cost of all required upgrades to the cost of the selected upgrade.")
    public boolean cumulative = true;

}
