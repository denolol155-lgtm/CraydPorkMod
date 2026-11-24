package de.johnabipork.module;

import de.johnabipork.render.Lines;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class PlayerEspModule extends Module {
    private static final int COLOR = 0xFFFF4D4D;

    public PlayerEspModule() {
        super("player_esp", "Spieler-ESP", "Rote Box um andere Spieler, auch durch Waende.");
    }

    @Override
    public void render(WorldRenderContext ctx, Lines lines, MinecraftClient mc) {
        float td = ctx.tickCounter().getTickDelta(true);
        for (AbstractClientPlayerEntity p : mc.world.getPlayers()) {
            if (p == mc.player || p.isSpectator()) continue;
            Vec3d d = p.getLerpedPos(td).subtract(p.getPos());
            lines.box(p.getBoundingBox().offset(d), COLOR);
        }
    }
}
