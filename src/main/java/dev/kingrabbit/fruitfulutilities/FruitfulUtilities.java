package dev.kingrabbit.fruitfulutilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.kingrabbit.fruitfulutilities.config.ConfigManager;
import dev.kingrabbit.fruitfulutilities.hud.HudManager;
import dev.kingrabbit.fruitfulutilities.hud.HudRenderer;
import dev.kingrabbit.fruitfulutilities.listener.TickListener;
import dev.kingrabbit.fruitfulutilities.listener.WorldRenderListener;
import dev.kingrabbit.fruitfulutilities.pathviewer.PathManager;
import dev.kingrabbit.fruitfulutilities.pathviewer.PathScreen;
import me.x150.renderer.event.RenderEvents;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

@Environment(EnvType.CLIENT)
public class FruitfulUtilities implements ClientModInitializer {

    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();


    private static FruitfulUtilities instance;
    public String monarchNametag = "";
    public ConfigManager configManager;
    public HudManager hudManager;
    public Keybinds keybinds;

    public static FruitfulUtilities getInstance() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        configManager = new ConfigManager();
        hudManager = new HudManager();
        keybinds = new Keybinds();

        PathManager.loadPaths();

        ClientTickEvents.END_CLIENT_TICK.register(new TickListener());
        WorldRenderEvents.END.register(new WorldRenderListener());
        RenderEvents.WORLD.register(new WorldRenderListener());
        HudRenderCallback.EVENT.register(new HudRenderer());

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            LiteralCommandNode<FabricClientCommandSource> commandNode = dispatcher.register(
                    ClientCommandManager.literal("reload_paths")
                            .executes(context -> {
                                context.getSource().sendFeedback(Text.of("§aReloading paths..."));
                                PathManager.loadPaths();
                                return 1;
                            }));
        });
    }

    public void restartRun() {
        PathManager.purchased.clear();
        PathManager.tracking.clear();
        PathScreen.sections.clear();
        PathScreen.sections.put("beginnings", new float[]{-19284, -64, 1});
        PathScreen.selectedElement.clear();
        PathScreen.section = "beginnings";
    }



    public static boolean inPlot(BlockPos pos) {
        return inPlot(pos.getX(), pos.getZ());
    }

    public static boolean inPlot(float x, float z) {
        return -675 >= x && x >= -975 &&
                -3970 >= z && z >= -4270;
    }

}
