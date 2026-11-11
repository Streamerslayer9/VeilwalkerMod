# Veilwalker (Fabric, Minecraft 1.21.1)

## Content
- **Veil Ore** - generates in the Overworld (y -60 to 30). Needs an iron pickaxe or better. Drops 1-2 Veil Shards
  (Fortune works, Silk Touch keeps the ore). Also smeltable.
- **Veil Shard** - the core crafting material.
- **Block of Veil Shards** - 9 shards <-> 1 block.
- **Echo Lantern** - bright (light 14) decorative block. Recipe yields 2.
- **Echo Anchor** - Sneak + right-click binds a spot in any dimension; right-click blinks you back (cross-dimension,
  3s cooldown, 48 uses).
- **Phase Blade** - diamond-tier sword. 25% per hit to teleport behind the target, slowing and highlighting them
  and giving you a burst of Speed.
- A dedicated **Veilwalker** creative tab.

## Recipes
- Echo Anchor: echo shard top/bottom, veil shards left/right, ender pearl center.
- Phase Blade (shapeless): diamond sword + 2 veil shards + echo shard + ender pearl.
- Echo Lantern: veil shards in the corners, glowstone dust on the edges, echo shard center (makes 2).

## Build with GitHub
1. Push this folder to a GitHub repo (files at repo root).
2. Actions tab -> "Build Mod" runs automatically -> download artifact `veilwalker-jar` (use the jar without `-sources`).
3. Put it in `.minecraft/mods` with Fabric Loader 0.16+ and Fabric API for 1.21.1.

Tag `v1.1.0` to attach the jar to a GitHub Release.
