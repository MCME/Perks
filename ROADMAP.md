# Roadmap

## Completed

- [x] Add wolf variant support to companion perk (1.21.4 wolf variants)
- [x] Add more boat types (mangrove, cherry, bamboo, pale_oak) -- all 10 wood types
- [x] Add `/perk help <perkname>` for detailed per-perk help with clickable commands
- [x] Add `/perk list` command showing all perks available to the player (via `/perk help`)
- [x] Enhanced name tag perk with tier-based colors, prefix/suffix symbols, animated rainbow, per-tier permissions
- [x] Fix wizard light config bug (swapped maxSpeed/speedFactor defaults)
- [x] Fix fire perk forcing survival mode
- [x] Configurable potion effect amplifiers with saner defaults
- [x] Dynamic tab completion from command registry with permission filtering
- [x] Add `.gitignore` for `target/`, IDE files, etc.

## Backlog

### Code Health
- [ ] Add unit tests (JUnit 5 / MockBukkit)
- [ ] Replace deprecated `setPassenger()` call in `SitPerk.sitDown()`
- [ ] Replace deprecated `new URL()` in `HttpTextInputHandler` with `URI.create().toURL()`
- [ ] Migrate from legacy `ChatColor` to Adventure `Component` API across all perks
- [ ] Migrate from `setCustomName(String)` to `customName(Component)` across all perks
- [ ] Remove unused `instance` fields in `CompanionPerk`, `ParrotPerk`, `BoatPerk`, `HorsePerk`

### Features
- [ ] Add configurable cooldowns per perk
- [ ] Persist companion state across server restarts
- [ ] Resource pack custom badge icons for name tag tiers (font-based PUA characters)
- [ ] Particle trail perk (footstep particles while walking)
- [ ] Custom death messages perk (themed/funny death messages)
- [ ] Emote perk (`/perk emote wave` with particles and animations)
- [ ] Banner cape perk (banner on head slot as cosmetic cape)
- [ ] Music horn perk (play note block sounds)

### Infrastructure
- [ ] Set up GitHub Actions CI (build on push/PR)
- [ ] Publish releases to GitHub Releases with JAR artifact
- [ ] Add Maven wrapper (`mvnw`) for reproducible builds without global Maven
