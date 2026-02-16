# PR Update: Code Audit, Bug Fixes & Feature Enhancements

This document details every change made in this PR, the reasoning behind each decision, and the technical approach taken. It covers three phases: a 25-issue code audit, six feature enhancements, and documentation updates.

---

## Phase 1: Code Audit (25 issues fixed across 19 files)

Before adding features, the existing codebase was audited for correctness, safety, and maintainability. Every fix below targets a real defect or a pattern that would cause problems as the plugin evolves.

### Critical Fixes

#### 1. CompanionPerk.java — IndexOutOfBoundsException guard

**Problem:** `getMetadata().get(0)` was called without checking if the metadata list was empty. If the entity had no metadata for that key, this throws `IndexOutOfBoundsException` at runtime — a server crash for the player.

**Why this matters:** Metadata is set by our plugin but can be absent if the entity was spawned by something else, or if the server reloaded while companions were active. Defensive checks on external data are a basic requirement.

**Fix:** Added `!meta.isEmpty()` guard before accessing `meta.get(0)`.

#### 2. VideoTeamCommand.java — Unsafe cast crash

**Problem:** `(Player) sender` was used without checking if the sender was actually a `Player`. Running `/videoteam` from the server console would crash with a `ClassCastException`.

**Why this pattern is dangerous:** Any command registered in `plugin.yml` can be run from the console. Unchecked casts on `CommandSender` are a category of bug, not a one-off.

**Fix:** Replaced with `instanceof Player player` pattern matching (Java 21 feature). Console now gets a clean error message.

#### 3. TabComplete.java — Full rewrite for safety and maintainability

**Problem (old code):** Hardcoded `PERK_NAMES` string array that had to be manually kept in sync with registered perks. No `instanceof` check on the sender. No null check on `PerkManager.forName()` results.

**Why a rewrite was the right call:** The hardcoded array was already out of date. Patching individual issues would leave the fundamental design problem: a second source of truth for the command list that inevitably drifts.

**Fix (new code):** Tab completion now reads dynamically from `PerksCommandExecutor.getCommands()` — the same registry used for command dispatch. This means:
- Adding a new perk automatically adds tab completion (zero maintenance).
- Permission filtering uses the same `PermissionData.isAllowed()` path as the handler system.
- `instanceof Player` guard returns empty list for console tab-complete calls.
- `/perk help <TAB>` completes to available perk names.

#### 4. PotionEffectPerk.java — Dead scheduled task (resource leak)

**Problem:** An empty `BukkitRunnable` was being `.runTaskTimer()`'d — it ran every tick doing nothing. On a busy server, scheduled tasks consume tick time even when empty.

**Fix:** Removed the dead runnable entirely.

#### 5. BoatPerk.java — Deprecated API + unsafe cast

**Problem:** `getPassenger()` (deprecated since Bukkit 1.11.2) returns a single entity, but boats can have multiple passengers since 1.9. Also, the result was cast to `Player` without checking.

**Why deprecated APIs matter:** Paper marks deprecated methods for removal. They can be dropped in any minor version, turning a warning into a compile failure.

**Fix:** Replaced with `getPassengers()` + `instanceof Player` check on each passenger.

#### 6. BoatHandler.java — Class.forName() reflection replaced with whitelist

**Problem (old code):**
```java
Class.forName("org.bukkit.entity.boat." + args[0])
```
This is a security anti-pattern. The user controls the class name. While limited to the `boat` package, reflection-based dispatch from user input is never acceptable — it could load unintended classes if the package gains new types, and it provides no input validation or error messages.

**Why a Map is better:**
```java
private static final Map<String, Class<? extends Boat>> BOAT_TYPES = Map.of(
    "oak", OakBoat.class,
    "acacia", AcaciaBoat.class,
    // ...
);
```
- Compile-time verified: if Paper removes a boat class, this fails at compile, not at runtime.
- Self-documenting: the map *is* the list of valid types.
- Safe: only whitelisted types can be instantiated.
- Gives clear error messages: "Boat type not found! Using oak boat."

### Important Fixes

#### 7. HorsePerk.java — Same deprecated getPassenger() pattern as BoatPerk
**Fix:** Same approach — `getPassengers()` with `instanceof` check.

#### 8. JockeyPerk.java — Empty method body

**Problem:** `unjockey()` was declared but had an empty body. Players who mounted another player could never dismount through the perk system.

**Fix:** Implemented with `jockey.leaveVehicle()`.

#### 9. PerkManager.java — Case-insensitive comparison + defensive collection

**Problem:** `.toLowerCase().equals()` is fragile — if the locale changes, `toLowerCase()` can produce unexpected results for certain characters. `getPerks()` returned the mutable internal set.

**Fix:** Replaced with `.equalsIgnoreCase()` (locale-safe). `getPerks()` now returns `Collections.unmodifiableSet()` to prevent accidental modification of the registry.

#### 10. SitPerk.java — Atomic map operation to prevent memory leak

**Problem:** `sitUp()` called `armorStands.get(player)` followed by `armorStands.remove(player)`. Between these two calls, another thread could modify the map. More importantly, the old code would leave entries in the map if the ArmorStand was null, slowly leaking memory.

**Fix:** `armorStands.remove(player)` returns the value and removes atomically.

#### 11. ParrotHandler.java — Overly broad catch

**Problem:** `catch(Exception)` was used when only `IllegalArgumentException` was expected from `DyeColor.valueOf()`. This silently swallowed NPEs, ArrayIndexOutOfBounds, and other real bugs.

**Fix:** `catch(IllegalArgumentException)` — catches only what's expected, lets real bugs propagate.

#### 12. HttpTextInputHandler.java — URL prefix assembly bug

**Problem:** `url + "http://"` — the protocol was appended *after* the URL instead of before it. Every HTTP request was malformed.

**Fix:** `"http://" + url`.

#### 13. PerksPlugin.java — Null config section access

**Problem:** `getConfig().getConfigurationSection("perks")` can return `null` if the config section doesn't exist. The result was used without null checks in `arePerksEnabled`, `isPerkEnabled`, `enablePerk`, and `enableAllPerks`. First server start with no config = `NullPointerException`.

**Fix:** Added null guards before every section access.

#### 14. CompassHandler.java — System.out.println in production

**Problem:** `System.out.println` bypasses the plugin's logging framework, has no prefix, and can't be filtered or disabled. In a server with 50+ plugins, untagged stdout is noise.

**Fix:** Replaced with `cs.sendMessage()` to send feedback to the command sender.

#### 15-16. SitPerk.java and ParrotPerk.java — Debug logging in hot paths

**Problem:** `Logger.getGlobal().info()` in `sitDown()`, `sitUp()`, and `checkParrot()` — methods that run on every sit/stand/parrot event. This floods the global log with per-player debug output that no admin wants to see in production.

**Fix:** Removed all debug log statements (8 in SitPerk, 1 in ParrotPerk).

### Code Quality (17-25)

Removed from 8 files:
- Commented-out code blocks (5 in CompanionPerk, respawn block in HorsePerk, boatSpawn in BoatListener, config code in SitPerk)
- Unused imports (`Logger` in 4 files, `ParrotPerk`/`Variant`/`Location`/`World` in CompanionHandler, `ItemStack` in HorseHandler, `Bisected` in SitPerk, plus 5 unused imports in BoatListener)
- Dead variables and trailing whitespace

**Why bother?** Commented-out code signals "this might be needed" and discourages developers from understanding the current design. Unused imports trigger IDE warnings that train developers to ignore warnings. Clean files make real changes visible in diffs.

---

## Phase 2: Feature Enhancements (6 items)

### 1. Boat Types — All 10 wood types supported

**Files:** `BoatHandler.java`

**Before:** 6 boat types with wrong names ("redwood", "generic" don't exist in Minecraft).
**After:** All 10 Paper 1.21.4 boat types: oak, acacia, birch, dark_oak, jungle, spruce, mangrove, cherry, bamboo, pale_oak.

**Technical note:** `Map.of()` supports exactly 10 entries (its maximum), which is exactly the count we need. For >10, you'd need `Map.ofEntries()`. The bamboo entry maps to `BambooRaft.class` (not `BambooBoat`) because Paper models bamboo watercraft as rafts, matching Minecraft's actual behavior.

### 2. Wolf Variants — 1.21.4 Registry API

**Files:** `CompanionPerk.java`, `CompanionHandler.java`

**Before:** All dogs spawned as the default (pale) variant.
**After:** `/perk pet dog Rex red ashen` spawns a dog with a specific wolf variant.

**Why Registry instead of enum:** In Paper 1.21.4, `Wolf.Variant` is a `Keyed` interface, not an enum. Variants are looked up via `Registry.WOLF_VARIANT.get(NamespacedKey.minecraft(name))`. This is the correct modern Paper pattern — registries are data-driven and can be extended by datapacks, while enums cannot. The code also provides `getRandomWolfVariant()` using `Registry.WOLF_VARIANT.forEach()` for when no variant is specified.

**Why the signature changed:** `spawnDog()` now takes a `Wolf.Variant` parameter. This keeps the spawn logic in the perk class (where it belongs) while letting the handler control which variant to use. The handler parses user input and falls back to a random variant.

### 3. Wizard Light Config Bug

**File:** `WizardLightPerk.java`

**Before:**
```java
config.set("maxSpeed", speedFactor);  // WRONG: writing speedFactor as maxSpeed
config.set("speedFactor", maxSpeed);  // WRONG: writing maxSpeed as speedFactor
```

**After:**
```java
config.set("maxSpeed", maxSpeed);
config.set("speedFactor", speedFactor);
```

**Impact:** On first install, the config would have swapped values, making the light orb behave unpredictably (too fast or too slow). Existing servers with manually-tuned configs are not affected since `writeDefaultConfig()` only runs when the config section doesn't exist.

### 4. Fire Perk Game Mode

**File:** `FireHandler.java`

**Before:** `player.setGameMode(GameMode.SURVIVAL)` was called every time a player used `/perk fire`. This forcibly switched creative/adventure/spectator players to survival — a destructive side effect that undoes admin mode.

**Why it existed:** Probably a workaround for fire resistance not applying in creative. But creative mode already makes players immune to fire damage, so the forced switch was both unnecessary and harmful.

**Fix:** Removed the `setGameMode()` call and the unused `GameMode` import.

### 5. Enhanced Name Tag Perk — Tier system with permissions

**Files:** `NameTagPerk.java` (rewritten), `NameTagHandler.java` (new)

This is the largest change. The old name tag system applied a single hardcoded yellow color to all donors. The new system supports:

**Tier architecture:**
- Config-driven tiers defined in `config.yml` under `perks.name.tiers.*`
- Each tier has: `color` (ChatColor name), `prefix` (MiniMessage string), `suffix` (MiniMessage string)
- Per-tier permission nodes: `perks.name.<tier>` (e.g., `perks.name.gold`, `perks.name.diamond`)
- Default tier (yellow, no prefix/suffix) is available to all name tag perk users

**Why Scoreboard Teams:** Minecraft's overhead name tag color is controlled *only* through Scoreboard Teams. `player.displayName()` only affects chat, not the floating name above the player's head. The Teams API's `team.prefix(Component)` and `team.suffix(Component)` accept Adventure Components, which is how we render MiniMessage-formatted symbols.

**Why MiniMessage for config:** MiniMessage (`<gold>★ </gold>`) is Paper's modern text formatting standard. It's human-readable in YAML configs, supports all Adventure formatting options, and avoids the deprecated `ChatColor` + `§` format codes. Server admins can edit tier prefixes/suffixes without touching Java code.

**Rainbow animation:** Uses a `BukkitRunnable` that cycles through 7 `ChatColor` values every 10 ticks (0.5 seconds) via `team.setColor()`. The task self-cancels when no rainbow players are online, avoiding unnecessary tick consumption. It restarts automatically when a player selects the rainbow tier.

**State management:**
- `playerTiers: Map<UUID, String>` — tracks which tier each player has selected
- `rainbowPlayers: Set<UUID>` — tracks which players need rainbow cycling
- `tierTeams: Map<String, Team>` — caches created teams to avoid re-registering
- `disable()` properly cleans up: removes all players from teams, unregisters teams, clears all state, cancels the rainbow task

**Default config tiers:**
| Tier | Color | Prefix | Permission |
|------|-------|--------|------------|
| default | YELLOW | (none) | `perks.name` (base perk) |
| gold | GOLD | ★ | `perks.name.gold` |
| diamond | AQUA | ◆...◆ | `perks.name.diamond` |
| rainbow | animated | ✨...✨ | `perks.name.rainbow` |

**NameTagHandler (`/perk name [tier|off|info]`):**
- No args or `info`: Shows current tier and available tiers
- `<tier>`: Checks permission, applies tier, sends confirmation
- `off`: Removes name tag entirely
- Follows the existing handler pattern (`extends PerksCommandHandler`)

### 6. Potion Effect Balance — Configurable amplifiers

**Files:** `PotionEffectPerk.java`, `PerksPlugin.java`

**Before:** All three potion perks (speed, jump, ring) had a hardcoded default amplifier of 1 in `writeDefaultConfig()`. This meant Jump Boost II (amplifier 1) — barely noticeable — while Speed II was reasonable.

**Problem with hardcoded defaults:** The `PotionEffectData` constructor reads from config at runtime (`PerksPlugin.getPerkInt(name, "amplifier", 1)`), but `writeDefaultConfig()` always wrote `1` regardless. So the config would always say amplifier 1, and you'd have to manually edit it for every server.

**Solution:** Added a `defaultAmplifier` field to `PotionEffectPerk`. Each perk now passes its own sensible default:
- **Speed:** amplifier 1 (Speed II) — noticeable but not game-breaking
- **Jump:** amplifier 2 (Jump Boost III) — actually useful for parkour/exploration
- **Ring:** amplifier 0 (Invisibility I) — level doesn't matter for invisibility, but 0 is semantically correct

The old single-arg constructor delegates to the new one with `defaultAmplifier = 1` for backwards compatibility — existing code that doesn't specify an amplifier still compiles and works identically.

---

## Phase 3: In-Game Help System

**Files:** `HelpHandler.java` (new), `PerksCommandExecutor.java` (modified)

### The problem

The only way to learn perk commands was to read source code or ask another player. `/perk` with no args showed a raw list with no descriptions. There was no way to discover arguments, valid values, or see examples.

### Design decisions

**Why a single `/perk help` entry point:** Players already know `/perk`. Adding `/perk help` follows the existing subcommand pattern. No new top-level command to register, no new permission to manage.

**Why clickable text (FancyMessage):** Minecraft chat supports hover text and click-to-suggest through JSON chat components. PluginUtils already provides `FancyMessage` for this. Clickable commands mean players don't have to type — they click a command, it appears in their chat input, and they can modify arguments before sending.

**Permission-filtered display:** The help menu only shows commands the player has access to. Admin commands appear in a separate section only for admins. This prevents confusion ("why doesn't this command work?") and avoids exposing admin functionality to regular players.

**Dynamic registration:** `HelpHandler` receives the command map from `PerksCommandExecutor` — it doesn't maintain its own list. When a new perk is registered, it automatically appears in help. `getShortDescription()` and `getUsageDescription()` are abstract methods on `PerksCommandHandler`, so every handler is required to provide help text.

**Example commands per perk:** The `getExamples()` switch expression returns clickable example commands for each perk. These demonstrate valid arguments and common usage patterns. New cases are trivial to add.

---

## Phase 4: Documentation

All project documentation was updated to reflect the current state:

- **README.md** — Updated boat types, wolf variants, name tag command/tiers/permissions, potion amplifiers, help command section
- **ROADMAP.md** — Moved 10 completed items to "Completed" section, added new backlog items (resource pack badges, particle trails, CI, Maven wrapper)
- **Changes.md** — Full changelog for both the code audit and feature enhancements
- **CLAUDE.md** — Updated architecture patterns (help system, tab completion, name tag tiers, config defaults), key commands, and code conventions

---

## Summary of changes by file

| File | Type | Change |
|------|------|--------|
| `CompanionPerk.java` | Fix + Feature | Metadata guard, cleanup, wolf variant parameter |
| `CompanionHandler.java` | Fix + Feature | Cleanup, wolf variant UI and parsing |
| `VideoTeamCommand.java` | Fix | Safe cast with instanceof |
| `TabComplete.java` | Rewrite | Dynamic command registry, permission filtering |
| `PotionEffectPerk.java` | Fix + Feature | Removed dead task, configurable amplifiers |
| `BoatPerk.java` | Fix | Deprecated API replacement |
| `BoatHandler.java` | Fix + Feature | Reflection removal, all 10 boat types |
| `HorsePerk.java` | Fix | Deprecated API replacement |
| `JockeyPerk.java` | Fix | Implemented empty unjockey() |
| `PerkManager.java` | Fix | equalsIgnoreCase, unmodifiable set |
| `SitPerk.java` | Fix | Atomic remove, debug logging, cleanup |
| `ParrotHandler.java` | Fix | Specific catch type |
| `ParrotPerk.java` | Fix | Debug logging, unused import |
| `HttpTextInputHandler.java` | Fix | URL prefix order |
| `PerksPlugin.java` | Fix + Feature | Null checks, per-perk amplifiers |
| `CompassHandler.java` | Fix | System.out replacement |
| `BoatListener.java` | Fix | Cleanup (dead code, unused imports) |
| `HorseHandler.java` | Fix | Cleanup |
| `ItemPerk.java` | Fix | Unused import |
| `WizardLightPerk.java` | Fix | Swapped config values |
| `FireHandler.java` | Fix | Removed forced game mode |
| `NameTagPerk.java` | Rewrite | Tier-based system, rainbow, MiniMessage |
| `NameTagHandler.java` | New | `/perk name` command handler |
| `HelpHandler.java` | New | In-game help system |
| `PerksCommandExecutor.java` | Modified | Help handler integration |

**Total: 24 files changed, 2 new files, 25 bugs fixed, 6 features added.**
