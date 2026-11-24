package de.johnabipork.gui;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Alle Vanilla-Items aus der Registry (Texturen/Namen kommen direkt aus dem Spiel). */
public final class ItemCatalog {
    public static final class Entry {
        public final int index;
        public final Item item;
        public final ItemStack stack;
        public final String id;
        public final String name;
        final String search;

        Entry(int index, Item item) {
            this.index = index;
            this.item = item;
            this.stack = new ItemStack(item);
            this.id = Registries.ITEM.getId(item).toString();
            this.name = item.getName().getString();
            this.search = (name + " " + id).toLowerCase(Locale.ROOT);
        }
    }

    private static List<Entry> all;

    private ItemCatalog() {}

    public static List<Entry> all() {
        if (all == null) {
            all = new ArrayList<>();
            int i = 0;
            for (Item item : Registries.ITEM) {
                if (item == Items.AIR) continue;
                all.add(new Entry(i++, item));
            }
        }
        return all;
    }

    public static List<Entry> search(String query) {
        String q = query.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) return all();
        String[] tokens = q.split("\\s+");
        List<Entry> out = new ArrayList<>();
        outer:
        for (Entry e : all()) {
            for (String t : tokens) {
                if (!e.search.contains(t)) continue outer;
            }
            out.add(e);
        }
        return out;
    }
}
