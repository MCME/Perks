# Contributing to MCME Perks

## Prerequisites

- Java 21+
- Maven
- A Paper 1.21.4+ test server
- [PluginUtils](https://github.com/MCME/PluginUtils) 1.9.0 installed on the server

## Building

```bash
mvn clean package
```

The JAR is output to `target/Perks-2.5.1.jar`. Copy it to your server's `plugins/` folder.

## Project Structure

```
src/main/java/com/mcmiddleearth/perks/
  PerksPlugin.java        # Main entry point, config loading, perk registration
  PerkManager.java        # Perk registry, listener/handler registration
  commands/               # One handler class per perk subcommand
  listeners/              # One listener class per perk's events
  perks/                  # Perk implementations (extend Perk.java)
  permissions/            # Permission system and donor tier logic
  tabCompleter/           # Tab completion
  utils/                  # HTTP utilities
```

## Adding a New Perk

1. **Create the perk class** in `perks/` extending `Perk`:
   ```java
   public class MyPerk extends Perk {
       public MyPerk() {
           super("myperk");
           setListener(new MyPerkListener());
           setCommandHandler(new MyPerkHandler(this, Permissions.USER.getPermissionNode()), "myperk");
       }
       @Override public void disable() { /* cleanup */ }
       @Override public void check() { /* permission check */ }
   }
   ```

2. **Create the command handler** in `commands/` extending `PerksCommandHandler`:
   - Implement `getShortDescription()`, `getUsageDescription()`, and `execute()`

3. **Create the listener** in `listeners/` implementing Bukkit `Listener`:
   - Handle relevant events (spawn, interact, quit, kick, death, etc.)

4. **Register the perk** in `PerksPlugin.onEnable()`:
   ```java
   PerkManager.addPerk(new MyPerk());
   ```

5. **Add default config** by overriding `writeDefaultConfig()` in your perk class.

6. **Add tab completion** in `TabComplete.java` by adding your perk name to the `PERK_NAMES` array.

## Conventions

- Each perk has its own handler, listener, and perk class
- Clean up spawned entities on player quit/kick/death/teleport
- Use `PermissionData.isAllowed()` for permission checks
- Use `PerksPlugin.getMessageUtil()` for player messages
- Use `instanceof` checks before casting `CommandSender` to `Player`
- Add null checks when accessing config sections

## Branching

- `master` - stable releases
- `development` - integration branch
- Feature branches off `development`

## Pull Requests

- Target `development`, not `master`
- Compile before submitting (`mvn clean package`)
- Test on a server with at least one other player for multiplayer perks
- Fill out the PR template
