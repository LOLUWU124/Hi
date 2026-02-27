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
- Gradle 8.10+ (older Gradle versions can fail with Fabric Loom API errors)
- Internet access to Maven repositories (Fabric + Maven Central)

### Command

```bash
JAVA_HOME=$HOME/.local/share/mise/installs/java/21.0.2 PATH=$JAVA_HOME/bin:$PATH gradle clean build
```

### Install Gradle and Maven

If you meant **how to install the build tools**:

- Ubuntu/Debian:

```bash
sudo apt update
sudo apt install -y gradle maven
```

- macOS (Homebrew):

```bash
brew install gradle maven
```

- Windows (winget):

```powershell
winget install Gradle.Gradle
winget install Apache.Maven
```

Check versions:

```bash
gradle -v
mvn -v
```

> This Fabric mod is built with **Gradle**. Maven is optional unless you want to publish/install the built jar into a local Maven repo.

### Install this mod artifact into local Maven (optional)

After building the jar, you can install it into local Maven cache:

```bash
mvn install:install-file   -Dfile=build/libs/hi-races-1.0.0.jar   -DgroupId=com.hi   -DartifactId=hi-races   -Dversion=1.0.0   -Dpackaging=jar
```


If you already have Java 21 selected globally, this also works:

```bash
gradle clean build
```

Output jar:

- `build/libs/hi-races-<version>.jar`


### Troubleshooting


### Fabric Loom setup (fix for your error)

You do **not** install Loom globally. Loom is a Gradle plugin loaded from `build.gradle`.

1. Make sure plugin version is stable (already set in this repo):

```groovy
plugins {
    id 'fabric-loom' version '1.7.4'
}
```

2. Make sure `settings.gradle` contains Fabric Maven in `pluginManagement.repositories`.
3. Use Java 21.
4. Use Gradle 8.10+ (or run with the project wrapper if present).
5. Build again: `gradle clean build`.

If you still get `Problems.forNamespace(...)`, your Gradle runtime is too old for Loom; upgrade Gradle and retry.

If build fails with:

```
Problems.forNamespace(java.lang.String)
```

Your Gradle runtime is too old for Fabric Loom. Upgrade to Gradle 8.10+ and rebuild.

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
