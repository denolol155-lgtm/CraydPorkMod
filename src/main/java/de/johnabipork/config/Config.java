package de.johnabipork.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/** Speichert Schalter und markierte Items in config/johnabipork.json. */
public final class Config {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static Config instance = new Config();

    public boolean master = true;
    public Map<String, Boolean> modules = new HashMap<>();
    public Set<String> selectedItems = new LinkedHashSet<>();

    private static Path path() {
        return FabricLoader.getInstance().getConfigDir().resolve("johnabipork.json");
    }

    public static Config get() {
        return instance;
    }

    public static void load() {
        try {
            Path p = path();
            if (Files.exists(p)) {
                Config c = GSON.fromJson(Files.readString(p), Config.class);
                if (c != null) {
                    if (c.modules == null) c.modules = new HashMap<>();
                    if (c.selectedItems == null) c.selectedItems = new LinkedHashSet<>();
                    instance = c;
                }
            }
        } catch (Exception ignored) {
            instance = new Config();
        }
    }

    public void save() {
        try {
            Files.writeString(path(), GSON.toJson(this));
        } catch (IOException ignored) {
        }
    }
}
