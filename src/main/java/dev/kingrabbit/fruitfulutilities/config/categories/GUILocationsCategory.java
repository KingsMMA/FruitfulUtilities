package dev.kingrabbit.fruitfulutilities.config.categories;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.CategoryInfo;
import dev.kingrabbit.fruitfulutilities.config.ConfigCategory;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigButton;
import dev.kingrabbit.fruitfulutilities.hud.HudPositionsScreen;
import net.minecraft.client.MinecraftClient;

@SuppressWarnings("unused")
@CategoryInfo(id = "gui_locations", display = "GUI Locations")
public class GUILocationsCategory extends ConfigCategory {

    @ConfigButton(display = "Edit GUI Locations", description = "Edit the location of all GUI elements.", buttonText = "Edit")
    public Runnable editGuiLocations = () -> MinecraftClient.getInstance().setScreen(new HudPositionsScreen(MinecraftClient.getInstance().currentScreen));

    @ConfigButton(display = "Reset GUI Locations", description = "Reset the location of all GUI elements.", buttonText = "Reset")
    public Runnable resetGuiLocations = () -> FruitfulUtilities.getInstance().hudManager.resetLocations();

}
