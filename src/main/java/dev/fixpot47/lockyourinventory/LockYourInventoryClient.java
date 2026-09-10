package dev.fixpot47.lockyourinventory;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class LockYourInventoryClient implements ClientModInitializer {
    public static final String MOD_ID = "lockyourinventory";
    public static final String OPEN_MENU_KEY_NAME = "key.lockyourinventory.open_menu";

    private static final KeyMapping.Category CATEGORY = KeyMapping.Category.register(
        Identifier.fromNamespaceAndPath(MOD_ID, "controls")
    );

    private static KeyMapping openMenuKey;

    @Override
    public void onInitializeClient() {
        LockedKeyConfig.load();

        openMenuKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
            OPEN_MENU_KEY_NAME,
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_I,
            CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                client.gui.setScreen(new LockYourInventoryScreen(client.gui.screen()));
            }
        });
    }
}
