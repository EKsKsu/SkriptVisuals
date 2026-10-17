# SkriptVisuals

**A visual effects and hologram addon for Skript.**

SkriptVisuals adds particle effects and customizable text holograms to Minecraft servers, allowing server owners to create visual effects using simple Skript syntax instead of writing Java code.

## Features

- Spawn particles at locations.
- Create particle circles and lines.
- Display temporary holograms.
- Create holograms with multiple rows.
- Add, insert, edit, and remove hologram rows.
- Apply animated row effects.
- Move holograms between locations.
- Attach holograms above players.
- Configure spacing, shadows, see-through text, and billboard modes.

## Requirements

- A compatible Minecraft Paper server.
- [Skript](https://github.com/SkriptLang/Skript).
- SkriptVisuals plugin JAR.

Use a Minecraft version supported by your specific SkriptVisuals build.

## Installation

1. Download the SkriptVisuals `.jar` file.
2. Place it in your server's `plugins` folder.
3. Ensure Skript is also installed in the `plugins` folder.
4. Start or restart the server.
5. Create a file named `visuals.sk` inside `plugins/Skript/scripts/`.
6. Add your SkriptVisuals code to the file.
7. Run `/sk reload visuals`.

If the plugin fails to load, check the server console for dependency, version, or syntax errors.

## Supported Skript Syntax

### 1. Particle Effects

**Spawn particles at a location**

```vb
spawn 20 of "FLAME" particles at location of player
```

Syntax:

```vb
spawn %number% [of] %string% particles at %location%
```

**Create a particle circle**

```vb
spawn a "HAPPY_VILLAGER" particle circle at location of player with radius 3 and 30 points
```

Syntax:

```vb
spawn [a] %string% particle circle at %location% with radius %number% and %number% points
```

**Draw a particle line**

```vb
draw a "FLAME" particle line from location of player to location 5 blocks above player with 30 points
```

Syntax:

```vb
draw [a] %string% particle line from %location% to %location% with %number% points
```

Particle names must be valid for the Minecraft server version you are running.

### 2. Temporary Holograms

Display a temporary hologram at a location.

```vb
show hologram "&aWelcome to the server!" at location of player for 5 seconds
```

Syntax:

```vb
show hologram %string% at %location% for %number% seconds
```

The text supports legacy Minecraft color codes, such as `&a`, `&c`, and `&l`, where supported by the implementation.

### 3. Create and Manage Holograms

Every managed hologram has a unique ID. Use that ID when modifying or deleting the hologram.

**Create a hologram**

```vb
create a hologram with id "welcome" at location of player
```

**Set a row**

```vb
set hologram row 1 of "welcome" to "&aWelcome!"
```

**Add a row**

```vb
add hologram row "&7Enjoy your stay." to "welcome"
```

**Insert a row**

```vb
insert hologram row "&eServer Information" at 2 of "welcome"
```

**Remove a row**

```vb
remove hologram row 2 of "welcome"
```

**Clear all rows**

```vb
clear hologram rows of "welcome"
```

**Move a hologram**

```vb
move hologram "welcome" to location of player
```

**Delete a hologram**

```vb
delete hologram "welcome"
```

### 4. Player Attachments

Attach a hologram above a player so it follows their position.

**Attach**

```vb
attach hologram "welcome" above player
```

**Detach**

```vb
detach hologram "welcome"
```

Detaching stops the hologram from following the player.

### 5. Hologram Row Effects

Apply an animation or visual effect to a row.

**Rainbow**

```vb
set hologram row 1 of "welcome" effect to "RAINBOW"
```

**Pulse**

```vb
set hologram row 1 of "welcome" effect to "PULSE"
```

**Wave**

```vb
set hologram row 1 of "welcome" effect to "WAVE"
```

**Fade**

```vb
set hologram row 1 of "welcome" effect to "FADE"
```

**Disable the effect**

```vb
set hologram row 1 of "welcome" effect to "NONE"
```

Supported effect names:

- `NONE`
- `RAINBOW`
- `PULSE`
- `WAVE`
- `FADE`

### 6. Hologram Appearance

**Set row spacing**

```vb
set hologram "welcome" spacing to 0.3
```

**Enable or disable see-through text**

```vb
set hologram "welcome" see-through to true
```

**Enable or disable text shadows**

```vb
set hologram "welcome" shadow to false
```

**Set the billboard mode**

```vb
set hologram "welcome" billboard to "CENTER"
```

Supported billboard values are the Bukkit/Paper `Display.Billboard` names:

- `FIXED`
- `VERTICAL`
- `HORIZONTAL`
- `CENTER`

### 7. Complete Example

Create a file named `visuals.sk` inside `plugins/Skript/scripts/`.

```vb
command /visualcreate:
    permission: skriptvisuals.admin
    trigger:
        create a hologram with id "welcome" at location of player
        set hologram row 1 of "welcome" to "&a&lWelcome!"
        set hologram row 2 of "welcome" to "&7Welcome to the server."
        set hologram row 3 of "welcome" to "&eEnjoy your stay!"
        set hologram row 1 of "welcome" effect to "RAINBOW"
        set hologram "welcome" spacing to 0.3
        set hologram "welcome" see-through to true
        set hologram "welcome" shadow to false
        send "&aHologram created!" to player

command /visualdelete:
    permission: skriptvisuals.admin
    trigger:
        delete hologram "welcome"
        send "&cHologram deleted!" to player

command /visualtest:
    permission: skriptvisuals.test
    trigger:
        spawn 20 of "FLAME" particles at location of player
        spawn a "HAPPY_VILLAGER" particle circle at location of player with radius 3 and 30 points
        draw a "FLAME" particle line from location of player to location 5 blocks above player with 30 points
        show hologram "&aSkriptVisuals works!" at location of player for 5 seconds
```

Reload the script:

```text
/sk reload visuals
```

Then use `/visualcreate`, `/visualdelete`, or `/visualtest` in-game.

## Permissions

| Permission | Purpose |
|---|---|
| `skriptvisuals.admin` | Permission used by the example hologram administration commands. |
| `skriptvisuals.test` | Permission used by the example visual testing command. |

These permissions are used in the example Skript commands. They are not a complete permission system for the addon itself.

## Important Notes

- Hologram IDs should be unique to avoid accidentally replacing or modifying the wrong hologram.
- Managed holograms are stored in memory by the current implementation. Do not assume they persist after a server restart.
- The temporary hologram effect is separate from the managed hologram system.
- Use valid particle names and billboard values for your server version.
- SkriptVisuals only supports the syntax registered by the installed plugin version. Features not listed here may require a later release.

## Support

For bug reports, compatibility issues, and feature suggestions, open an issue in the SkriptVisuals GitHub repository.

**SkriptVisuals — Simple syntax. Better visuals.**
