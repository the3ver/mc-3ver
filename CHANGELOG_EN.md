# Changelog

All notable changes to the **WorldExplorerMod (mc-3ver)** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [0.3.0] - 2026-09-03

### ✨ Added
* **Chain Lightning Wand (`mc3ver:lightning_wand`):**
  * **Chain Lightning Attack:** Strikes the targeted entity and chains to up to 4 additional living mobs within an 8-block radius.
  * **Hold-to-Charge Mechanic:**
    * Holding right-click charges the wand for up to 5 seconds (100 ticks).
    * Dynamic spark particles and rising electric crackle audio during charge-up.
    * Damage scales from **8.0 HP** (4 hearts) up to **24.0 HP** (12 hearts, 3x multiplier).
    * Durability loss scales linearly from 1 to 5 points.
  * **Authentic Lightning Effects (`thunderHit`):**
    * Creepers transform into **Charged Creepers** with electric blue energy aura.
    * Pigs transform into **Zombified Piglins**, villagers into **Witches**, and mooshrooms switch brown/red variants.
  * **Block Ignition & Interaction:**
    * Rays hitting flammable blocks (wood, foliage, wool, fences, etc.) ignite them in fire.
    * Extinguished campfires and candles are ignited.
    * TNT is instantly primed.
    * Lightning does not chain further from inanimate blocks.
  * **Crafting Recipes:**
    * Shaped: Lightning rod at top center, flanked by 2 amethyst shards, with a copper ingot and stick below.
    * Shapeless: Lightning rod, copper ingot, stick, and 2 amethyst shards in any configuration.
  * **Visuals & Audio:**
    * Custom 16x16 pixel-art texture and handheld item model.
    * Multi-part custom soundscape (charging hum, thunderclap, lightning strike, electric sparks).
* **Wind Wand (`mc3ver:wind_wand`):**
  * Unleashes focused bursts of compressed wind against foes with gust particles and punchy knockback sound effects.
  * Added shaped and shapeless crafting recipes.
* **Localization:** Complete German (`de_de.json`) and English (`en_us.json`) translations for all new items and messages.

---

## [0.2.1] - 2026-08-29

### 🐛 Fixed
* Dimension teleportation stability improvements.
* Release workflow bugfixes.

---

## [0.2.0] - 2026-08-29

### ✨ Added
* **Transport Flames & Teleportation Cards:**
  * Placing a Transport Flame automatically generates a linked teleport card in player inventory.
  * 2.5-second channeling safely teleports player back to the flame across dimensions (Overworld, Nether, The End).
* **Pear Tree:**
  * Pear wood logs, stripped logs, planks, leaves, and saplings.

---

## [0.1.1] - 2026-08-27

### 🔄 Changed
* Double jump particle effect optimizations.

---

## [0.1.0] - 2026-08-26

### ✨ Added
* **Double Jump:** Mid-air jump boost with localized wind burst effects.
* **Welcome Message:** Server/world join welcome notification displaying mod name and version.
