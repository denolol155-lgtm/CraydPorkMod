package de.johnabipork.render;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.gl.ShaderProgramKeys;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;

/** Zeichnet Linien/Boxen in Weltkoordinaten, auch durch Wände sichtbar. */
public final class Lines {
    private final Matrix4f matrix;
    private final Vec3d cam;
    private BufferBuilder buffer;

    private Lines(Matrix4f matrix, Vec3d cam) {
        this.matrix = matrix;
        this.cam = cam;
    }

    public static Lines begin(WorldRenderContext ctx) {
        return new Lines(ctx.matrixStack().peek().getPositionMatrix(), ctx.camera().getPos());
    }

    public void line(double x1, double y1, double z1, double x2, double y2, double z2, int argb) {
        if (buffer == null) {
            buffer = Tessellator.getInstance().begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        }
        float a = ((argb >>> 24) & 255) / 255f;
        float r = ((argb >> 16) & 255) / 255f;
        float g = ((argb >> 8) & 255) / 255f;
        float b = (argb & 255) / 255f;
        buffer.vertex(matrix, (float) (x1 - cam.x), (float) (y1 - cam.y), (float) (z1 - cam.z)).color(r, g, b, a);
        buffer.vertex(matrix, (float) (x2 - cam.x), (float) (y2 - cam.y), (float) (z2 - cam.z)).color(r, g, b, a);
    }

    public void box(Box bx, int argb) {
        double x1 = bx.minX, y1 = bx.minY, z1 = bx.minZ, x2 = bx.maxX, y2 = bx.maxY, z2 = bx.maxZ;
        // unten
        line(x1, y1, z1, x2, y1, z1, argb);
        line(x2, y1, z1, x2, y1, z2, argb);
        line(x2, y1, z2, x1, y1, z2, argb);
        line(x1, y1, z2, x1, y1, z1, argb);
        // oben
        line(x1, y2, z1, x2, y2, z1, argb);
        line(x2, y2, z1, x2, y2, z2, argb);
        line(x2, y2, z2, x1, y2, z2, argb);
        line(x1, y2, z2, x1, y2, z1, argb);
        // Pfosten
        line(x1, y1, z1, x1, y2, z1, argb);
        line(x2, y1, z1, x2, y2, z1, argb);
        line(x2, y1, z2, x2, y2, z2, argb);
        line(x1, y1, z2, x1, y2, z2, argb);
    }

    public void end() {
        if (buffer == null) return;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(ShaderProgramKeys.POSITION_COLOR);
        BufferRenderer.drawWithGlobalProgram(buffer.end());
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        buffer = null;
    }
}
