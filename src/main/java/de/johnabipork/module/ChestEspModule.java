package de.johnabipork.module;

import de.johnabipork.render.Lines;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.WorldChunk;

public class ChestEspModule extends Module {
    private static final int COLOR = 0xFF33FF55;

    public ChestEspModule() {
        super("chest_esp", "Truhen-ESP", "Gruene Umrandung und Linie bis zum Boden bei Truhen, Fassern, Shulkern.");
    }

    @Override
    public void render(WorldRenderContext ctx, Lines lines, MinecraftClient mc) {
        int radius = Math.min(mc.options.getClampedViewDistance(), 10);
        ChunkPos cp = mc.player.getChunkPos();
        int bottom = mc.world.getBottomY();
        for (int cx = cp.x - radius; cx <= cp.x + radius; cx++) {
            for (int cz = cp.z - radius; cz <= cp.z + radius; cz++) {
                WorldChunk chunk = mc.world.getChunkManager().getWorldChunk(cx, cz);
                if (chunk == null) continue;
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (!(be instanceof ChestBlockEntity || be instanceof EnderChestBlockEntity
                            || be instanceof BarrelBlockEntity || be instanceof ShulkerBoxBlockEntity)) continue;
                    BlockPos pos = be.getPos();
                    lines.box(new Box(pos), COLOR);
                    lines.line(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                            pos.getX() + 0.5, bottom, pos.getZ() + 0.5, COLOR);
                }
            }
        }
    }
}
