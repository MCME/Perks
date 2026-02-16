# Changes

## Feature Enhancements

### New Features

- **NameTagPerk.java** - Tier-based name tag system with configurable color, prefix/suffix symbols (MiniMessage), animated rainbow cycling, and per-tier permissions (`perks.name.<tier>`)
- **NameTagHandler.java** - New command handler for `/perk name [tier|off|info]` to let players choose their name tag style
- **HelpHandler.java** - New in-game help system with clickable commands: `/perk help` lists available perks, `/perk help <name>` shows detailed usage with examples
- **CompanionHandler.java** - Wolf variant support: `/perk pet dog Rex red ashen` spawns dogs with 1.21.4 wolf variants (pale, spotted, snowy, black, ashen, rusty, woods, chestnut, striped)
- **BoatHandler.java** - Added mangrove, cherry, bamboo, and pale_oak boat types (all 10 wood types now supported)

### Bug Fixes

- **WizardLightPerk.java** - Fixed swapped `maxSpeed`/`speedFactor` values in `writeDefaultConfig()`
- **FireHandler.java** - Removed forced `GameMode.SURVIVAL` that overrode the player's current game mode

### Balance

- **PotionEffectPerk.java** - Per-perk default amplifiers: Speed II, Jump Boost III, Invisibility I (previously all defaulted to amplifier 1)

### Improvements

- **TabComplete.java** - Dynamic tab completion from command registry with permission filtering
- **PerksCommandExecutor.java** - Integrated help handler; simplified command list display
- **CompanionPerk.java** - `spawnDog()` now accepts `Wolf.Variant` parameter

### Documentation

- **README.md** - Updated for all new features: boat types, wolf variants, name tag tiers/permissions, help command, potion balance
- **ROADMAP.md** - Moved completed items, added new backlog (resource pack badges, particle trails, emotes, CI, Maven wrapper)
- **CLAUDE.md** - Updated project structure and conventions for new handler/listener patterns

## Code Audit & Cleanup

### Critical Fixes

- **CompanionPerk.java** - Added empty-list check before `getMetadata().get(0)` to prevent `IndexOutOfBoundsException`
- **VideoTeamCommand.java** - Replaced unsafe `(Player)` cast with `instanceof Player` pattern matching; console usage no longer crashes
- **TabComplete.java** - Rewritten with `instanceof Player` guard, null-safe `PerkManager.forName()` checks, and loop over constant array
- **PotionEffectPerk.java** - Removed empty `BukkitRunnable` that was scheduled but never used (resource leak)
- **BoatPerk.java** - Replaced deprecated `getPassenger()` with `getPassengers()` and safe `instanceof Player` check
- **BoatHandler.java** - Replaced `Class.forName()` reflection with a `Map<String, Class>` whitelist of valid boat types

### Important Fixes

- **HorsePerk.java** - Replaced deprecated `getPassenger()` with `getPassengers()` and safe `instanceof Player` check
- **JockeyPerk.java** - Implemented empty `unjockey()` method body with `jockey.leaveVehicle()`
- **PerkManager.java** - Changed `.toLowerCase().equals()` to `.equalsIgnoreCase()`; `getPerks()` now returns `Collections.unmodifiableSet()`
- **SitPerk.java** - Changed `sitUp()` to use `armorStands.remove(player)` atomically to prevent memory leak
- **ParrotHandler.java** - Changed `catch(Exception)` to `catch(IllegalArgumentException)`
- **HttpTextInputHandler.java** - Fixed URL prefix bug: `url + "http://"` changed to `"http://" + url`
- **PerksPlugin.java** - Added null checks to all config section accesses in `arePerksEnabled`, `isPerkEnabled`, `enablePerk`, `enableAllPerks`
- **CompassHandler.java** - Replaced `System.out.println` with `cs.sendMessage()` for console output
- **SitPerk.java** - Removed 8 `Logger.getGlobal().info()` debug statements from hot path
- **ParrotPerk.java** - Removed `Logger.getGlobal().info()` debug statement from `checkParrot()`

### Code Quality

- **CompanionPerk.java** - Removed debug logging, 5 commented-out code blocks, unused `Logger` import, trailing whitespace
- **CompanionHandler.java** - Removed unused imports (`ParrotPerk`, `Variant`, `Location`, `World`), dead variables, trailing whitespace
- **ParrotHandler.java** - Removed trailing whitespace
- **ParrotPerk.java** - Removed unused `Logger` import, trailing whitespace
- **BoatListener.java** - Removed commented-out `boatSpawn` method, unused imports (`Logger`, `EntityType`, `CreatureSpawnEvent`, `EntitySpawnEvent`, `EventPriority`), debug comments
- **HorseHandler.java** - Removed commented-out debug lines, unused `ItemStack` import
- **HorsePerk.java** - Removed commented-out respawn code block in `spawn()`
- **SitPerk.java** - Removed unused `Bisected` import, commented-out code in `getConfigSection()`
- **ItemPerk.java** - Removed unused `Logger` import

### Documentation

- **README.md** - Complete rewrite with full documentation of all 16 perks, commands, permissions, donation tiers, configuration, and build instructions
- **CLAUDE.md** - Created development skeleton with build commands, project structure, architecture patterns, and code conventions
