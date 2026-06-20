# Booster Powerful Pump

Booster Powerful Pump is a Minecraft Forge 1.20.1 Create addon.

It adds one Create-compatible block:

- `booster:powerful_mechanical_pump`

The Powerful Mechanical Pump extends Create's Mechanical Pump behavior, consumes higher stress, and applies higher pressure to Create's existing fluid pipe network.

## Environment

- Minecraft `1.20.1`
- Forge `47.1.33`
- Create `6.0.8`
- Java `17`
- Mod id: `booster`

## Build

```powershell
.\gradlew.bat build
```

The built jar is written to:

```text
build/libs/booster-1.20.1-0.1.0.jar
```

## Development Client

```powershell
.\gradlew.bat runClient
```

## Configuration

Server config file:

```text
booster-server.toml
```

Important keys:

- `powerfulPumpStressImpact`: default `16.0`
- `powerfulPumpPressureMultiplier`: default `8.0`

Existing worlds keep their own `serverconfig/booster-server.toml`; edit that file if you change defaults after a world has already been created.
