package dev.fixpot47.lockyourinventory;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Arrays;

public final class LockYourInventoryScreen extends Screen {
    private final Screen parent;
    private KeyMapping[] mappings = new KeyMapping[0];
    private int page;
    private int rowsPerPage;

    public LockYourInventoryScreen(Screen parent) {
        super(Component.translatable("lockyourinventory.screen.title"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        this.mappings = Arrays.stream(this.minecraft.options.keyMappings)
            .filter(mapping -> !LockYourInventoryClient.OPEN_MENU_KEY_NAME.equals(mapping.getName()))
            .toArray(KeyMapping[]::new);
        Arrays.sort(this.mappings);

        this.rowsPerPage = Math.max(4, Math.min(8, (this.height - 105) / 24));
        int pageCount = getPageCount();
        if (this.page >= pageCount) {
            this.page = Math.max(0, pageCount - 1);
        }

        int left = this.width / 2 - 165;
        int startY = 52;
        int startIndex = this.page * this.rowsPerPage;
        int endIndex = Math.min(this.mappings.length, startIndex + this.rowsPerPage);

        for (int i = startIndex; i < endIndex; i++) {
            KeyMapping mapping = this.mappings[i];
            int y = startY + (i - startIndex) * 24;

            Button toggleButton = Button.builder(getBindingLabel(mapping), button -> {
                LockedKeyConfig.toggle(mapping);
                this.rebuildWidgets();
            }).bounds(left, y, 330, 20).build();
            this.addRenderableWidget(toggleButton);
        }

        int footerY = this.height - 28;
        Button previous = Button.builder(Component.translatable("lockyourinventory.previous"), button -> {
            this.page--;
            this.rebuildWidgets();
        }).bounds(this.width / 2 - 155, footerY, 90, 20).build();
        previous.active = this.page > 0;
        this.addRenderableWidget(previous);

        Button done = Button.builder(Component.translatable("gui.done"), button -> this.onClose())
            .bounds(this.width / 2 - 50, footerY, 100, 20).build();
        this.addRenderableWidget(done);

        Button next = Button.builder(Component.translatable("lockyourinventory.next"), button -> {
            this.page++;
            this.rebuildWidgets();
        }).bounds(this.width / 2 + 65, footerY, 90, 20).build();
        next.active = this.page + 1 < pageCount;
        this.addRenderableWidget(next);
    }

    private Component getBindingLabel(KeyMapping mapping) {
        String action = Component.translatable(mapping.getName()).getString();
        Component state = LockedKeyConfig.isLocked(mapping)
            ? Component.translatable("lockyourinventory.locked")
            : Component.translatable("lockyourinventory.active");
        return Component.literal(action + "  →  ").append(state);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.centeredText(this.font, this.title, this.width / 2, 16, -1);
        graphics.centeredText(
            this.font,
            Component.translatable("lockyourinventory.screen.hint"),
            this.width / 2,
            32,
            -8355712
        );
        graphics.centeredText(
            this.font,
            Component.translatable("lockyourinventory.page", this.page + 1, getPageCount()),
            this.width / 2,
            this.height - 42,
            -8355712
        );
    }

    @Override
    public void onClose() {
        LockedKeyConfig.save();
        this.minecraft.gui.setScreen(this.parent);
    }

    private int getPageCount() {
        if (this.rowsPerPage <= 0) {
            return 1;
        }
        return Math.max(1, (this.mappings.length + this.rowsPerPage - 1) / this.rowsPerPage);
    }
}
