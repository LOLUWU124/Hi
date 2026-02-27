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
- Gradle 8.10+
- Internet access to Fabric/Maven repositories

### Build command

```bash
gradle clean build
```

Output jar will be in:

- `build/libs/hi-races-<version>.jar`

## Your Maven error: file does not exist

If Maven says:

```text
The specified file '...\build\libs\hi-races-1.0.0.jar' does not exist
```

it means the jar either was not built yet, or the filename/version is different.

### Fix steps (Windows PowerShell)

1. Build first:

```powershell
gradle clean build
```

2. Confirm actual jar name:

```powershell
Get-ChildItem .\build\libs\*.jar
```

3. Install using the exact file shown above:

```powershell
mvn install:install-file `
  -Dfile="C:\path\to\project\build\libs\hi-races-1.0.0.jar" `
  -DgroupId=com.hi `
  -DartifactId=hi-races `
  -Dversion=1.0.0 `
  -Dpackaging=jar
```

> If the built jar has another version (example `hi-races-1.0.1.jar`), update both `-Dfile` and `-Dversion`.

## Fabric Loom setup note

You do **not** install Loom globally. It is a Gradle plugin configured in `build.gradle`:

```groovy
plugins {
    id 'fabric-loom' version '1.7.4'
}
```

If you hit `Problems.forNamespace(...)`, update Gradle to 8.10+ and re-run build.

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
- `/race Bob metal-jaw`
