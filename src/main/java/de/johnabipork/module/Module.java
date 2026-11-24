package de.johnabipork.module;

import de.johnabipork.config.Config;
import de.johnabipork.render.Lines;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;

/** Basisklasse: Neue Funktion = neue Unterklasse + eine Zeile in ModuleManager.init(). */
public abstract class Module {
    public final String id;
    public final String name;
    public final String description;
    public boolean enabled;
    /** Animationswert fuer den Schalter im GUI (0..1). */
    public float anim;
    /** Animationswert fuer den Hover-Effekt (0..1). */
    public float hover;

    protected Module(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
        Config.get().modules.put(id, value);
        Config.get().save();
    }

    public void toggle() {
        setEnabled(!enabled);
    }

    /** Wird jeden Frame aufgerufen, solange Master + Modul an sind. */
    public abstract void render(WorldRenderContext ctx, Lines lines, MinecraftClient mc);
}
