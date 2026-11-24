package de.johnabipork.gui;

import de.johnabipork.config.Config;
import de.johnabipork.module.Module;
import de.johnabipork.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Util;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class MainScreen extends Screen {
    private static final int CELL_W = 46, CELL_H = 38, SIDE_W = 160, ROW_H = 36;

    private int px, py, pw, ph, gx, gy, gw, gh, cols, offsetX;
    private float maxScroll, scroll, scrollTarget, openAnim;
    private long last;
    private String query = "";
    private List<ItemCatalog.Entry> filtered = ItemCatalog.all();
    private float[] hover = new float[ItemCatalog.all().size() + 1];

    public MainScreen() {
        super(Text.literal("Johnabipork"));
    }

    @Override
    protected void init() {
        layout();
    }

    private void layout() {
        pw = Math.min(width - 30, 640);
        ph = Math.min(height - 30, 380);
        px = (width - pw) / 2;
        py = (height - ph) / 2;
        gx = px + SIDE_W + 20;
        gw = pw - SIDE_W - 30;
        gy = py + 74;
        gh = ph - 74 - 10;
        cols = Math.max(1, gw / CELL_W);
        offsetX = (gw - cols * CELL_W) / 2;
    }

    private int masterX() { return px + pw - 14 - 40; }
    private int masterY() { return py + 9; }
    private int rowY(int i) { return py + 52 + i * (ROW_H + 4); }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public void renderBackground(DrawContext ctx, int mx, int my, float delta) {
        // eigener Hintergrund in render()
    }

    @Override
    public void render(DrawContext ctx, int mx, int my, float delta) {
        long now = Util.getMeasuringTimeMs();
        float dt = last == 0 ? 0.016f : Math.min(0.1f, (now - last) / 1000f);
        last = now;
        layout();
        openAnim += (1 - openAnim) * Math.min(1f, dt * 10f);
        float oa = openAnim;
        float k = Math.min(1f, dt * 14f);

        ctx.fill(0, 0, width, height, Theme.alpha(0xB0000000, oa));
        Theme.rrect(ctx, px, py, pw, ph, 8, Theme.alpha(Theme.BG, oa));

        // Header
        ctx.drawText(textRenderer, Text.literal("JOHNABIPORK").formatted(Formatting.BOLD), px + 14, py + 14, Theme.alpha(Theme.TEXT, oa), false);
        ctx.fill(px + 10, py + 36, px + pw - 10, py + 37, Theme.LINE);

        // Master-Schalter oben rechts
        ModuleManager.masterAnim += ((ModuleManager.master ? 1f : 0f) - ModuleManager.masterAnim) * k;
        String state = ModuleManager.master ? "ON" : "OFF";
        int sw = textRenderer.getWidth(state);
        ctx.drawText(textRenderer, state, masterX() - sw - 8, py + 14,
                Theme.lerp(Theme.TEXT_DIM, Theme.ACCENT, ModuleManager.masterAnim), false);
        Theme.toggle(ctx, masterX(), masterY(), 40, 18, ModuleManager.masterAnim);

        // Modul-Liste
        List<Module> mods = ModuleManager.all();
        for (int i = 0; i < mods.size(); i++) {
            Module m = mods.get(i);
            int rx = px + 10, ry = rowY(i), rw = SIDE_W;
            boolean hov = mx >= rx && mx < rx + rw && my >= ry && my < ry + ROW_H;
            m.hover += ((hov ? 1f : 0f) - m.hover) * k;
            m.anim += ((m.enabled ? 1f : 0f) - m.anim) * k;
            Theme.rrect(ctx, rx, ry, rw, ROW_H, 5, Theme.lerp(Theme.CELL, Theme.CELL_HOVER, m.hover));
            ctx.drawText(textRenderer, m.name, rx + 8, ry + 7, Theme.TEXT, false);
            drawScaled(ctx, textRenderer.trimToWidth(m.description, (int) ((rw - 60) / 0.65f)), rx + 8, ry + 21, 0.65f, Theme.TEXT_DIM);
            Theme.toggle(ctx, rx + rw - 36, ry + 10, 28, 14, m.anim);
        }

        // Suchleiste
        int sy = py + 46;
        Theme.rrect(ctx, gx, sy, gw, 20, 5, Theme.CELL);
        Theme.outline(ctx, gx, sy, gw, 20, Theme.alpha(Theme.ACCENT, 0.5f));
        boolean blink = (now / 500) % 2 == 0;
        if (query.isEmpty()) {
            ctx.drawText(textRenderer, "Item suchen...", gx + 8, sy + 6, Theme.TEXT_DIM, false);
        } else {
            ctx.drawText(textRenderer, textRenderer.trimToWidth(query, gw - 70) + (blink ? "_" : ""), gx + 8, sy + 6, Theme.TEXT, false);
        }
        String info = filtered.size() + " Items, " + Config.get().selectedItems.size() + " markiert";
        drawScaled(ctx, info, gx + gw - (int) (textRenderer.getWidth(info) * 0.7f) - 6, sy + 7, 0.7f, Theme.TEXT_DIM);

        // Grid
        int rows = (filtered.size() + cols - 1) / cols;
        maxScroll = Math.max(0, rows * CELL_H - gh);
        scrollTarget = Math.max(0, Math.min(maxScroll, scrollTarget));
        scroll += (scrollTarget - scroll) * Math.min(1f, dt * 14f);

        ItemCatalog.Entry hovered = null;
        ctx.enableScissor(gx, gy, gx + gw, gy + gh);
        int firstRow = Math.max(0, (int) (scroll / CELL_H));
        int lastRow = (int) ((scroll + gh) / CELL_H);
        for (int r = firstRow; r <= lastRow; r++) {
            for (int c = 0; c < cols; c++) {
                int i = r * cols + c;
                if (i >= filtered.size()) break;
                ItemCatalog.Entry e = filtered.get(i);
                int cx = gx + offsetX + c * CELL_W;
                int cy = gy + r * CELL_H - (int) scroll;
                boolean hov = mx >= cx && mx < cx + CELL_W && my >= cy && my < cy + CELL_H
                        && mx >= gx && mx < gx + gw && my >= gy && my < gy + gh;
                if (hov) hovered = e;
                hover[e.index] += ((hov ? 1f : 0f) - hover[e.index]) * k;
                boolean sel = Config.get().selectedItems.contains(e.id);
                Theme.rrect(ctx, cx + 2, cy + 2, CELL_W - 4, CELL_H - 4, 4, Theme.lerp(Theme.CELL, Theme.CELL_HOVER, hover[e.index]));
                if (sel) Theme.outline(ctx, cx + 2, cy + 2, CELL_W - 4, CELL_H - 4, Theme.ACCENT);
                ctx.drawItem(e.stack, cx + (CELL_W - 16) / 2, cy + 5);
                float s = 0.6f;
                String n = textRenderer.trimToWidth(e.name, (int) ((CELL_W - 6) / s));
                int tw = (int) (textRenderer.getWidth(n) * s);
                drawScaled(ctx, n, cx + (CELL_W - tw) / 2, cy + 25, s, sel ? Theme.TEXT : Theme.TEXT_DIM);
            }
        }
        ctx.disableScissor();

        // Scrollbar
        if (maxScroll > 0) {
            int barH = Math.max(20, (int) (gh * (gh / (float) (gh + maxScroll))));
            int barY = gy + (int) ((gh - barH) * (scroll / maxScroll));
            Theme.rrect(ctx, gx + gw - 3, barY, 3, barH, 1, Theme.alpha(Theme.ACCENT, 0.7f));
        }

        if (hovered != null) {
            ctx.drawTooltip(textRenderer, List.of(Text.literal(hovered.name), Text.literal(hovered.id).formatted(Formatting.DARK_GRAY)), mx, my);
        }
    }

    private void drawScaled(DrawContext ctx, String text, int x, int y, float s, int color) {
        ctx.getMatrices().push();
        ctx.getMatrices().translate(x, y, 0);
        ctx.getMatrices().scale(s, s, 1f);
        ctx.drawText(textRenderer, text, 0, 0, color, false);
        ctx.getMatrices().pop();
    }

    private int cellAt(double mx, double my) {
        if (mx < gx || mx >= gx + gw || my < gy || my >= gy + gh) return -1;
        double rx = mx - gx - offsetX;
        if (rx < 0) return -1;
        int c = (int) (rx / CELL_W);
        if (c >= cols) return -1;
        int r = (int) ((my - gy + scroll) / CELL_H);
        int i = r * cols + c;
        return i < filtered.size() ? i : -1;
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (button == 0) {
            if (mx >= masterX() && mx < masterX() + 40 && my >= masterY() && my < masterY() + 18) {
                ModuleManager.setMaster(!ModuleManager.master);
                return true;
            }
            List<Module> mods = ModuleManager.all();
            for (int i = 0; i < mods.size(); i++) {
                int rx = px + 10, ry = rowY(i);
                if (mx >= rx && mx < rx + SIDE_W && my >= ry && my < ry + ROW_H) {
                    mods.get(i).toggle();
                    return true;
                }
            }
            int idx = cellAt(mx, my);
            if (idx >= 0) {
                String id = filtered.get(idx).id;
                var sel = Config.get().selectedItems;
                if (!sel.remove(id)) sel.add(id);
                Config.get().save();
                return true;
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double h, double v) {
        scrollTarget = Math.max(0, Math.min(maxScroll, scrollTarget - (float) v * CELL_H * 1.5f));
        return true;
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        if (chr >= 32 && chr != 127 && query.length() < 40) {
            query += chr;
            refilter();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_BACKSPACE) {
            if (!query.isEmpty()) {
                query = query.substring(0, query.length() - 1);
                refilter();
            }
            return true;
        }
        if (Screen.isPaste(keyCode)) {
            String clip = client.keyboard.getClipboard();
            if (clip != null && !clip.isEmpty()) {
                query = (query + clip.replaceAll("[\\r\\n]", " ")).substring(0, Math.min(40, query.length() + clip.length()));
                refilter();
            }
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void refilter() {
        filtered = ItemCatalog.search(query);
        scroll = 0;
        scrollTarget = 0;
    }

    @Override
    public void removed() {
        Config.get().save();
    }
}
