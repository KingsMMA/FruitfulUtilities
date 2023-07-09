package dev.kingrabbit.fruitfulutilities.listener;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.pathviewer.PathManager;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.Objects;
import java.util.logging.Logger;

public class WorldRenderListener implements WorldRenderEvents.End {

    @Override
    public void onEnd(WorldRenderContext context) {
        if (MinecraftClient.getInstance().player == null) return;
        if (!FruitfulUtilities.getInstance().configManager.enabled()) return;

        Vec3d playerPos = MinecraftClient.getInstance().player.getPos();
        for (JsonObject upgrade : PathManager.allTracked()) {

            String[] location = upgrade.get("location").getAsString().split(",");
            if (location.length != 3)
                Logger.getGlobal().severe("Unable to parse location of " + upgrade.get("display").getAsString() + " (" + upgrade.get("location").getAsString() + ")");
            int x = Integer.parseInt(location[0]);
            int y = Integer.parseInt(location[1]);
            int z = Integer.parseInt(location[2]);

            Vec3d upgradePos = new Vec3d(x, y, z);
            double distance = playerPos.distanceTo(upgradePos);
            int alpha = (int) Math.min(Math.max(Math.pow(2, 0.5 * distance) * 5, 50), 255);

            MatrixStack matrices = context.matrixStack();
            if (distance >= 5) {
                matrices.push();
                matrices.translate(x - playerPos.x, y - playerPos.y - 0.7, z - playerPos.z);
                BeaconBlockEntityRenderer.renderBeam(
                        matrices,
                        Objects.requireNonNull(context.consumers()),
                        BeaconBlockEntityRenderer.BEAM_TEXTURE,
                        context.tickDelta(),
                        1.0f,
                        context.world().getTime(),
                        0,
                        255,
                        new float[]{255, 255, 255},
                        0.2f,
                        0f
                );
                matrices.pop();
            }
        }
    }

}
