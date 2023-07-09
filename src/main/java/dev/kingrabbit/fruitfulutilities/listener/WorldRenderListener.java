package dev.kingrabbit.fruitfulutilities.listener;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.pathviewer.PathManager;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BeaconBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

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

            int rawX = Integer.parseInt(location[0]);
            int rawY = Integer.parseInt(location[1]);
            int rawZ = Integer.parseInt(location[2]);

            Vec3d upgradePos = new Vec3d(rawX, rawY, rawZ);
            MatrixStack matrices = context.matrixStack();
            Matrix4f matrix = matrices.peek().getPositionMatrix();
            Vec3d cameraPos = MinecraftClient.getInstance().gameRenderer.getCamera().getPos();
            Vec3d offset = upgradePos.subtract(cameraPos);

            double distance = cameraPos.distanceTo(upgradePos);
            float red = 140 / 255f;
            float green = 0f;
            float blue = 250 / 255f;
            float alpha = ((int) Math.min(Math.max(Math.pow(2, 0.5 * distance) * 5, 50), 255)) / 255f;

            if (true) {
                float x1, x2, y1, y2, z1, z2;
                x1 = x2 = (float) offset.x;
                y1 = y2 = (float) offset.y;
                z1 = z2 = (float) offset.z;
                x2++;
                y2++;
                z2++;

                RenderSystem.enableBlend();
                RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                RenderSystem.disableDepthTest();
                RenderSystem.setShader(GameRenderer::getPositionColorProgram);

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();

                buffer.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR);
                buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).next();

                buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).next();

                buffer.vertex(matrix, x2, y2, z2).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).next();

                buffer.vertex(matrix, x2, y2, z1).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).next();

                buffer.vertex(matrix, x1, y2, z1).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x1, y2, z2).color(red, green, blue, alpha).next();

                buffer.vertex(matrix, x1, y1, z1).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x2, y1, z1).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x2, y1, z2).color(red, green, blue, alpha).next();
                buffer.vertex(matrix, x1, y1, z2).color(red, green, blue, alpha).next();

                BufferRenderer.drawWithGlobalProgram(buffer.end());

                RenderSystem.enableCull();
                RenderSystem.disableBlend();
                RenderSystem.enableDepthTest();
            }

            if (distance >= 5) {
                matrices.push();
                matrices.translate(offset.x, offset.y + 1, offset.z);
                BeaconBlockEntityRenderer.renderBeam(
                        matrices,
                        Objects.requireNonNull(context.consumers()),
                        BeaconBlockEntityRenderer.BEAM_TEXTURE,
                        context.tickDelta(),
                        1.0f,
                        context.world().getTime(),
                        0,
                        255,
                        new float[]{red, green, blue},
                        0.2f,
                        0f
                );
                matrices.pop();
            }
        }
    }

}
