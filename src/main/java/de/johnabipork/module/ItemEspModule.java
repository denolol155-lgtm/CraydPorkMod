package de.johnabipork.module;

import de.johnabipork.config.Config;
import de.johnabipork.render.Lines;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.Vec3d;

public class ItemEspModule extends Module {
    private static final int COLOR = 0xFFFFD34D;

    public ItemEspModule() {
        super("item_esp", "Item-ESP", "Markiert am Boden liegende Items. Im Item-Grid ausgewaehlte Items gelten als Filter.");
    }

    @Override
    public void render(WorldRenderContext ctx, Lines lines, MinecraftClient mc) {
        float td = ctx.tickCounter().getTickDelta(true);
        var selected = Config.get().selectedItems;
        for (Entity e : mc.world.getEntities()) {
            if (!(e instanceof ItemEntity ie)) continue;
            if (!selected.isEmpty()) {
                String id = Registries.ITEM.getId(ie.getStack().getItem()).toString();
                if (!selected.contains(id)) continue;
            }
            Vec3d d = ie.getLerpedPos(td).subtract(ie.getPos());
            lines.box(ie.getBoundingBox().offset(d).expand(0.1), COLOR);
        }
    }
}
