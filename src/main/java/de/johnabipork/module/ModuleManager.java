package de.johnabipork.module;

import de.johnabipork.config.Config;
import de.johnabipork.render.Lines;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;

import java.util.ArrayList;
import java.util.List;

public final class ModuleManager {
    private static final List<Module> MODULES = new ArrayList<>();
    public static boolean master = true;
    public static float masterAnim = 1f;

    private ModuleManager() {}

    public static void init() {
        register(new PlayerEspModule());
        register(new ChestEspModule());
        register(new ItemEspModule());
        // Neue Module hier registrieren.

        Config c = Config.get();
        master = c.master;
        masterAnim = master ? 1f : 0f;
        for (Module m : MODULES) {
            m.enabled = c.modules.getOrDefault(m.id, false);
            m.anim = m.enabled ? 1f : 0f;
        }
    }

    public static void register(Module m) {
        MODULES.add(m);
    }

    public static List<Module> all() {
        return MODULES;
    }

    public static void setMaster(boolean value) {
        master = value;
        Config.get().master = value;
        Config.get().save();
    }

    public static void render(WorldRenderContext ctx) {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (!master || mc.world == null || mc.player == null) return;
        Lines lines = Lines.begin(ctx);
        for (Module m : MODULES) {
            if (!m.enabled) continue;
            try {
                m.render(ctx, lines, mc);
            } catch (Exception e) {
                m.setEnabled(false);
                e.printStackTrace();
            }
        }
        lines.end();
    }
}
