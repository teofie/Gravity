# Gravity

> Your game, your gravity.

**Current release: v3.0** · Minecraft 1.21.11 · Fabric

Gravity lets you adjust the world's gravity while playing. Configure it manually with an in-game slider or enable automatic changes over time.

## Download

Download `Gravity.jar` from the [v3.0 GitHub release](https://github.com/teofie/Gravity/releases/tag/v3.0). Earlier releases remain available: [v2.0](https://github.com/teofie/Gravity/releases/tag/v2.0) and the original [v1.0.0](https://github.com/teofie/Gravity/releases/tag/v1.0.0).

## Features

- Set gravity from 0% to 100% using the `/gravity` settings screen.
- 50% is vanilla gravity; 0% disables gravity and 100% is twice vanilla gravity.
- Gravity affects living entities and dropped items.
- Fall damage scales with the configured gravity.
- Automatically increase or decrease gravity by one percentage point at a configurable interval. Automatic changes stop at 0% or 100%.
- Save gravity and automatic-mode settings with the world. Settings are restored when the world is loaded again.
- Use the redesigned compact settings screen, which scales to the game window and keeps controls legible when the window is resized.
- See the animated planet logo and the `Your game, your gravity.` tagline in the settings screen.

## What's new in v3.0

- Rebuilt the settings screen with a minimal, futuristic layout while retaining every existing gravity control.
- Rescaled the interface to about half of the game window's diagonal and made it adapt to window resizing.
- Preserved edited control values while Minecraft relayouts the screen after a resize.
- Clarified the vanilla baseline: 50% gravity is vanilla gravity (1.0x).
- Kept the animated planet logo, tagline, and `/gravity check` command.

## Commands

- `/gravity` opens the settings screen. Use the slider for manual gravity, or enable automatic management and set its starting value, interval, and direction.
- `/gravity check` displays the current gravity percentage in chat.

Automatic intervals accept seconds (`30s`), minutes (`1m`), hours (`1h`), or a number of seconds (`30`). The minimum interval is one second.

## Installation

1. Install Fabric Loader for Minecraft 1.21.11 and add Fabric API to the same game instance.
2. Download `Gravity.jar` from the [v3.0 release](https://github.com/teofie/Gravity/releases/tag/v3.0) and place it in the instance's `mods` folder.
3. Launch Minecraft with the Fabric profile.
4. Run `/gravity` in a world to configure gravity.

## Requirements

- Minecraft 1.21.11
- Fabric Loader 0.18.1 or newer
- Fabric API
- Java 21 or newer

## Build from source

This project uses Gradle and Fabric Loom. With Java 21 installed, run:

```shell
./gradlew build
```

On Windows, run `gradlew.bat build`. The remapped mod JAR is written to `build/libs/Gravity.jar`.

## Author

teofie
