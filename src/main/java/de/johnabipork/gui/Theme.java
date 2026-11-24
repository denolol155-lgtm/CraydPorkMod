package de.johnabipork.gui;

import net.minecraft.client.gui.DrawContext;

/** Farben und kleine Zeichenhelfer. Hier das Design zentral aendern. */
public final class Theme {
    public static final int BG = 0xF2101018;
    public static final int PANEL = 0xFF181824;
    public static final int CELL = 0xFF202030;
    public static final int CELL_HOVER = 0xFF2E2E48;
    public static final int ACCENT = 0xFF7C5CFF;
    public static final int OFF = 0xFF3A3A4C;
    public static final int TEXT = 0xFFEDEDF5;
    public static final int TEXT_DIM = 0xFF8C8CA6;
    public static final int LINE = 0xFF2A2A3C;

    private Theme() {}

    public static int lerp(int a, int b, float t) {
        t = Math.max(0f, Math.min(1f, t));
        int aa = (int) (((a >>> 24) & 255) * (1 - t) + ((b >>> 24) & 255) * t);
        int ar = (int) (((a >> 16) & 255) * (1 - t) + ((b >> 16) & 255) * t);
        int ag = (int) (((a >> 8) & 255) * (1 - t) + ((b >> 8) & 255) * t);
        int ab = (int) ((a & 255) * (1 - t) + (b & 255) * t);
        return (aa << 24) | (ar << 16) | (ag << 8) | ab;
    }

    public static int alpha(int argb, float f) {
        int a = (int) (((argb >>> 24) & 255) * Math.max(0f, Math.min(1f, f)));
        return (a << 24) | (argb & 0xFFFFFF);
    }

    public static void rrect(DrawContext c, int x, int y, int w, int h, int r, int color) {
        r = Math.min(r, Math.min(w, h) / 2);
        if (r <= 0) {
            c.fill(x, y, x + w, y + h, color);
            return;
        }
        c.fill(x, y + r, x + w, y + h - r, color);
        for (int i = 0; i < r; i++) {
            double dy = r - i - 0.5;
            int inset = (int) Math.ceil(r - Math.sqrt(r * r - dy * dy));
            c.fill(x + inset, y + i, x + w - inset, y + i + 1, color);
            c.fill(x + inset, y + h - 1 - i, x + w - inset, y + h - i, color);
        }
    }

    public static void outline(DrawContext c, int x, int y, int w, int h, int color) {
        c.fill(x, y, x + w, y + 1, color);
        c.fill(x, y + h - 1, x + w, y + h, color);
        c.fill(x, y, x + 1, y + h, color);
        c.fill(x + w - 1, y, x + w, y + h, color);
    }

    public static void toggle(DrawContext c, int x, int y, int w, int h, float anim) {
        rrect(c, x, y, w, h, h / 2, lerp(OFF, ACCENT, anim));
        int k = h - 4;
        int kx = x + 2 + (int) ((w - k - 4) * anim);
        rrect(c, kx, y + 2, k, k, k / 2, 0xFFFFFFFF);
    }
}
