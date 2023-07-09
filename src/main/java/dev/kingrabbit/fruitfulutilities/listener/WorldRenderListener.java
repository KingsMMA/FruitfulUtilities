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
            int x1 = Integer.parseInt(location[0]);
            int y1 = Integer.parseInt(location[1]);
            int z1 = Integer.parseInt(location[2]);
            int x2 = x1 + 1;
            int y2 = y1 + 1;
            int z2 = z1 + 1;

            Vec3d upgradePos = new Vec3d(x, y, z);
            double distance = playerPos.distanceTo(upgradePos);
            int alpha = (int) Math.min(Math.max(Math.pow(2, 0.5 * distance) * 5, 50), 255);

            Vec3d offset = upgradePos.subtract(MinecraftClient.getInstance().gameRenderer.getCamera().getPos());

            MatrixStack matrices = context.matrixStack();
            if (distance >= 5) {
                matrices.push();
                matrices.translate(offset.x, offset.y, offset.z);
                RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha / 255f);
                BeaconBlockEntityRenderer.renderBeam(
                        matrices,
                        Objects.requireNonNull(context.consumers()),
                        BeaconBlockEntityRenderer.BEAM_TEXTURE,
                        context.tickDelta(),
                        1.0f,
                        context.world().getTime(),
                        0,
                        255,
                        new float[]{140 / 255f, 0 / 255f, 250 / 255f},
                        0.2f,
                        0f
                );
                matrices.pop();
            }
        }
    }

}
