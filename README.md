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

## Build (mod jar)
```bash
./gradlew build
```

Expected output:
```bash
build/libs/powers-mod-1.0.0.jar
```
