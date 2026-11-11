# Veilwalker (Fabric, Minecraft 1.21.1)

## Content
- **Deepslate Veil Ore** - generates only in deepslate (y -60 to 5). Needs an iron pickaxe or better. Drops 1-2 Veil Shards
  (Fortune works, Silk Touch keeps the ore). Also smeltable.
- **Veil Shard** - the core crafting material.
- **Block of Veil Shards** - 9 shards <-> 1 block.
- **Echo Lantern** - bright (light 14) decorative block. Recipe yields 2.
- **Echo Anchor** - Sneak + right-click binds a spot in any dimension; right-click blinks you back (cross-dimension,
  3s cooldown, 48 uses).
- **Phase Blade** - diamond-tier sword. Right-click a creature within 10 blocks to teleport behind it: backstab
  (6 damage), slows and reveals the target, and gives you Speed. 2.5s cooldown. Left-click is a normal sword swing.
- A dedicated **Veilwalker** creative tab.

## Animations & effects (v1.3.0)
- **Animated textures** on every item and block: shard shimmer + twinkles, pulsing anchor with orbiting sparks,
  an energy pulse racing up the Phase Blade, breathing ore crystals, shimmering Veil Block, flickering Echo Lantern.
- **Ambient particles**: Veil Ore and Veil Blocks sparkle on exposed faces; Echo Lanterns release rising light motes.
  Ore, blocks and lanterns also give off light.
- **Echo Anchor**: binding sends out rings and a rising double spiral with chimes; blinking collapses rings into the
  veil, then erupts with expanding rings, a light burst and portal particles. A bound anchor glows and motes orbit you
  while you hold it.
- **Phase Blade**: enchant-glint always on, a mote drifts around you while held, normal hits leave veil dust, and a
  teleport leaves a streak of light, a sweep slash, crit sparks and layered sounds.

## Recipes
- Echo Anchor: echo shard top/bottom, veil shards left/right, ender pearl center.
- Phase Blade (shapeless): diamond sword + 2 veil shards + echo shard + ender pearl.
- Echo Lantern: veil shards in the corners, glowstone dust on the edges, echo shard center (makes 2).

## Build with GitHub
1. Push this folder to a GitHub repo (files at repo root).
2. Actions tab -> "Build Mod" runs automatically -> download artifact `veilwalker-jar` (use the jar without `-sources`).
3. Put it in `.minecraft/mods` with Fabric Loader 0.16+ and Fabric API for 1.21.1.

Tag `v1.3.0` to attach the jar to a GitHub Release.
