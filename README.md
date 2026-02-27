# Hi Races (Fabric 1.21)

A Fabric server mod that adds a `/race` admin command and six custom races:

- `giller`
- `nightling`
- `metaljaw`
- `fireborn`
- `swiftling`
- `phantom`

## Build

### Requirements

- Java 21
- Gradle 8+
- Internet access to Maven repositories (Fabric + Maven Central)

### Command

```bash
JAVA_HOME=$HOME/.local/share/mise/installs/java/21.0.2 PATH=$JAVA_HOME/bin:$PATH gradle build
```

If you already have Java 21 selected globally, this also works:

```bash
gradle build
```

Output jar:

- `build/libs/hi-races-<version>.jar`

## Install on server

1. Install Fabric Loader for Minecraft 1.21.
2. Put Fabric API in the server `mods/` folder.
3. Put this mod jar in the server `mods/` folder.
4. Start the server.

## Usage

`/race <player> <raceName>`

Examples:

- `/race Steve giller`
- `/race Alex nightling`
- `/race Bob metal-jaw` (normalization handles dashes/spaces/underscores)

The command includes suggestion completion and typo autocorrect.
