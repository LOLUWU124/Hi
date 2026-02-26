# PowerPlugin

Spigot 1.21 plugin with `/power` and **30 superhero-style powers** (keeping `metal_jaw`, remaking the other 29).

## Commands
- `/power add <power> <player>`
- `/power remove <power> <player>`
- `/power awaken <power> <player>`
- `/power list <player>`

## Awakening Difficulty + Reward
- Mastery gain is globally slowed down (about **55%** of raw event gain), so awakening is harder to unlock.
- Every power now has a strong awakened identity through passive buffs and/or special combat/mobility procs.

## Power List (Base + Awakening)
1. **metal_jaw** — Base: eat non-food; Awakened: high regen sustain.
2. **sky_sentinel** — Base: hero flight enable; Awakened: flight + speed sky dominance.
3. **titan_guard** — Base: tank reduction; Awakened: much stronger mitigation.
4. **blaze_runner** — Base: fire-speed sprinting; Awakened: super speed trail + stronger fire play.
5. **storm_lancer** — Base: storm bonus strikes; Awakened: stronger charge + storm mobility.
6. **phase_stepper** — Base: portal-step combat; Awakened: faster evasive movement.
7. **ocean_warden** — Base: swim control; Awakened: elite water mobility.
8. **frost_knight** — Base: cold resistance fighting; Awakened: hardened frost defense.
9. **shadow_specter** — Base: stealth invis bursts; Awakened: stable stealth uptime.
10. **solar_monk** — Base: daylight haste style; Awakened: stronger work/combat speed.
11. **lunar_assassin** — Base: night bonus damage; Awakened: lethal night burst.
12. **arc_puncher** — Base: charged melee hits; Awakened: strength-enhanced brawling.
13. **earthshaker** — Base: fall impact shock; Awakened: heavier jump/fall control.
14. **iron_heart** — Base: sustain from consumption; Awakened: constant regen durability.
15. **toxic_veil** — Base: poison resistance; Awakened: anti-debuff tanking.
16. **thorn_guardian** — Base: punishes melee pressure; Awakened: stronger defensive core.
17. **crystal_archer** — Base: crystal haste precision; Awakened: amplified crystal speed.
18. **nether_reaver** — Base: nether/fire adaptation; Awakened: persistent infernal resistance.
19. **aether_wing** — Base: glide control; Awakened: superior aerial handling.
20. **bulwark** — Base: shield-like mitigation; Awakened: fortress-level resistance.
21. **wild_caller** — Base: nature/animal regen; Awakened: stronger natural sustain.
22. **light_bringer** — Base: radiant vision support; Awakened: empowered utility aura.
23. **void_walker** — Base: portal mobility combat; Awakened: faster dimensional movement.
24. **thunderclap** — Base: shockwave hits; Awakened: lightning-impact burst.
25. **gearmind** — Base: repetition-based growth; Awakened: accelerated work tempo.
26. **starforged** — Base: momentum strikes; Awakened: stronger speed from cosmic momentum.
27. **phoenix_soul** — Base: fire survival clutch; Awakened: heat converts to healing.
28. **gravity_knight** — Base: knockback control; Awakened: force-lift enemy control.
29. **beacon_spirit** — Base: utility aura uptime; Awakened: stronger luck/vision aura.
30. **rift_blade** — Base: dimensional duelist; Awakened: amplified projectile evasion.


## Quality polish
- Added particle-rich visuals for major power triggers (flame trails, electric sparks, portal pulses, impact rings, light auras).
- Added smoother feel with movement throttling and short-duration stacked effects so powers feel responsive instead of jittery.
- Added thematic sounds on high-impact moments (fire/portal bursts) for stronger hero feedback.

## Build
```bash
mvn package
```

Output:
```bash
target/power-plugin-1.0.0.jar
```
