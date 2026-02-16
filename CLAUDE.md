# CLAUDE.md - Perks Plugin Development Guide

## Project Overview
**Perks** is a Minecraft Paper plugin (v2.5.1) providing donation-tier perks for the MCME (Minecraft Middle Earth) server. It targets **Paper 1.21.4** on **Java 21**.

## Build & Run
```bash
mvn clean package           # Build the plugin JAR
mvn compile                 # Compile only
```
Output JAR goes to `target/Perks-2.5.1.jar`. Deploy to a Paper 1.21.4 server's `plugins/` folder.

**Required dependency:** PluginUtils 1.9.0 must be installed on the server.

## Project Structure
```
src/main/java/com/mcmiddleearth/perks/
  PerksPlugin.java          # Main plugin entry, config loading
  PerkManager.java          # Registry: registers perks, listeners, command handlers
  commands/                 # Command handlers (one per perk + admin commands)
  listeners/                # Bukkit event listeners (one per perk feature)
  perks/                    # Perk classes (extend abstract Perk.java)
  permissions/              # Permission system, donor data, credit tiers
  tabCompleter/             # Tab completion logic
  utils/                    # HTTP utilities
src/main/resources/
  plugin.yml                # Plugin metadata, commands, permissions
  config.yml                # Perk settings (items, durations, toggles)
  creditDefinition.yml      # Donation tier -> perk mappings
  SitPerkConfiguration.yml  # Block-specific sit positioning
```

## Architecture Patterns
- **Perk base class:** All perks extend `Perk.java` which provides enable/disable/check/writeDefaultConfig
- **Handler pattern:** Each command subcommand has a `*Handler.java` extending `PerksCommandHandler`
- **Listener pattern:** Each perk with events has a `*Listener.java` implementing Bukkit `Listener`
- **Registration:** `PerkManager` registers perks, their listeners, and their handlers
- **Permissions:** Enum-based (`Permissions.java`), mapped to donation tiers in `creditDefinition.yml`
- **Help system:** `HelpHandler` reads the command registry, filters by permission, renders clickable FancyMessage lines
- **Tab completion:** `TabComplete` dynamically reads from `PerksCommandExecutor.getCommands()` with permission filtering
- **Name tag tiers:** Config-driven tier system (`perks.name.tiers.*`), MiniMessage for prefix/suffix, Scoreboard Teams for display
- **Config defaults:** `writeDefaultConfig()` in each Perk writes sensible defaults; existing server configs are preserved

## Key Commands
- `/perk <subcommand>` - Main command (permission: `perks.user`)
- `/perk help [perkname]` - In-game help with clickable commands and examples
- `/perk name [tier|off|info]` - Name tag customization with tier-based permissions
- `/perk enable|disable|info|open|close` - Admin commands (permission: `perks.admin`)
- `/videoteam` - Toggle nametags (permission: `videoteam.user`)

## Code Conventions
- Java 21 source level (switch expressions, pattern matching, `instanceof` patterns)
- No test framework configured
- Uses Paper API (not Spigot) with Adventure Component API for rich text
- MiniMessage for formatted text in config (prefixes, suffixes)
- Logging via plugin-specific `MessageUtil` (avoid `Logger.getGlobal()`)
- Configuration via Bukkit YAML API (`getConfig()`)
- FancyMessage from PluginUtils for clickable/hoverable chat UI

## Notes
- No test framework is configured; verify changes by building and testing on a server
- Maven is required to build (`mvn clean package`)
