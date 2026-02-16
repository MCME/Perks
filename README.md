# MCME Perks

A comprehensive donation/permission-based perks plugin for [Minecraft Middle Earth](https://www.mcmiddleearth.com/) servers running **Paper 1.21.4+**.

**Version:** 2.5.1
**Authors:** Dags, DonoA, Eriol_Eandur, meggawatts, Jubo
**License:** GNU General Public License v3
**Java:** 21

## Features

### Mounts & Vehicles
| Perk | Command | Description |
|------|---------|-------------|
| **Horse** | `/perk horse [color] [pattern]` | Spawn a rideable horse with customizable coat colors (white, black, brown, chestnut, creamy, dark_brown, gray) and patterns (none, black_dots, white, white_dots, whitefield) |
| **Gallop** | `/perk gallop` / `/perk whoa` | Make your horse run at 2x speed or slow it back down |
| **Boat** | `/perk boat [woodType]` | Spawn a boat (oak, acacia, birch, dark_oak, jungle, spruce, mangrove, cherry, bamboo, pale_oak) |

### Companions
| Perk | Command | Description |
|------|---------|-------------|
| **Pet** | `/perk pet cat\|dog [name] [collar color] [type]` | Spawn tame cats or dogs that follow you (max 3). Cats have 11 breed types, dogs have 9 wolf variants (pale, spotted, snowy, black, ashen, rusty, woods, chestnut, striped). Supports 16 collar colors |
| **Parrot** | `/perk parrot [color] [left\|right] [remove]` | Place a parrot on your shoulder in blue, cyan, gray, green, or red |
| **Jockey** | Hold a LEAD near a player | Ride other players (blocked by `perks.antijockey` permission) |

### Magic & Effects
| Perk | Command | Description |
|------|---------|-------------|
| **Wizard Light** | `/perk light [intensity]` | Summon a floating light orb that follows you. Control distance with left/right click while holding a stick. Renders 3D geometric shapes (Icosahedron, Dodecahedron) with particles |
| **Fire** | `/perk fire [duration]` | Set yourself on fire with fire resistance (default 10s, max 100s) |
| **Ring of Power** | `/perk ring` | Grants invisibility, nausea, and blindness with Tolkien-inspired sound effects |
| **Speed Boots** | `/perk speed` | Receive Diamond Boots that grant Speed II when right-clicked (configurable amplifier) |
| **Jump Leggings** | `/perk jump` | Receive Diamond Leggings that grant Jump Boost III when right-clicked (configurable amplifier) |

### Utility
| Perk | Command | Description |
|------|---------|-------------|
| **Sit** | Right-click a block with a Ghast Tear | Sit on any block with smart positioning for stairs, slabs, and custom blocks |
| **Compass** | `/perk compass [direction\|degrees] [x z]` | Set your compass to point north/south/east/west, a specific heading in degrees, or exact coordinates |
| **Name Tag** | `/perk name [tier\|off\|info]` | Customizable name tag with color tiers, prefix/suffix symbols, and animated rainbow effect. Default: yellow. Per-tier permissions (`perks.name.<tier>`) |

### Items
| Perk | Command | Description |
|------|---------|-------------|
| **Firework** | `/perk firework` | Receive 64 named firework rockets |
| **Elytra** | `/perk elytra` | Receive a named Elytra |

### Help
| Perk | Command | Description |
|------|---------|-------------|
| **Help** | `/perk help [perkname]` | In-game help system with clickable commands. Lists available perks or shows detailed usage for a specific perk |

## Admin Commands

All admin commands require `perks.admin` permission.

| Command | Description |
|---------|-------------|
| `/perk enable [perkName]` | Enable a specific perk or all perks |
| `/perk disable [perkName]` | Disable a specific perk or all perks |
| `/perk info` | Show enable/disable status of all perks |
| `/perk open <perkName> [minutes]` | Make a perk free for all players (default: 10 minutes) |
| `/perk close <perkName>` | Remove free access to a perk |

## Additional Commands

| Command | Permission | Description |
|---------|-----------|-------------|
| `/videoteam` | `videoteam.user` | Toggle nametag visibility for video recording |

## Permissions

| Permission | Description | Default |
|-----------|-------------|---------|
| `perks.user` | Access to use perks | op |
| `perks.admin` | Admin commands (inherits `perks.user`) | op |
| `perks.antijockey` | Prevents other players from jockeying you | op |
| `videoteam.user` | Access to `/videoteam` | op |

Individual perk permissions are auto-generated as `perks.<perkname>` (e.g., `perks.horse`, `perks.fire`, `perks.sit`).

### Name Tag Tier Permissions
| Permission | Description |
|-----------|-------------|
| `perks.name.gold` | Gold tier name tag with star prefix |
| `perks.name.diamond` | Diamond tier name tag with diamond prefix/suffix |
| `perks.name.rainbow` | Animated rainbow color cycling name tag |

## Donation Tier System

Perks are unlocked based on donation credits defined in `creditDefinition.yml`:

| Credits | Perk Unlocked |
|---------|--------------|
| 5 | Name Tag |
| 10 | Sit |
| 20 | Ring of Power |
| 30 | Jump Leggings |
| 40 | Fire |
| 50 | Horse |
| 60 | Wizard Light |
| 70 | Jockey |
| 80 | Elytra |
| 100 | Firework |

Donor data is synchronized from an external URL configured in `config.yml`.

## Configuration

### config.yml
Each perk can be individually configured under the `perks` section:
- **Enabled/disabled** toggle per perk
- **Custom items** (material type, display name, quantity)
- **Duration/intensity limits** (fire ticks, light intensity/duration)
- **Companion limits** (max number of pets)

### SitPerkConfiguration.yml
Block-specific X/Y/Z/Yaw offsets for custom sit positioning on special blocks.

### creditDefinition.yml
Maps donation credit thresholds to perk names.

## Requirements

- **Server:** Paper 1.21.4+
- **Java:** 21+
- **Dependencies:** [PluginUtils](https://github.com/MCME/PluginUtils) 1.9.0

## Building

```bash
mvn clean package
```

Deploy the resulting JAR from `target/` to your server's `plugins/` directory.
