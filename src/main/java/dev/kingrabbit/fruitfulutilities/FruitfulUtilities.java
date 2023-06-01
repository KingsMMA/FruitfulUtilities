package dev.kingrabbit.fruitfulutilities;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.kingrabbit.fruitfulutilities.config.ConfigManager;
import dev.kingrabbit.fruitfulutilities.config.ConfigScreen;
import dev.kingrabbit.fruitfulutilities.listener.TickListener;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class FruitfulUtilities implements ClientModInitializer {

    private static FruitfulUtilities instance;
    public boolean inMelonKing = false;
    public String monarchNametag = "";
    public ConfigManager configManager;
    public Keybinds keybinds;

    public static FruitfulUtilities getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        configManager = new ConfigManager();
        keybinds = new Keybinds();

        ClientTickEvents.END_CLIENT_TICK.register(new TickListener());

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LiteralCommandNode<FabricClientCommandSource> commandNode = dispatcher.register(
                    ClientCommandManager.literal("fu")
                            .executes(context -> {
                                context.getSource().sendFeedback(Text.of("Test"));
                                MinecraftClient.getInstance().setScreen(new ConfigScreen());
                                context.getSource().sendFeedback(Text.of("Test2"));
                                return 1;
                            }));
            dispatcher.register(ClientCommandManager.literal("fruitfulutilities").redirect(commandNode));
            dispatcher.register(ClientCommandManager.literal("fruitfulutils").redirect(commandNode));
            dispatcher.register(ClientCommandManager.literal("fruit").redirect(commandNode));
        });
    }

}
