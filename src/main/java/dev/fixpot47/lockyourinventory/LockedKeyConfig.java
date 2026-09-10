package dev.fixpot47.lockyourinventory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

public final class LockedKeyConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("lockyourinventory.json");

    private static Data data = new Data();

    private LockedKeyConfig() {
    }

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            data = new Data();
            return;
        }

        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            Data loaded = GSON.fromJson(reader, Data.class);
            data = loaded != null ? loaded : new Data();
            if (data.locked == null) {
                data.locked = new LinkedHashSet<>();
            }
        } catch (Exception ignored) {
            data = new Data();
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException ignored) {
        }
    }

    public static boolean isLocked(KeyMapping mapping) {
        if (mapping == null || LockYourInventoryClient.OPEN_MENU_KEY_NAME.equals(mapping.getName())) {
            return false;
        }
        return data.locked.contains(mapping.getName());
    }

    public static void toggle(KeyMapping mapping) {
        setLocked(mapping, !isLocked(mapping));
    }

    public static void setLocked(KeyMapping mapping, boolean locked) {
        if (mapping == null || LockYourInventoryClient.OPEN_MENU_KEY_NAME.equals(mapping.getName())) {
            return;
        }

        if (locked) {
            mapping.setDown(false);
            while (mapping.consumeClick()) {
                // Drain queued clicks before the binding becomes locked.
            }
            data.locked.add(mapping.getName());
        } else {
            data.locked.remove(mapping.getName());
        }
        save();
    }

    private static final class Data {
        private Set<String> locked = new LinkedHashSet<>();
    }
}
