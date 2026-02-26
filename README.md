# Powers Mod (Minecraft 1.21 Fabric)

This project is now a **mod**, not a Bukkit/Spigot plugin.

## What changed
- Rebuilt from plugin architecture to Fabric 1.21 mod architecture.
- Replaced `/power` handling with Brigadier commands registered through Fabric API.
- Removed `plugin.yml`/`JavaPlugin` usage and added `fabric.mod.json` + Gradle Loom build.

## Commands
- `/power add <power> <player>`
- `/power remove <power> <player>`
- `/power awaken <power> <player>`
- `/power list <player>`

## Powers
The 30 superhero power keys are still available through `PowerType` (including `metal_jaw` and 29 remade powers).

## Build (Windows + Linux/macOS)

### Windows (PowerShell)
```powershell
# Make sure Java 21 is installed and selected
java -version

# If you have wrapper files:
.\gradlew.bat build

# If wrapper files are missing, use local Gradle install:
gradle build
```

### Linux/macOS
```bash
# If wrapper files exist
./gradlew build

# If wrapper files are missing
gradle build
```

## Common errors and fixes
- **`'gradlew' is not recognized` / `No such file or directory`**
  - This means wrapper scripts are missing. Use `gradle build` instead.
- **`Unsupported class file major version`**
  - Use **Java 21** for Gradle and the project.
- **Fabric plugin resolution errors**
  - Check internet access and firewall/proxy settings for:
    - `https://maven.fabricmc.net/`
    - `https://services.gradle.org/`
    - `https://repo.maven.apache.org/`

Expected output:
```text
build/libs/powers-mod-1.0.0.jar
```
