# Changelog

All notable changes to the **WorldExplorerMod (mc-3ver)** project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/), and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.8.4] - 2026-10-31

### ✨ Added

- New biome **Pear Grove** (work in progress): replaces part of the Flower Forests via TerraBlender. Warm golden-green grass, turquoise water, meadow music, pear trees and meadow flowers. Now requires TerraBlender.

## [0.8.3] - 2026-10-31

### 🔧 Internal

- Pear Grove (work in progress): `GroveZoneLogic` splits the grove into edge, groups, dense forest and heart and sets tree density per zone.

## [0.8.2] - 2026-10-31

### 🔧 Internal

- Pear Grove (work in progress): TerraBlender dependency for overworld biomes, `GroveCenterLogic` places grove centers and the old pear tree.

## [0.8.1] - 2026-10-31

### 🐛 Bug Fixes
* **Pear wood and explosions:** Pear logs, wood, planks and saplings always dropped when blown up. Now some are lost, like oak.

### 🔧 Internal
* Fabric datagen now generates the loot tables of the pear wood set instead of hand-written files.
* New automated checks: consistent formatting (Spotless), Error Prone, architecture rules (ArchUnit), resource completeness, project rules (Semgrep) and weekly mutation testing (PIT). The version lives only in `gradle.properties`.
* GitHub Actions updated to Node 24 versions. `setup-gradle` stays on v5 because v6 makes caching proprietary under Gradle's terms of use. Dependabot ignores v6.
* Release and Modrinth upload read the Minecraft version from `gradle.properties` instead of a fixed `26.2`.
* Spotless 8.10.4, JUnit 6.1.3, ArchUnit 1.5.1, Error Prone plugin 5.1.1, foojay-resolver 1.0.0.
* `runClient` starts with the fixed player name `Frank`. Since 26.3 a singleplayer world belongs to a fixed player UUID; changing dev names lost inventory and position.

---

## [0.8.0] - 2026-10-31

### ✨ Added
* **Minecraft 26.3:** The mod now runs on Minecraft 26.3 with Fabric Loader 0.20 and Fabric API 0.165. Version 0.7.1 stays available for Minecraft 26.2.

### 🐛 Bug Fixes
* **Transport map:** On 26.3 the map showed the purple-black missing texture in the inventory because vanilla removed the `filled_map_markings` texture. The model now uses only `filled_map`. A new test checks that every texture and parent model our models reference exists.

### 🔧 Internal
* Pear tree worldgen moved to the new `worldgen/feature` format, loot tables to the new condition format (`condition`, `modifier`).
* Pear tree items get composting and fuel through the new item components, stripping through Fabric API's `BlockTransformerHelper`. Burn times and compost chances are unchanged.
* Gradle 9.8.2 and Loom 1.19.3. Branch `mc/26.2` keeps the 26.2 state.

---

## [0.7.1] - 2026-10-28

### 🐛 Bug Fixes
* **Translated dimension names:** The transport map tooltip showed "Overworld" or "The End" in every language. It now uses the translated names. The friendship stone action bar also shows just the dimension name, for example "Frank: Nether".

### 🔧 Internal
* Cleanup: removed the dead `loot_tables/` folder and two unused classes, one shared base for transport map and friendship stone, one source for the 16 dye colors, client setup split by feature, lightning wand and pear wood registration broken into small steps. Gameplay is unchanged.

---

## [0.7.0] - 2026-10-27

### ✨ Added
* **Friendship Stone:** New item for LAN parties. Sneak + right-click binds an unbound stone to you. It is then called "Friendship Stone of <Name>" and shows a heart icon. Give the bound stone to a friend.
* **Teleport:** Hold right-click for 2.5 seconds to teleport next to the player who bound the stone. Works in the Nether and the End. Cooldown is 30 seconds. Taking a hit during channeling cancels the teleport, and this now applies to the transport map too.
* **Notification:** The player who bound the stone receives a message that someone is coming. If they are offline, nothing happens.
* **Action bar:** While holding the stone, the action bar shows distance in blocks and compass direction to the friend. If the friend is in another dimension, the dimension is shown instead.
* **Revoke:** Sneak + right-click on one of your own bound stones revokes all stones you ever handed out.
* **Recipe:** 4 amethyst shards around an ender pearl make 2 stones. Unbound stones stack to 16.

---

## [0.6.3] - 2026-10-25

### ✨ Added
* **Recover a lost Transport Map:** Keep right-click pressed on the flame with an empty hand for 1.5 seconds. A progress bar above the hotbar shows how far along you are, then you get a free new map linked to this flame in its original color. Players who already carry a map for the flame, also inside a bundle, get no second one.
* **Name on placement:** After placing a flame, a small input field asks for its name. The name appears on the map and on every map recovered later. If you close the field without a name, right-clicking the flame with an empty hand opens it again. The first name is final and only the player who placed the flame may set it. A flame item renamed at the anvil takes over its name directly. After that, renaming works as before only on the map at the anvil.

---

## [0.6.0] - 2026-10-25

### ✨ Added
* **Bonfire:** A large campfire covering a 3x3 area. The woodpile consists of two crosswise layers at the base with eight logs leaning against them in a teepee shape. Five flickering flame tongues rise from the top, the tallest reaching nearly 4 blocks high. They glow fully at night and scale up with distance. While lit, the fire emits light level 15 and deals fire damage.
* **Smoke plume:** Individual smoke puffs rise 64 blocks into the air, growing larger and more transparent as they climb, and drift sideways in the wind. The plume stays visible across the full render distance. Wind shifts slowly over several minutes and is the same for all players.
* **Smoke color:** Right-clicking the fire with any of the 16 dyes colors the plume and puffs. The dye is consumed (not in creative mode). Colors are slightly brightened so dark shades remain visible against the sky. The color is lost when the fire is broken.
* **Controls:** Right-click on flat ground with a free 3x3 area to place the fire already lit. A shovel or water bucket extinguishes it, flint and steel or fire charge relights it. Breaking any block of the fire removes the whole structure and returns the item.
* **Recipe:** Campfire in the center, logs in the corners, coal or charcoal on the sides.

---

## [0.5.7] - 2026-10-25

### ⚖️ Balancing
* **Pear leaves drop like oak leaves:** Saplings (5%), sticks (2%) and pears (0.5%, like apples) now drop as often as from vanilla trees; before, they dropped 2 to 12 times more often. Fortune raises the chances as in vanilla, and Silk Touch harvests the leaves themselves.

---

## [0.5.6] - 2026-10-25

### ✨ New Features
* **White Flag (pacifist mode):** Right-click raises the flag and grants the permanent *Pacifist* effect. Monsters no longer target you and deal no damage, not even through stray arrows or explosions. Right-click again to lower the flag.
* **Breaking the peace:** Attacking a monster while the effect is active removes it and locks the flag for 30 seconds. Hitting animals or players does not break the peace.
* **Recipe:** 3 sticks as a left column, 4 white wool to the right.

---

## [0.5.5] - 2026-10-25

### 🐛 Bug Fixes
* **Rainproof tent roof:** Minecraft did not treat the thin roof ridge as solid, so rain fell onto the sleeping bag. The tent canvas now stops rain, and splashes no longer show through the thin roof from inside.

---

## [0.5.4] - 2026-10-24

### ✨ Tent Makeover
* **Seams and darker roof:** Each canvas panel shows a fold and a stitched seam. The roof is one shade darker than the walls.
* **Hanging lantern:** A lantern hangs under the roof slope above the crafting table and lights the tent with light level 14.
* **Tent flap:** The entrance has a flap in the tent color. Right-click rolls it up or closes it, and a closed flap keeps mobs out. New tents start open.
* **Tent chest:** A 27-slot chest stands on the right next to the foot of the sleeping bag; its lid flips open with the chest sound. Packing moves its contents into the tent item, pitching restores them. Re-dyeing keeps the contents and applies the exact dye color.
* **Stakes and guy lines:** Guy lines at all four corners anchor the tent walls to stakes in the ground.
* **Pennant:** A pennant in the tent color flutters on the front gable, so the tent is easy to spot from afar.

---

## [0.5.3] - 2026-10-24

### ✨ New Features
* **Tent Furnace:** A fully functional furnace sits on the left tent wall next to the foot of the sleeping bag. It uses original furnace textures with a tent-colored back wall, yields experience, and emits light level 13 while burning.
* **Stovepipe:** A dark metal stovepipe with a rain cap extends above the furnace through the sloped roof to the outside. Smoke rises from the pipe whenever the furnace is burning.
* **Tent Packing:** Packing the tent via sneak right-click drops all furnace contents and stored experience. Previously placed tents receive the furnace only after tearing down and rebuilding them.

---

## [0.5.2] - 2026-10-24

### 🐛 Bug Fixes
* **Sleeping in explorer tents:** Sleeping bags now inherit from `BedBlock`. Vanilla no longer wakes players up on the first tick.
* **Home spawn point:** Sleeping in a tent no longer changes the spawn point, neither during the day nor at night. The Fabric API event `EntitySleepEvents.ALLOW_SETTING_SPAWN` prevents spawn overrides and suppresses the chat message entirely.

---

## [0.5.1] - 2026-09-13

### 🔧 Maintenance & Dependency Updates
* **Fabric Loader:** Updated to `0.19.5` (was `0.19.3`).
* **JUnit Jupiter:** Test framework updated to `5.12.2` (was `5.10.2`).
* Verified all mod classes, mixins, and unit tests with Java 25.

---

## [0.5.0] - 2026-09-06

### ✨ Added & Improvements
* **Mobile Explorer Tent (`mc3ver:explorer_tent`):**
  * Deployable shelter designed for wilderness exploration and traveling.
  * **Integrated Sleeping Bag & Crafting Table:** Automatically deploys a sleeping mat and a fully functional crafting table at the head end.
  * **16 Dyeable Colors:** Craftable with any wool color and dyeable with any Minecraft dye.
  * **Custom 3D Geometry:** Tuned canvas angles with standing room in the central aisle, flush back wall, centered front entrance, and accurate collision shapes.
  * **Packable:** Shift-right-click any tent block to instantly pack up the entire tent back into a single item.
* **Documentation & Recipe Icons:**
  * Updated web documentation with authentic Minecraft inventory textures for all crafting recipes.
  * Added tent recipe card and highlight feature section to documentation page.

---

## [0.4.3] - 2026-09-05

### ✨ Added & Fixed
* **Pear Wood Boats (`mc3ver:pear_boat` & `mc3ver:pear_chest_boat`):**
  * Boats can now be crafted using 5x Pear Planks (`mc3ver:pear_planks`).
  * Pear Chest Boats can be crafted by combining a chest and a pear boat.
  * Full integration with entity and item tags (`#minecraft:boats`, `#minecraft:chest_boats`, `#minecraft:boat`), furnace fuel (60 seconds), and dispenser behavior.
  * Custom pixel-art textures and client-side boat renderers.

---

## [0.4.2] - 2026-09-05

### ✨ Improvements & Bug Fixes
* **Bundle Support for Transport Maps:**
  * Transport Maps now have a max stack size of 64 (consistent with vanilla maps).
  * Each Transport Map now occupies only 1/64 of a bundle, allowing up to 64 maps to be stored together inside a single bundle.
* **Smart Re-linking of Transport Maps:**
  * Breaking and placing a Transport Flame no longer creates redundant or useless duplicate maps.
  * Any extinguished Transport Map in the player's inventory (including inside Bundles) automatically re-links to the newly placed flame while preserving its original color and ID.
  * A new map is only granted if the player no longer has the old map in their possession.

---

## [0.4.1] - 2026-09-05

### ✨ Added
* **Golden Pear / Explorer's Pear (`mc3ver:golden_pear`):**
  * **Consumption Effects:**
    * **Jump Boost II** for 60 seconds &rarr; enables **Triple Jump**!
    * **Speed II** for 60 seconds for high mobility.
    * **Slow Falling I** for 45 seconds to prevent fall damage.
  * **Food Properties:** 4 nutrition (2 hunger shanks), 1.2 saturation modifier, edible even when full (`alwaysEdible`).
  * **Item Rarity:** `RARE` (aqua item name).
  * **Crafting Recipes:**
    * Shaped recipe: 1x Pear in center surrounded by 8x Gold Ingots.
    * Shapeless recipe: 1x Pear + 8x Gold Ingots anywhere in the crafting grid.
* **Expanded Mobility Mechanics (Triple Jump):**
  * Players with Jump Boost (`MobEffects.JUMP_BOOST`) can perform up to 2 extra mid-air jumps (Double Jump & Triple Jump).
* **Website & Recipe Documentation:**
  * Added Golden Pear crafting table recipe visualization to GitHub Pages.

---

## [0.4.0] - 2026-09-05

### ✨ Added
* **Complete Pear Wood Set (Swiss Pear Wood Aesthetic):**
  * **8 New Blocks & Items:**
    * **Pear Stairs (`mc3ver:pear_stairs`)**
    * **Pear Slab (`mc3ver:pear_slab`)**
    * **Pear Fence (`mc3ver:pear_fence`)**
    * **Pear Fence Gate (`mc3ver:pear_fence_gate`)**
    * **Pear Door (`mc3ver:pear_door`)** with stylish window cutout
    * **Pear Trapdoor (`mc3ver:pear_trapdoor`)**
    * **Pear Pressure Plate (`mc3ver:pear_pressure_plate`)**
    * **Pear Button (`mc3ver:pear_button`)**
  * **9 Bespoke 16x16 Pixel-Art Textures:**
    * Refined, warm Swiss pear wood color palette for planks, logs, stripped logs, doors, and trapdoors.
  * **Full Wood System Integration:**
    * Crafting recipes for all blocks and items.
    * Integrated into all standard Minecraft block and item tags (`wooden_doors`, `wooden_stairs`, `wooden_slabs`, `wooden_fences`, `fence_gates`, etc.).
    * Accurate loot tables and acoustic block sound types.
* **Visually Distinct Pear Sapling (`mc3ver:pear_sapling`):**
  * Dedicated 16x16 pixel-art texture featuring the distinct foliage shade of mature pear trees and unique pear-shaped crown to easily tell them apart from oak saplings.
* **Website & Crafting Documentation:**
  * Added visual workbench crafting recipe grids for Transport Flame, Wind Wand, and Chain Lightning Wand on GitHub Pages.

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
