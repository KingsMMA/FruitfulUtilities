package dev.kingrabbit.fruitfulutilities.config.categories;

import dev.kingrabbit.fruitfulutilities.config.CategoryInfo;
import dev.kingrabbit.fruitfulutilities.config.ConfigCategory;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigButton;
import dev.kingrabbit.fruitfulutilities.hud.HudPositionsScreen;
import net.minecraft.client.MinecraftClient;

@CategoryInfo(id = "gui_locations", display = "GUI Locations")
public class GUILocationsCategory extends ConfigCategory {

    @ConfigButton(display = "Edit GUI Locations", description = "Edit the location of all GUI elements.", buttonText = "Edit")
    public Runnable editGuiLocations = () -> {
        MinecraftClient.getInstance().setScreen(new HudPositionsScreen(MinecraftClient.getInstance().currentScreen));
    };

}
