package dev.kingrabbit.fruitfulutilities;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Keybinds {

    public final KeyBinding openConfig;
    public final KeyBinding openPathViewer;

    public Keybinds() {
        openConfig = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open Config",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                "Fruitful Utilities"
        ));
        openPathViewer = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open Path Viewer",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "Fruitful Utilities"
        ));
    }

}
