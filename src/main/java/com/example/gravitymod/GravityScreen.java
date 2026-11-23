package com.example.gravitymod;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.SliderWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public final class GravityScreen extends Screen {
    private static final int PANEL_WIDTH = 600;
    private static final int PANEL_HEIGHT = 413;
    private static final double TARGET_DIAGONAL_RATIO = 0.5D;

    private double uiScale;
    private static final int PANEL = 0xF0111D24;
    private static final int PANEL_INNER = 0xFF15272C;
    private static final int TEXT = 0xFFF0F5F2;
    private static final int MUTED_TEXT = 0xFF9BB0AE;
    private static final int EMERALD = 0xFF52E3A4;
    private static final int BORDER = 0xFF29413F;

    private GravitySlider gravitySlider;
    private ButtonWidget automaticToggle;
    private ButtonWidget directionButton;
    private ButtonWidget decreaseButton;
    private TextFieldWidget intervalField;
    private TextFieldWidget initialGravityField;
    private ButtonWidget doneButton;
    private final int initialGravity;
    private boolean automatic;
    private boolean increasing;
    private int animationTicks;

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
        // Screen.init runs again when Minecraft's GUI dimensions change; retain edits across that relayout.
        int sliderValue = gravitySlider == null ? initialGravity : gravitySlider.getGravityValue();
        String initialValue = initialGravityField == null
                ? initialGravity + "%"
                : initialGravityField.getText();
        String intervalValue = intervalField == null
                ? formatInterval(initialIntervalSeconds)
                : intervalField.getText();

        double panelDiagonal = Math.hypot(PANEL_WIDTH, PANEL_HEIGHT);
        double screenDiagonal = Math.hypot(width, height);
        double scaleToTargetDiagonal = TARGET_DIAGONAL_RATIO * screenDiagonal / panelDiagonal;
        double scaleToFitWindow = Math.min(width / (double) PANEL_WIDTH, height / (double) PANEL_HEIGHT);
        uiScale = Math.min(1.0D, Math.min(scaleToTargetDiagonal, scaleToFitWindow));
        int panelWidth = scaled(PANEL_WIDTH);
        int panelHeight = scaled(PANEL_HEIGHT);
        int left = (width - panelWidth) / 2;
        int top = (height - panelHeight) / 2;
        int controlHeight = scaled(24);

        gravitySlider = addDrawableChild(new GravitySlider(
            left + scaled(61),
            top + scaled(178),
            scaled(242),
            scaled(18),
                sliderValue / 100.0D
        ));

        automaticToggle = addDrawableChild(ButtonWidget.builder(
                Text.literal("OFF"),
                        button -> toggleAutomatic()
                ).dimensions(left + scaled(506), top + scaled(169), scaled(54), controlHeight).build());

        initialGravityField = addDrawableChild(new TextFieldWidget(
            textRenderer,
            left + scaled(24),
            top + scaled(260),
            scaled(142),
            controlHeight,
            Text.literal("Initial gravity")
        ));
        initialGravityField.setText(initialValue);
        initialGravityField.setMaxLength(4);
        initialGravityField.active = automatic;

        intervalField = addDrawableChild(new TextFieldWidget(
                textRenderer,
                left + scaled(180),
            top + scaled(260),
            scaled(142),
            controlHeight,
                Text.literal("Interval")
        ));
        intervalField.setText(intervalValue);
        intervalField.setMaxLength(8);
        intervalField.setPlaceholder(Text.literal("30s / 1m / 1h"));

        directionButton = addDrawableChild(ButtonWidget.builder(
                        Text.literal("+ INCREASE"),
                        button -> {
                            increasing = true;
                            updateDirectionMessage();
                            sendSettings();
                        }
                ).dimensions(left + scaled(336), top + scaled(260), scaled(116), controlHeight).build());
        decreaseButton = addDrawableChild(ButtonWidget.builder(
                Text.literal("DECREASE"),
                button -> {
                    increasing = false;
                    updateDirectionMessage();
                    sendSettings();
                }
        ).dimensions(left + scaled(460), top + scaled(260), scaled(116), controlHeight).build());
        doneButton = addDrawableChild(ButtonWidget.builder(
                Text.literal("DONE"), button -> close()
        ).dimensions(left + scaled(468), top + scaled(324), scaled(108), scaled(36)).build());
        automaticToggle.setMessage(Text.literal(
                automatic ? "ON" : "OFF"
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
        automaticToggle.setMessage(Text.literal(automatic ? "ON" : "OFF"));
        sendSettings();
    }

    private void updateDirectionMessage() {
        directionButton.setMessage(Text.literal(increasing ? "+ INCREASE" : "INCREASE"));
        decreaseButton.setMessage(Text.literal(increasing ? "DECREASE" : "- DECREASE"));
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
    public void tick() {
        super.tick();
        animationTicks++;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        int panelWidth = scaled(PANEL_WIDTH);
        int panelHeight = scaled(PANEL_HEIGHT);
        int left = (width - panelWidth) / 2;
        int top = (height - panelHeight) / 2;
        int right = left + panelWidth;
        int bottom = top + panelHeight;

        context.fill(0, 0, width, height, 0xE6091115);
        // A few restrained signal marks give the dark game view a futuristic frame.
        context.fill(scaled(86), height / 2 - scaled(4), scaled(110), height / 2 - scaled(2), 0xFF16342F);
        context.fill(width - scaled(110), height / 3, width - scaled(88), height / 3 + scaled(2), 0xFF16342F);
        context.fill(width - scaled(178), height / 6, width - scaled(174), height / 6 + scaled(4), EMERALD);
        int glow = scaled(4);
        context.fill(left - glow, top - glow, right + glow, bottom + glow, 0x4038A987);
        drawPixelPanel(context, left, top, right, bottom);

        // Compact two-column control area with clear separation between values and actions.
        context.fill(left + scaled(24), top + scaled(74), left + scaled(352), top + scaled(210), PANEL_INNER);
        context.fill(left + scaled(368), top + scaled(74), right - scaled(24), top + scaled(210), PANEL_INNER);
        context.fill(left + scaled(24), top + scaled(74), left + scaled(26), top + scaled(210), EMERALD);
        context.fill(left + scaled(368), top + scaled(74), left + scaled(370), top + scaled(210), 0xFFF5C66B);
        context.fill(left + scaled(24), top + scaled(228), right - scaled(24), top + scaled(229), BORDER);
        context.fill(left + scaled(24), top + scaled(312), right - scaled(24), top + scaled(313), BORDER);

        drawPlanetLogo(context, left + scaled(28), top + scaled(18), scaled(46));
        context.drawTextWithShadow(textRenderer, Text.literal("GRAVITY"), left + scaled(94), top + scaled(20), TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("YOUR GAME, YOUR GRAVITY."), left + scaled(95), top + scaled(41), MUTED_TEXT);
        context.fill(right - scaled(160), top + scaled(24), right - scaled(24), top + scaled(51), 0xFF142A2A);
        context.fill(right - scaled(148), top + scaled(34), right - scaled(142), top + scaled(40), EMERALD);
        context.drawTextWithShadow(textRenderer, Text.literal("WORLD LINKED"), right - scaled(132), top + scaled(33), TEXT);

        context.drawTextWithShadow(textRenderer, Text.literal("GRAVITY FIELD"), left + scaled(44), top + scaled(90), MUTED_TEXT);
        drawGravityReadout(context, left + scaled(42), top + scaled(111), gravitySlider.getGravityValue());
        context.drawTextWithShadow(textRenderer, Text.literal("VANILLA BASELINE"), left + scaled(175), top + scaled(119), MUTED_TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("1.0x"), left + scaled(175), top + scaled(143), EMERALD);
        context.drawTextWithShadow(textRenderer, Text.literal("0"), left + scaled(44), top + scaled(184), MUTED_TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("100"), left + scaled(320), top + scaled(184), MUTED_TEXT);

        context.drawTextWithShadow(textRenderer, Text.literal("AUTO SHIFT"), left + scaled(388), top + scaled(90), MUTED_TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("Let gravity change"), left + scaled(388), top + scaled(111), TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("over time."), left + scaled(388), top + scaled(129), TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal(automatic ? "ON" : "OFF"), left + scaled(388), top + scaled(177), MUTED_TEXT);

        context.drawTextWithShadow(textRenderer, Text.literal("START VALUE"), left + scaled(24), top + scaled(242), MUTED_TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("INTERVAL"), left + scaled(180), top + scaled(242), MUTED_TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("DIRECTION"), left + scaled(336), top + scaled(242), MUTED_TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("/gravity check"), left + scaled(24), top + scaled(327), EMERALD);
        context.drawTextWithShadow(textRenderer, Text.literal("VIEW LIVE GRAVITY"), left + scaled(24), top + scaled(346), MUTED_TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("ESC TO CLOSE"), left + scaled(24), top + scaled(381), MUTED_TEXT);
        context.drawTextWithShadow(textRenderer, Text.literal("GRAVITY  /  V3.0"), right - scaled(118), top + scaled(381), MUTED_TEXT);

        super.render(context, mouseX, mouseY, delta);
    }

    private void drawGravityReadout(DrawContext context, int x, int y, int value) {
        var matrices = context.getMatrices();
        String digits = String.valueOf(value);
        matrices.pushMatrix();
        matrices.translate((float) x, (float) y);
        matrices.scale(3.5F, 3.5F);
        context.drawTextWithShadow(textRenderer, Text.literal(digits), 0, 0, TEXT);
        matrices.popMatrix();
        int percentX = x + (int) Math.ceil(textRenderer.getWidth(digits) * 3.5D) + scaled(5);
        context.drawTextWithShadow(textRenderer, Text.literal("%"), percentX, y + scaled(13), EMERALD);
    }

    private void drawPixelPanel(DrawContext context, int left, int top, int right, int bottom) {
        int edge = Math.max(1, scaled(2));
        context.fill(left, top, right, bottom, PANEL);
        context.fill(left, top, right, top + scaled(4), EMERALD);
        context.fill(left + edge, top + edge, right - edge, bottom - edge, 0xFF111D24);
        context.fill(left + scaled(8), top + scaled(61), right - scaled(8), top + scaled(62), BORDER);
        context.fill(left + scaled(8), bottom - scaled(8), right - scaled(8), bottom - scaled(7), BORDER);
    }

    private void drawPlanetLogo(DrawContext context, int x, int y, int size) {
        context.fill(x, y, x + size, y + size, 0xFF17332F);
        x += Math.max(1, size / 7);
        y += Math.max(1, size / 7);
        size = Math.max(12, size * 5 / 7);
        int unit = Math.max(1, size / 12);
        int centerX = x + size / 2;
        int centerY = y + size / 2;
        // Pixel-built globe, intentionally restrained so the controls remain the focus.
        context.fill(x + unit * 3, y + unit * 2, x + unit * 9, y + unit * 10, 0xFF167065);
        context.fill(x + unit * 2, y + unit * 3, x + unit * 10, y + unit * 9, 0xFF1E8D7B);
        context.fill(x + unit * 4, y + unit, x + unit * 8, y + unit * 11, 0xFF1E8D7B);
        context.fill(x + unit * 4, y + unit * 2, x + unit * 7, y + unit * 4, 0xFF76D98A);
        context.fill(x + unit * 3, y + unit * 5, x + unit * 6, y + unit * 7, 0xFF63C77A);
        context.fill(x + unit * 7, y + unit * 7, x + unit * 9, y + unit * 9, 0xFF63C77A);
        context.fill(x + unit * 5, y + unit, x + unit * 7, y + unit * 2, 0xFFC5F3CE);
        context.fill(x + unit, y + unit * 6, x + unit * 11, y + unit * 7, 0xFF62E8B4);

        double phase = (animationTicks + 0.0D) * 0.035D;
        int moonX = centerX + (int) Math.round(Math.cos(phase) * size * 0.46);
        int moonY = centerY + (int) Math.round(Math.sin(phase) * size * 0.22);
        int moon = Math.max(2, unit * 2);
        context.fill(moonX - moon / 2, moonY - moon / 2, moonX + moon / 2, moonY + moon / 2, 0xFFF5C66B);
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
            setMessage(Text.empty());
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
            int centerY = getY() + getHeight() / 2;
            int trackLeft = getX();
            int trackRight = getX() + getWidth();
            int knobX = trackLeft + (int) Math.round(value * Math.max(0, getWidth() - 8));
            context.fill(trackLeft, centerY - 2, trackRight, centerY + 2, 0xFF2A4241);
            context.fill(trackLeft, centerY - 2, knobX + 4, centerY + 2, EMERALD);
            context.fill(knobX, getY() + 2, knobX + 8, getY() + getHeight() - 2, 0xFFE8FFF3);
        }

        @Override
        protected void applyValue() {
            if (!automatic) {
                GravityClient.sendSettings(getGravityValue(), false, parseIntervalSeconds(), increasing);
            }
        }
    }
}
