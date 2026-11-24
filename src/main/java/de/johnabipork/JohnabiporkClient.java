package de.johnabipork;

import de.johnabipork.config.Config;
import de.johnabipork.gui.MainScreen;
import de.johnabipork.module.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class JohnabiporkClient implements ClientModInitializer {
    public static KeyBinding openKey;

    @Override
    public void onInitializeClient() {
        Config.load();
        ModuleManager.init();

        openKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.johnabipork.open", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_SHIFT, "category.johnabipork"));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            while (openKey.wasPressed()) {
                if (mc.world != null && mc.currentScreen == null) {
                    mc.setScreen(new MainScreen());
                }
            }
        });

        WorldRenderEvents.LAST.register(ModuleManager::render);
    }
}
