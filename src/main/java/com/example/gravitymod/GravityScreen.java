package com.example.gravitymod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class GravityScreen extends Screen {
    private static final int PANEL_WIDTH = 640;
    private static final int PANEL_HEIGHT = 430;

    private double uiScale;
    private static final int PANEL = 0xB91B2028;
    private static final int TEXT = 0xFFFFFFFF;
    private static final int MUTED_TEXT = 0xFFC9CDD4;

    private GravitySlider gravitySlider;
    private ButtonWidget automaticToggle;
    private ButtonWidget directionButton;
    private TextFieldWidget intervalField;
    private TextFieldWidget initialGravityField;
    private final int initialGravity;
    private boolean automatic;
    private boolean increasing;

    public GravityScreen(int initialGravity,
                         boolean automatic,
                         int intervalSeconds,
                         boolean increasing) {
        super(Text.literal("Gravity"));
        this.initialGravity = Math.max(0, Math.min(100, initialGravity));
        this.automatic = automatic;
        this.increasing = increasing;
        this.initialIntervalSeconds = Math.max(1, intervalSeconds);
    }

    private final int initialIntervalSeconds;

    @Override
    protected void init() {
        uiScale = Math.min(2.0D / 3.0D, Math.min(width / (double) PANEL_WIDTH, height / (double) PANEL_HEIGHT));
        int panelWidth = scaled(PANEL_WIDTH);
        int panelHeight = scaled(PANEL_HEIGHT);
        int left = (width - panelWidth) / 2;
        int top = (height - panelHeight) / 2;
        int controlHeight = Math.max(20, scaled(24));
        int labelX = left + scaled(32);
        int controlX = left + scaled(190);

        gravitySlider = addDrawableChild(new GravitySlider(
            left + scaled(190),
            top + scaled(94),
            scaled(360),
            controlHeight,
                initialGravity / 100.0D
        ));

        automaticToggle = addDrawableChild(ButtonWidget.builder(
                Text.literal("[ ]"),
                        button -> toggleAutomatic()
                ).dimensions(labelX, top + scaled(150), scaled(28), controlHeight).build());

        initialGravityField = addDrawableChild(new TextFieldWidget(
            textRenderer,
            controlX,
            top + scaled(206),
            scaled(100),
            controlHeight,
            Text.literal("Valore iniziale")
        ));
        initialGravityField.setText(initialGravity + "%");
        initialGravityField.setMaxLength(3);
        initialGravityField.active = automatic;

        intervalField = addDrawableChild(new TextFieldWidget(
                textRenderer,
                controlX,
            top + scaled(286),
            scaled(150),
            controlHeight,
                Text.literal("Intervallo")
        ));
        intervalField.setText(formatInterval(initialIntervalSeconds));
        intervalField.setMaxLength(8);
        intervalField.setPlaceholder(Text.literal("30s / 1m / 1h"));

        directionButton = addDrawableChild(ButtonWidget.builder(
                        Text.literal("Decrease"),
                        button -> {
                            increasing = !increasing;
                            updateDirectionMessage();
                            sendSettings();
                        }
                ).dimensions(left + scaled(350), top + scaled(286), scaled(190), controlHeight).build());
        automaticToggle.setMessage(Text.literal(
                automatic ? "[x]" : "[ ]"
        ));
        gravitySlider.active = !automatic;
        updateDirectionMessage();
    }

    private String formatInterval(int seconds) {
        if (seconds % 60 == 0) {
            return (seconds / 60) + "m";
        }
        return seconds + "s";
    }

    private int scaled(int value) {
        return Math.max(1, (int) Math.round(value * uiScale));
    }

    private void toggleAutomatic() {
        automatic = !automatic;
        gravitySlider.active = !automatic;
        initialGravityField.active = automatic;
        automaticToggle.setMessage(Text.literal(
            automatic ? "[x]" : "[ ]"
        ));
        sendSettings();
    }

    private void updateDirectionMessage() {
        directionButton.setMessage(Text.literal(increasing ? "Increase" : "Decrease"));
    }

    private void sendSettings() {
        GravityClient.sendSettings(
                automatic ? parseInitialGravity() : gravitySlider.getGravityValue(),
                automatic,
                parseIntervalSeconds(),
                increasing
        );
    }

    private int parseInitialGravity() {
        try {
            String value = initialGravityField.getText().trim().replace("%", "");
            return Math.max(0, Math.min(100, Integer.parseInt(value)));
        } catch (NumberFormatException exception) {
            return initialGravity;
        }
    }

    private int parseIntervalSeconds() {
        String raw = intervalField.getText().trim().toLowerCase();
        try {
            if (raw.endsWith("m")) {
                return Math.max(1, Integer.parseInt(raw.substring(0, raw.length() - 1)) * 60);
            }
            if (raw.endsWith("h")) {
                return Math.max(1, Integer.parseInt(raw.substring(0, raw.length() - 1)) * 3600);
            }
            if (raw.endsWith("s")) {
                return Math.max(1, Integer.parseInt(raw.substring(0, raw.length() - 1)));
            }
            return Math.max(1, Integer.parseInt(raw));
        } catch (NumberFormatException exception) {
            return 60;
        }
    }

    @Override
    public void close() {
        sendSettings();
        super.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int panelWidth = scaled(PANEL_WIDTH);
        int panelHeight = scaled(PANEL_HEIGHT);
        int left = (width - panelWidth) / 2;
        int top = (height - panelHeight) / 2;
        int right = left + panelWidth;
        int bottom = top + panelHeight;

        context.fill(0, 0, width, height, 0xD10A0D12);
        int glow = scaled(5);
        context.fill(left - glow, top - glow, right + glow, bottom + glow, 0x301B2633);
        drawGlassRect(context, left, top, right, bottom, PANEL);

        context.drawCenteredTextWithShadow(
                textRenderer,
                Text.literal("GRAVITY"),
                width / 2,
                top + scaled(58),
                TEXT
        );
        int labelX = left + scaled(32);
        int controlX = left + scaled(190);
        context.drawTextWithShadow(textRenderer, Text.literal("gravity"), labelX, top + scaled(100), TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("Automated management"), labelX + scaled(42), top + scaled(156), TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("Initial gravity"), labelX, top + scaled(212), TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("Time"), labelX, top + scaled(294), TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("0"), controlX - scaled(6), top + scaled(118), MUTED_TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("100"), controlX + scaled(362), top + scaled(118), MUTED_TEXT);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawGlassRect(DrawContext context, int left, int top, int right, int bottom, int color) {
        int edge = Math.max(1, scaled(1));
        context.fill(left, top, right, bottom, color);
        context.fill(left + edge, top + edge, right - edge, top + edge * 2, 0x35FFFFFF);
        context.fill(left + edge, top + edge, left + edge * 2, bottom - edge, 0x20FFFFFF);
        context.fill(left + edge, bottom - edge * 2, right - edge, bottom - edge, 0x38000000);
    }

    private final class GravitySlider extends SliderWidget {
        private GravitySlider(int x, int y, int width, int height, double value) {
            super(x, y, width, height, Text.literal("Gravita"), value);
            updateMessage();
        }

        private int getGravityValue() {
            return (int) Math.round(value * 100.0D);
        }

        @Override
        protected void updateMessage() {
            setMessage(Text.literal("" + getGravityValue() + "%"));
        }

        @Override
        protected void applyValue() {
            if (!automatic) {
                GravityClient.sendSettings(getGravityValue(), false, parseIntervalSeconds(), increasing);
            }
        }
    }
}
