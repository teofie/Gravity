# Gravity

> Your game, your gravity.

**Current release: v2.0** · Minecraft 1.21.11 · Fabric

Gravity lets you adjust the world's gravity while playing. Configure it manually with an in-game slider or enable automatic changes over time.

## Download

Download the latest `Gravity-2.0.jar` from the [v2.0 GitHub release](https://github.com/teofie/Gravity-Mod/releases/tag/v2.0). The original v1.0 release is preserved separately as [v1.0.0](https://github.com/teofie/Gravity-Mod/releases/tag/v1.0.0).

## Features

- Set gravity from 0% to 100% using the `/gravity` settings screen.
- 50% is vanilla gravity; 0% disables gravity and 100% is twice vanilla gravity.
- Gravity affects living entities and dropped items.
- Fall damage scales with the configured gravity.
- Automatically increase or decrease gravity by one percentage point at a configurable interval. Automatic changes stop at 0% or 100%.
- Save gravity and automatic-mode settings with the world. Settings are restored when the world is loaded again.

## Commands

- `/gravity` opens the settings screen. Use the slider for manual gravity, or enable automatic management and set its starting value, interval, and direction.
- `/gravity check` displays the current gravity percentage in chat.

Automatic intervals accept seconds (`30s`), minutes (`1m`), hours (`1h`), or a number of seconds (`30`). The minimum interval is one second.

## Installation

1. Install Fabric Loader for Minecraft 1.21.11 and add Fabric API to the same game instance.
2. Download `Gravity-2.0.jar` from the [v2.0 release](https://github.com/teofie/Gravity-Mod/releases/tag/v2.0) and place it in the instance's `mods` folder.
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
