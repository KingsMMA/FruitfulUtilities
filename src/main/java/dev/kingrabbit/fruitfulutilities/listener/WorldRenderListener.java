package dev.kingrabbit.fruitfulutilities.listener;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.categories.PathViewerCategory;
import dev.kingrabbit.fruitfulutilities.pathviewer.PathManager;
import me.x150.renderer.event.RenderEvents;
import me.x150.renderer.render.Renderer3d;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.logging.Logger;

public class WorldRenderListener implements RenderEvents.RenderEvent, WorldRenderEvents.End {

    public static final Color UPGRADE_COLOUR = new Color(140, 0, 250);

    @Override
    public void rendered(MatrixStack matrices) {
        PathViewerCategory category = FruitfulUtilities.getInstance().configManager.getCategory(PathViewerCategory.class);

        for (JsonObject upgrade : PathManager.tracking) {
            if (PathManager.purchased.contains(upgrade.get("display").getAsString()) && category.hideIfUnlocked) continue;

            String[] location = upgrade.get("location").getAsString().split(",");
            if (location.length != 3)
                Logger.getGlobal().severe("Unable to parse location of " + upgrade.get("display").getAsString() + " (" + upgrade.get("location").getAsString() + ")");
            int x = Integer.parseInt(location[0]);
            int y = Integer.parseInt(location[1]);
            int z = Integer.parseInt(location[2]);

            Renderer3d.renderThroughWalls();
            Vec3d upgradePos = new Vec3d(x, y, z);
            double distance = MinecraftClient.getInstance().player.getPos().distanceTo(upgradePos);
            int alpha = (int) Math.min(Math.max(Math.pow(2, 0.5 * distance) * 5, 50), 255);
            Renderer3d.renderFilled(matrices, new Color(140, 0, 250, alpha), upgradePos, new Vec3d(1, 1, 1));
            Renderer3d.stopRenderThroughWalls();
//            if (distance >= 5) {
//                RenderUtil.renderBeaconBeam(new BlockPos(x, y, z));
//            }
        }
    }

    @Override
    public void onEnd(WorldRenderContext context) {
        if (!FruitfulUtilities.getInstance().configManager.enabled()) return;

        PathViewerCategory category = FruitfulUtilities.getInstance().configManager.getCategory(PathViewerCategory.class);

        Vec3d playerPos = MinecraftClient.getInstance().player.getPos();
        for (JsonObject upgrade : PathManager.tracking) {
            if (PathManager.purchased.contains(upgrade.get("display").getAsString()) && category.hideIfUnlocked) continue;

            String[] location = upgrade.get("location").getAsString().split(",");
            if (location.length != 3) {
                Logger.getGlobal().severe("Unable to parse location of " + upgrade.get("display").getAsString() + " (" + upgrade.get("location").getAsString() + ")");
            }
            int x = Integer.parseInt(location[0]);
            int y = Integer.parseInt(location[1]);
            int z = Integer.parseInt(location[2]);

            Vec3d upgradePos = new Vec3d(x, y, z);
            double distance = playerPos.distanceTo(upgradePos);
            int alpha = (int) Math.min(Math.max(Math.pow(2, 0.5 * distance) * 5, 50), 255);
            if (distance >= 5) {
//                System.out.println(x);
                context.matrixStack().push();
                context.matrixStack().translate(x - playerPos.x, y - playerPos.y - 0.7, z - playerPos.z);
                RenderSystem.setShaderColor(140, 0, 250, alpha);  // TODO alpha
                BeaconBlockEntityRenderer.renderBeam(
                        context.matrixStack(),
                        context.consumers(),
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
                context.matrixStack().pop();
            }
        }
    }

}
