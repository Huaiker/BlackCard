# BlackCard - Minecraft 1.20.1 Forge

[![Minecraft 1.20.1](https://img.shields.io/badge/Minecraft-1.20.1-green?logo=minecraft)](https://www.minecraft.net/)
[![Forge 47.4.13+](https://img.shields.io/badge/Forge-47.4.13%2B-orange)](https://files.minecraftforge.net/)
[![Version 2.0.0](https://img.shields.io/badge/Version-2.0.0-blue)]()

A universal item voucher mod — Black Cards can craft any item! Supports multi-item binding, blacklist filtering, and Cell⁴ integration.

[中文文档](README_zh.md)

---

## Card Types

### 1. Item Black Card (`black_card`)

Binds one or more specific items. When multiple items are bound, **Shift + Scroll** to cycle through them.

```mcfunction
# Single item
/give @p blackcard:black_card{bcitem:"minecraft:diamond",targetCount:1}

# Multiple items (list format, aligned with Cell⁴)
/give @p blackcard:black_card{bcitem:["minecraft:diamond","minecraft:iron_ingot"],targetCount:1}

# Single blacklist item (string format)
/give @p blackcard:black_card{bcitem:"minecraft:diamond",targetCount:1,bcblacklist:"minecraft:bedrock"}

# Multiple blacklist items (list format)
/give @p blackcard:black_card{bcitem:["minecraft:diamond","minecraft:iron_ingot","minecraft:gold_ingot"],targetCount:1,bcblacklist:["minecraft:bedrock","minecraft:command_block","minecraft:barrier"]}
```

### 2. Tag Black Card (`tag_black_card`)

Binds one or more item tags. **Shift + Scroll** to cycle through items within the tag.

```mcfunction
# Single tag
/give @p blackcard:tag_black_card{bctag:"minecraft:logs",targetCount:1}

# Multiple tags
/give @p blackcard:tag_black_card{bctag:["minecraft:logs","forge:ingots/iron"],targetCount:1}

# Single blacklist item (string format)
/give @p blackcard:tag_black_card{bctag:"minecraft:logs",targetCount:1,bcblacklist:"minecraft:crimson_stem"}

# Multiple blacklist items (list format)
/give @p blackcard:tag_black_card{bctag:"minecraft:logs",targetCount:1,bcblacklist:["minecraft:crimson_stem","minecraft:warped_stem","minecraft:stripped_crimson_stem"]}
```

### 3. Mod Black Card (`mod_black_card`)

Binds one or more mod IDs. **Shift + Scroll** to cycle through items from the mod.

```mcfunction
# Single mod
/give @p blackcard:mod_black_card{bcmodid:"mekanism",targetCount:1}

# Multiple mods
/give @p blackcard:mod_black_card{bcmodid:["mekanism","thermal"],targetCount:1}

# Single blacklist item (string format)
/give @p blackcard:mod_black_card{bcmodid:"mekanism",targetCount:1,bcblacklist:"mekanism:creative_bin"}

# Multiple blacklist items (list format)
/give @p blackcard:mod_black_card{bcmodid:"mekanism",targetCount:1,bcblacklist:["mekanism:creative_bin","mekanism:creative_energy_cube","mekanism:creative_fluid_tank"]}
```

---

## NBT Key Reference

| Key | Description | Cell⁴ Equivalent |
|:---:|:---|:---:|
| `bcitem` | Item ID (string or list) | `cell4item` |
| `bctag` | Tag ID (string or list) | `cell4tag` |
| `bcmodid` | Mod ID (string or list) | `cell4modid` |
| `bcblacklist` | Blacklisted item IDs (string or list) | `cell4blacklist` |
| `targetCount` | Crafting output count (int, default 1) | — |
| `currentItemIndex` | Current cycle index (all cards with multi-value) | — |
| `targetItem` | Currently selected item ID (auto-synced) | — |

> All identifier keys support both **single string** and **string list** formats, fully consistent with Cell⁴'s NBT format.

---

## Blacklist

All card types support the `bcblacklist` key to exclude specific items. The blacklist accepts both **single string** and **string list** formats — just like `bcitem`/`bctag`/`bcmodid`.

- **Skipped** during crafting — blacklisted items will never be produced
- **Excluded** from cycling on all cards with multi-value bindings
- Displayed with red markers in tooltips

```mcfunction
# ─── Single blacklist item (string format) ───

# Item Black Card: exclude one item
/give @p blackcard:black_card{bcitem:"minecraft:diamond",bcblacklist:"minecraft:bedrock"}

# Tag Black Card: exclude one item from a tag
/give @p blackcard:tag_black_card{bctag:"minecraft:logs",bcblacklist:"minecraft:crimson_stem"}

# Mod Black Card: exclude one item from a mod
/give @p blackcard:mod_black_card{bcmodid:"mekanism",bcblacklist:"mekanism:creative_bin"}

# ─── Multiple blacklist items (list format) ───

# Item Black Card: exclude multiple items
/give @p blackcard:black_card{bcitem:["minecraft:diamond","minecraft:iron_ingot","minecraft:gold_ingot"],bcblacklist:["minecraft:bedrock","minecraft:command_block","minecraft:barrier"]}

# Tag Black Card: exclude multiple items from a tag
/give @p blackcard:tag_black_card{bctag:"minecraft:logs",bcblacklist:["minecraft:crimson_stem","minecraft:warped_stem","minecraft:stripped_crimson_stem","minecraft:stripped_warped_stem"]}

# Mod Black Card: exclude multiple items from a mod
/give @p blackcard:mod_black_card{bcmodid:"mekanism",bcblacklist:["mekanism:creative_bin","mekanism:creative_energy_cube","mekanism:creative_fluid_tank"]}

# ─── Combined: multi-item binding + multi-blacklist ───

# Multiple items with multiple blacklisted items
/give @p blackcard:black_card{bcitem:["minecraft:diamond","minecraft:bedrock","minecraft:iron_ingot","minecraft:command_block"],bcblacklist:["minecraft:bedrock","minecraft:command_block"]}

# Multiple mods with multiple blacklisted items across mods
/give @p blackcard:mod_black_card{bcmodid:["mekanism","thermal"],bcblacklist:["mekanism:creative_bin","mekanism:creative_energy_cube","thermal:device_rock_gen","thermal:device_water_gen"]}
```

---

## Cell⁴ Integration

### Crafting Recipes

Black Card + AE2 Item Cell Housing (`ae2:item_cell_housing`) → Cell⁴ Infinity Cell

| Input | + | → Output |
|:---|:---:|:---|
| Black Card | AE2 Cell Housing | Cell⁴ Infinity Item Cell |
| Tag Black Card | AE2 Cell Housing | Cell⁴ Infinity Tag Cell |
| Mod Black Card | AE2 Cell Housing | Cell⁴ Infinity ModID Cell |

### NBT Auto-Conversion

During crafting, BlackCard NBT is automatically converted to Cell⁴ format:

| BlackCard | Cell⁴ | Description |
|:---:|:---:|:---|
| `bcitem` | `cell4item` | Item identifiers |
| `bctag` | `cell4tag` | Tag identifiers |
| `bcmodid` | `cell4modid` | Mod ID identifiers |
| `bcblacklist` | `cell4blacklist` | Blacklist |
| `targetCount` | Discarded | Not needed for infinity cells |

### Required Mods

- **AE2** v15.0.0+ — provides `item_cell_housing`
- **Cell⁴** v1.0.0+ — provides infinity cells

---

## Usage

1. **Craft**: Place a Black Card alone in the crafting grid to produce the specified item (card is not consumed)
2. **Cycle**: Hold a Black Card (multi-item), Tag Black Card, or Mod Black Card and **Shift + Scroll** to switch items
3. **Integrate**: Black Card + AE2 Cell Housing → Cell⁴ Infinity Cell

---

## Dependencies

| Mod | Required | Version |
|:---:|:---:|:---|
| Minecraft | Yes | 1.20.1 |
| Forge | Yes | 47.4.13+ |
| JEI | No | 15+ |
| AE2 | No | 15.0.0–16.0.0 |
| Cell⁴ | No | 1.0.0+ |

---

## Languages

English · 简体中文 · 日本語 · 한국어

---

## Configuration

| Option | Default | Description |
|:---|:---:|:---|
| `enableCustomDisplayName` | `true` | On: display "[Item] Black Card"; Off: display "Black Card" only |

Config file: `config/blackcard-common.toml`

---

## License

MIT
