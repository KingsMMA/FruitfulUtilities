package dev.kingrabbit.fruitfulutilities.listener;

import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.ConfigScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class TickListener implements ClientTickEvents.EndTick {

    @Override
    public void onEndTick(MinecraftClient client) {
        if (FruitfulUtilities.getInstance().keybinds.openConfig.isPressed()) {
            client.setScreen(new ConfigScreen());
        }
    }

}

