# PowerPlugin

Minecraft Spigot plugin with `/power` command and 20 powers (including **Metal Jaw**), each with a **Base Form** and an **Awakening Form**.

## Commands
- `/power add <power> <player>`
- `/power remove <power> <player>`
- `/power awaken <power> <player>` (requires 100% mastery)
- `/power list <player>`

## Power Progression
- Each power has mastery from **0% to 100%**.
- At 100%, that power can be awakened with `/power awaken <power> <player>`.
- Rebalance note: powers were tuned to be more complete but less overpowered, with clearer base + awakening identity.

## Full Power Explanations

### 1) metal_jaw
- **Base Form:** Consume normal food and non-edible items (right-click) to gain mastery based on item rarity.
- **Awakening:** Consuming items grants improved healing/hunger sustain and brief regeneration.

### 2) wind_step
- **Base Form:** Gain mastery by sprinting.
- **Awakening:** Permanent speed boost with occasional jump mobility bursts while sprinting.

### 3) stone_skin
- **Base Form:** Gain mastery by taking damage.
- **Awakening:** Passive resistance and additional direct damage reduction.

### 4) flame_heart
- **Base Form:** Gain mastery through melee combat.
- **Awakening:** Hits inflict stronger burn effects and grant fire resistance.

### 5) tide_caller
- **Base Form:** Gain mastery by swimming.
- **Awakening:** Better water control with dolphins grace + conduit-style swimming support.

### 6) shadow_veil
- **Base Form:** Gain mastery while sneaking and moving stealthily.
- **Awakening:** Reliable invisibility support for stealth play.

### 7) thunder_pulse
- **Base Form:** Gain mastery from combat strikes.
- **Awakening:** Chance-based lightning pulse and bonus hit damage.

### 8) nature_touch
- **Base Form:** Gain mastery by breaking natural blocks (logs/plants/leaves).
- **Awakening:** Adds sustain utility with regeneration trigger moments during harvesting.

### 9) frost_bloom
- **Base Form:** Gain mastery by traveling across snow/ice terrain.
- **Awakening:** Temporary defensive cold-form resistance while traversing frozen areas.

### 10) sunforge
- **Base Form:** Gain mastery by mining overworld stone during daytime.
- **Awakening:** Persistent haste for improved daytime resource gathering.

### 11) moonlit_hunter
- **Base Form:** Gain mastery by dealing combat damage at night.
- **Awakening:** Night vision and stronger consistency in low-light fights.

### 12) arcane_echo
- **Base Form:** Gain mastery from XP collection.
- **Awakening:** Passive luck for utility-focused magical progression.

### 13) beast_bond
- **Base Form:** Gain mastery by interacting with animals.
- **Awakening:** Regeneration support and village synergy via hero effect.

### 14) void_glide
- **Base Form:** Gain mastery from fall-survival and aerial risk.
- **Awakening:** Reduced fall damage and passive slow-falling control.

### 15) crystal_mind
- **Base Form:** Gain mastery from amethyst/quartz crystal block interactions.
- **Awakening:** Burst haste windows for focused mining clarity.

### 16) gravity_well
- **Base Form:** Gain mastery from pressure combat and taking hits.
- **Awakening:** Controlled knockback dampening on enemies and jump-control mobility.

### 17) storm_eye
- **Base Form:** Gain mastery by fighting during storms.
- **Awakening:** Weather-adapted water breathing and charged storm combat bonus.

### 18) bloodrush
- **Base Form:** Gain mastery through aggressive combat.
- **Awakening:** Moderate low-health damage multiplier for clutch moments.

### 19) aether_shift
- **Base Form:** Gain mastery while gliding and from fall-risk movement.
- **Awakening:** Mobility-focused speed support and slight projectile evasive movement tech.

### 20) iron_stomach
- **Base Form:** Gain mastery from food consumption and survival pacing.
- **Awakening:** Saturation baseline and controlled absorption gain on consumption.

## Build
```bash
mvn package
```

Output JAR:
```bash
target/power-plugin-1.0.0.jar
```
